package org.meldr.compiler;

import java.io.File;
import java.net.URISyntaxException;
import java.util.*;

import org.meldr.compiler.MeldrLangParser.Rotation_declContext;

public class PythonBuilder extends MeldrLangBaseListener
{
    private String sceneName = "";
    private final ArrayList<BlenderObject> objects = new ArrayList<>();
    private BlenderObject currentObject;
    private String semanticError = "";

    // Set will contain objects properties for identifying duplicates
    private Set<String> currentProperties;

    // Scene will default to white plane
    private int level = 0;

    public String getSceneName() {
        return sceneName;
    }

    @Override
    public void enterScene(MeldrLangParser.SceneContext ctx)
    {
        //name for the python file!
        sceneName = ctx.IDENTIFIER().getText();
    }

    @Override
    public void enterObj_decl(MeldrLangParser.Obj_declContext ctx)
    {
        currentObject = new BlenderObject(ctx.IDENTIFIER().getText());
        currentProperties = new HashSet<>();
    }

    @Override
    public void exitObj_decl(MeldrLangParser.Obj_declContext ctx) 
    { 
        objects.add(currentObject);

        currentObject = null;
        currentProperties = null;
    }

    @Override
    public void enterObjectProperty(MeldrLangParser.ObjectPropertyContext ctx) {
        String propName = ctx.getChild(0).getChild(0).getText().toUpperCase();

        if (!currentProperties.contains(propName)) {
            // add property with null value for now
            currentProperties.add(propName);
        } else {
            semanticError += "ERROR [LINE " +  ctx.start.getLine() +  "]: Cannot define " + propName + " multiple times!\n";
        }
    }

    @Override
    public void enterModel_decl(MeldrLangParser.Model_declContext ctx) 
    {
        String modelType = ctx.IDENTIFIER().getText();
        if(PropertyTypes.modelTypeExists(modelType)) {
            currentObject.setModelType(modelType);
        }

    }

    @Override
    public void enterColorValue(MeldrLangParser.ColorValueContext ctx) {

        String rColor, gColor, bColor;
        if(ctx.getChild(0) instanceof MeldrLangParser.RgbContext) {
            rColor = ctx.rgb().r_percent().percent().getText();
            gColor = ctx.rgb().g_percent().percent().getText();
            bColor = ctx.rgb().b_percent().percent().getText();

            rColor = String.valueOf(Double.parseDouble(rColor) / 100);
            gColor = String.valueOf(Double.parseDouble(gColor) / 100);
            bColor = String.valueOf(Double.parseDouble(bColor) / 100);

            currentObject.setModelColor('(' + rColor + ',' + gColor + ',' + bColor + ", 1.0)");
        } else if (ctx.getChild(0) instanceof MeldrLangParser.HexColorContext) {
            String hex = ctx.hexColor().HEXVALUE().getText();

            // Parse R, G, B components from hex
            int r = Integer.parseInt(hex.substring(0, 2), 16);
            int g = Integer.parseInt(hex.substring(2, 4), 16);
            int b = Integer.parseInt(hex.substring(4, 6), 16);

            // Convert to 0.0 - 1.0 floats and format as strings
            rColor = String.format("%.4f", r / 255.0f);
            gColor = String.format("%.4f", g / 255.0f);
            bColor = String.format("%.4f", b / 255.0f);

            currentObject.setModelColor('(' + rColor + ',' + gColor + ',' + bColor + ", 1.0)");
        } else {
            String identifier = ctx.IDENTIFIER().getText();
            if (PropertyTypes.colorTypeExists(identifier)) {
                currentObject.setModelColor(PropertyTypes.getColorFromType(identifier));
            } else {
                semanticError += "ERROR [LINE " +  ctx.start.getLine() +  "]: " + identifier + " is not a valid color!\n";
            }
        }
    }

    @Override
    public void enterLocation_decl(MeldrLangParser.Location_declContext ctx) 
    {
        String xVal = ctx.vector().x_number().number().getChild(0).getText();
        String yVal = ctx.vector().y_number().number().getChild(0).getText();
        String zVal = ctx.vector().z_number().number().getChild(0).getText();

        currentObject.setLocation("(" + xVal + ", " + yVal + ", " + zVal + ")");
    }

    @Override
    public void enterRotation_decl(Rotation_declContext ctx) {
        if(ctx.number() != null) {
            currentObject.setzRot(ctx.number().getChild(0).getText());
        } else if (ctx.vector() != null) {
            currentObject.setxRot(ctx.vector().x_number().number().getChild(0).getText());
            currentObject.setyRot(ctx.vector().y_number().number().getChild(0).getText());
            currentObject.setzRot(ctx.vector().z_number().number().getChild(0).getText());
        }
    }

    @Override
    public void enterDynamic_decl(MeldrLangParser.Dynamic_declContext ctx) 
    {
        String choice = ctx.booleanValue().getChild(0).getText();
        currentObject.setDynamicPhysics(Boolean.parseBoolean(choice.toLowerCase()));
    }

    @Override
    public void enterSize_decl(MeldrLangParser.Size_declContext ctx) {
        currentObject.setSize(ctx.number().getChild(0).getText());
    }

    @Override
    public void enterLevel(MeldrLangParser.LevelContext ctx) {
        this.level = Integer.parseInt(ctx.INT().getText());
    }

    public String printOutput() throws URISyntaxException {
        StringBuilder code = new StringBuilder();
        if(semanticError.isEmpty())
        {
            // Get path to the running JAR file
            File jarFile = new File(Driver.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI());

            // If running from a JAR, jarFile is the path to the JAR
            // Get its parent directory
            String jarDir = jarFile.getParent();

            code.append(String.format(
                        "import bpy\n" +
                        "import os\n" +
                        "import math\n" +
                        "\n" +
                        "addon_dir = r\"%s\"\n" +
                        "asset_file_path = os.path.join(addon_dir, \"assets\", \"objects.blend\")\n" +
                        "asset_directory = os.path.join(asset_file_path, \"Object\") + \"/\"\n" +
                        "\n" +
                        "level_file_path = os.path.join(addon_dir, \"assets\", \"levels.blend\")\n" +
                        "level_directory = os.path.join(level_file_path, \"Collection\") + \"/\"\n" +
                        "\n" +
                        // Delete all objects from scene and clear unused nodes
                        "bpy.ops.object.select_all(action='SELECT')\n" +
                        "bpy.ops.object.delete(use_global=False)\n" +
                        "\n" +
                        "for block in (\n" +
                        "    bpy.data.meshes,\n" +
                        "    bpy.data.materials,\n" +
                        "    bpy.data.textures,\n" +
                        "    bpy.data.images,\n" +
                        "    bpy.data.node_groups,\n" +
                        "    bpy.data.curves,\n" +
                        "    bpy.data.cameras,\n" +
                        "    bpy.data.lights,\n" +
                        "):\n" +
                        "    for datablock in block:\n" +
                        "        if datablock.users == 0:\n" +
                        "            block.remove(datablock)\n" +
                        "\n" +
                        "def delete_empty_collections():\n" +
                        "    # Make a list first to avoid modifying the collection while iterating\n" +
                        "    empty_collections = [col for col in bpy.data.collections if len(col.objects) == 0 and len(col.children) == 0]\n" +
                        "\n" +
                        "    for col in empty_collections:\n" +
                        "        bpy.data.collections.remove(col)\n" +
                        "\n" +
                        "delete_empty_collections()\n" +
                        "collection_name = \"%d\"\n" +
                        "\n" +
                        "bpy.ops.wm.append(\n" +
                        "    filepath=os.path.join(level_directory, collection_name),\n" +
                        "    directory=level_directory,\n" +
                        "    filename=collection_name\n" +
                        ")\n" +
                        "bpy.context.scene.frame_end = 500\n" +
                        "bpy.context.scene.rigidbody_world.point_cache.frame_end = 500\n" +
                        "bpy.context.scene.frame_set(0)\n"
                        ,jarDir, level)
            );
            for(BlenderObject obj : objects)
            {
                code.append(obj.getPythonCode()).append("\n");
            }
            code.append(
                   "bpy.ops.screen.animation_play()\n"
            );
        } else
        {
            System.err.println(semanticError);
        }
        return code.toString();
    }
}



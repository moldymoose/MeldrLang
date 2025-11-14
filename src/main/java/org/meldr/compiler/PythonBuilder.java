package org.meldr.compiler;

import java.util.*;

public class PythonBuilder extends MeldrLangBaseListener
{
    private String sceneName = "";
    private final ArrayList<BlenderObject> objects = new ArrayList<>();
    private BlenderObject currentObject;
    private String semanticError = "";

    // Set will contain objects properties for identifying duplicates
    private Set<String> currentProperties;

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
        String propName = ctx.getChild(0).getChild(0).getText();

        if (currentProperties.contains(propName)) {
            semanticError += "ERROR [LINE " +  ctx.start.getLine() +  "]: Cannot define " + propName + " multiple times!\n";
        } else {
            currentProperties.add(propName);
        }
    }

    @Override
    public void enterModel_decl(MeldrLangParser.Model_declContext ctx) 
    {
        String modelType = ctx.IDENTIFIER().getText();
        currentObject.setModelType(modelType);
    }

    @Override
    public void enterColor_decl(MeldrLangParser.Color_declContext ctx)
    {

    }

    @Override
    public void enterLocation_decl(MeldrLangParser.Location_declContext ctx) 
    { 
        //int x = Integer.parseInt(ctx.X.getText());
        //int y = Integer.parseInt(ctx.Y.getText());
        //int z = Integer.parseInt(ctx.Z.getText());
        //currentObject.setCoordinates(x, y, z);
    }

    @Override
    public void enterDynamic_decl(MeldrLangParser.Dynamic_declContext ctx) 
    { 
        //currentObject.setDynamicPhysics(Boolean.parseBoolean(choice.toLowerCase()));
    }

    public String printOutput()
    {
        String code = "";
        if(semanticError.isEmpty())
        {
            code +=
                "import bpy\n" +
                "bpy.ops.object.select_all(action=\'SELECT\')\n" +
                "bpy.ops.object.delete()\n" +
                "# Ground plane\n" +
                "bpy.ops.mesh.primitive_plane_add(size=50, location=(0, 0, 0))\n" +
                "ground = bpy.context.active_object\n" +
                "ground.name = \"Ground\"\n" +
                "# Material for ground\n" +
                "mat_ground = bpy.data.materials.new(name=\"mat_Ground\")\n" +
                "mat_ground.diffuse_color = (1.0, 1.0, 1.0, 1.0)\n" +
                "ground.data.materials.append(mat_ground)\n" +
                "# Make ground static rigidbody\n" +
                "bpy.context.view_layer.objects.active = ground\n" +
                "bpy.ops.rigidbody.object_add()\n" +
                "ground.rigid_body.type = \'PASSIVE\'\n" +
                "ground.rigid_body.restitution = 0.5\n";
            for(BlenderObject obj : objects)
            {
                code += obj.getPythonCode() + "\n";
            }
        } else
        {
            System.err.println(semanticError);
        }
        return code;
    }
}



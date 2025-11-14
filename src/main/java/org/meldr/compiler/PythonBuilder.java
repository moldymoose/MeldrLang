package org.meldr.compiler;

import java.util.*;

public class PythonBuilder extends MeldrLangBaseListener
{
    private String sceneName = "";
    private final ArrayList<BlenderObject> objects = new ArrayList<>();
    private BlenderObject currentObject;
    private String semanticError = "";

    // Set will contain objects properties for identifying duplicates
    private Map<String, String> currentProperties;

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
        currentProperties = new HashMap<>();
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

        if (currentProperties.get(propName) == null) {
            // add property with null value for now
            currentProperties.put(propName, null);
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
    public void enterDynamic_decl(MeldrLangParser.Dynamic_declContext ctx) 
    {
        String choice = ctx.booleanValue().getChild(0).getText();
        currentObject.setDynamicPhysics(Boolean.parseBoolean(choice.toLowerCase()));
    }

    public String printOutput()
    {
        StringBuilder code = new StringBuilder();
        if(semanticError.isEmpty())
        {
            code.append("""
                    import bpy
                    import os
                    
                    current_dir = os.path.dirname(bpy.data.filepath)
                    asset_file_path = os.path.join(current_dir, "blenderAssets", "objects.blend")
                    directory = os.path.join(asset_file_path, "Object") + "/"
                    
                    """);
            for(BlenderObject obj : objects)
            {
                code.append(obj.getPythonCode()).append("\n");
            }
        } else
        {
            System.err.println(semanticError);
        }
        return code.toString();
    }
}



import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import java.util.*;

public class PythonBuilder extends MeldrLangBaseListener
{
    public String sceneName = "";
    private ArrayList<BlenderObject> objects = new ArrayList<>();
    private Stack<BlenderObject> currentObj = new Stack<>();
    private String semanticError = "";
    private int modelCount = 0, dynamicCount = 0, locationCount = 0, colorCount = 0;

    @Override
    public void enterScene(MeldrLangParser.SceneContext ctx)
    {
        //name for the python file!
        sceneName = ctx.id().getText();
    }

    @Override
    public void enterObj_decl(MeldrLangParser.Obj_declContext ctx) 
    {
        BlenderObject object = new BlenderObject(ctx.id().getText()); 
        currentObj.push(object);  
    }

    @Override
    public void exitObj_decl(MeldrLangParser.Obj_declContext ctx) 
    { 
        objects.add(currentObj.pop());
        //reset paramCount
        modelCount = 0;
        dynamicCount = 0; 
        locationCount = 0; 
        colorCount = 0;
    }

    @Override
    public void enterModel_decl(MeldrLangParser.Model_declContext ctx) 
    {
        modelCount++;
        if(modelCount > 1)
        {
            semanticError += "ERROR [LINE " +  ctx.start.getLine() +  "]: Cannot define MODEL multiple times!\n";
            return;
        } 
        String model_type = ctx.model_ty().getText();
        currentObj.peek().setModelType(model_type);  
    }

    @Override
    public void enterColor_decl(MeldrLangParser.Color_declContext ctx) 
    {
        colorCount++;
        if(colorCount > 1)
        {
            semanticError += "ERROR [LINE " +  ctx.start.getLine() +  "]: Cannot define COLOR multiple times!\n";
            return;
        } 
        if(ctx.rgb_assign() != null)
        {
            List<MeldrLangParser.Rgb_assignContext> array = ctx.rgb_assign();
            for(MeldrLangParser.Rgb_assignContext r : array)
            {
                String colorValue = r.getText();
                switch(colorValue.substring(0, colorValue.indexOf(":")))
                {
                    case "RED":
                        if(currentObj.peek().getRedHue() == -1)
                        {
                            //System.out.println(currentObj.peek().getRedHue());
                            currentObj.peek().setRedHue(Double.parseDouble(colorValue.substring(colorValue.indexOf(":") + 1, colorValue.length() - 1)));
                        } else
                        {
                            semanticError +=  "ERROR [LINE " +  ctx.start.getLine() +  "]: Cannot Redefine RGB values in Object Declarations!\n";
                        }
                        break;
                    case "GREEN":
                        if(currentObj.peek().getGreenHue() == -1)
                        {
                            //System.out.println(currentObj.peek().getRedHue());
                            currentObj.peek().setGreenHue(Double.parseDouble(colorValue.substring(colorValue.indexOf(":") + 1, colorValue.length() - 1)));
                        } else
                        {
                            semanticError =  "ERROR [LINE " + ctx.start.getLine() +  "]: Cannot Redefine RGB values in Object Declarations!\n";
                        }
                        break;
                    case "BLUE":
                        if(currentObj.peek().getBlueHue() == -1)
                        {
                            //System.out.println(currentObj.peek().getRedHue());
                            currentObj.peek().setBlueHue(Double.parseDouble(colorValue.substring(colorValue.indexOf(":") + 1, colorValue.length() - 1)));
                        } else
                        {
                            semanticError =  "ERROR [LINE " +  ctx.start.getLine() +  "]: Cannot Redefine RGB values in Object Declarations!\n";
                        }
                        break;
                }
            }
        }
    }

    @Override
    public void enterLocation_decl(MeldrLangParser.Location_declContext ctx) 
    { 
        locationCount++;
        if(locationCount > 1)
        {
            semanticError += "ERROR [LINE " +  ctx.start.getLine() +  "]: Cannot define LOCATION multiple times!\n";
        }
        //System.out.println(ctx.getText()); debug
        int x = Integer.parseInt(ctx.X.getText());
        int y = Integer.parseInt(ctx.Y.getText());
        int z = Integer.parseInt(ctx.Z.getText());
        currentObj.peek().setCoordinates(x, y, z);
    }

    @Override
    public void enterDynamic_decl(MeldrLangParser.Dynamic_declContext ctx) 
    { 
        dynamicCount++;
        if(dynamicCount > 1)
        {
            semanticError += "ERROR [LINE " +  ctx.start.getLine() +  "]: Cannot define DYNAMIC multiple times!\n";
        }
        String choice = ctx.getText().substring(ctx.getText().indexOf("=") + 1);
        //System.out.println(choice); debug
        currentObj.peek().setDynamicPhysics(Boolean.parseBoolean(choice.toLowerCase()));
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



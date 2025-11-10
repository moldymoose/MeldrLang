public class BlenderObject
{
    private double redHue = -1, blueHue = -1, greenHue = -1;
    private int[] location = {0, 0, 0};
    private String modelType = "SPHERE";
    private boolean dynamicPhysicsEnabled = false;
    private String name = "default";

    public BlenderObject(String name)
    {
        this.name = name;
    }

    public void setRedHue(double redHue)
    {
        this.redHue = redHue / 100;
    }

    public void setGreenHue(double greenHue)
    {
        this.greenHue = greenHue / 100;
    }

    public void setBlueHue(double blueHue)
    {
        this.blueHue = blueHue / 100;
    }

    public double getRedHue()
    {
        return redHue;
    }

    public double getBlueHue()
    {
        return blueHue;
    }

    public double getGreenHue()
    {
        return greenHue;
    }

    public void setModelType(String modelType)
    {
        this.modelType = modelType;
    }

    public String getModelType()
    {
        return modelType;
    }

    public void setCoordinates(int x, int y, int z)
    {
        location[0] = x;
        location[1] = y;
        location[2] = z;
        //System.out.println("Location Set: [" + x + " ," + y + " ," + z + "]"); debug
    }

    public int[] getCoordinates()
    {
        return location;
    }

    public void setDynamicPhysics(boolean option)
    {
        dynamicPhysicsEnabled = option;   
    }

    public String getPythonCode()
    {
        String code = String.format("bpy.ops.mesh.primitive_%s_add(%s, location=(%d, %d, %d))\n" +
        "%s = bpy.context.active_object\n" + 
        "%s.name = \"%s\"\n" +
        "mat_%s = bpy.data.materials.new(name=\"mat_%s\")\n" +
        "mat_%s.diffuse_color = (%f, %f, %f, 1.0)\n" +
        "%s.data.materials.append(mat_%s)\n", 
        (modelType.equals("SPHERE")) ? "uv_sphere" : "cube", (modelType.equals("SPHERE")) ? "radius=1" : "size=2",location[0], location[1], location[2], name, name, name, name, name, name, (redHue == -1) ? 0 : redHue, (greenHue == -1) ? 0 : greenHue, (blueHue == -1) ? 0 : blueHue, name, name);

        if(dynamicPhysicsEnabled)
        {
            code += String.format("bpy.context.view_layer.objects.active = %s\n" +
            "bpy.ops.rigidbody.object_add()\n" +
            "%s.rigid_body.type = 'ACTIVE'\n" +
            "%s.rigid_body.restitution = 0.8\n" +
            "%s.rigid_body.collision_shape = '%s'\n", name, name, name, name, modelType);
        } else
        {
            code += String.format(
                "bpy.context.view_layer.objects.active = %s\n" +
                "bpy.ops.rigidbody.object_add()\n" +
                "my_cube.rigid_body.type = 'PASSIVE'\n", name
            );
        }
        return code;
    }
}
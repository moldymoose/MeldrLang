package org.meldr.compiler;

public class BlenderObject
{

    private String location = "(0, 0, 0)";
    private String modelType = "EMPTY";
    private String modelColor = "(1.0, 1.0, 1.0, 1.0)";
    private boolean dynamicPhysicsEnabled = false;
    private String name;

    public BlenderObject(String name)
    {
        this.name = name;
    }

    public String getModelColor() {
        return modelColor;
    }

    public void setModelColor(String modelColor) {
        this.modelColor = modelColor;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setModelType(String modelType)
    {
        this.modelType = modelType.toUpperCase();
    }

    public String getModelType()
    {
        return modelType;
    }

    public void setDynamicPhysics(boolean option)
    {
        dynamicPhysicsEnabled = option;   
    }

    public String getPythonCode()
    {
        String code = String.format("""
                model = "%s"
                
                bpy.ops.wm.append(
                    filepath=os.path.join(directory, model),
                    directory=directory,
                    filename=model
                )
                
                obj = bpy.context.selected_objects[0]
                obj.name = "%s"
                obj.location = %s
                
                mat = obj.data.materials[0]
                new_mat = mat.copy()
                obj.data.materials[0] = new_mat
                
                nodes = new_mat.node_tree.nodes
                links = new_mat.node_tree.links
                
                bsdf = nodes.get("Principled BSDF")
                
                mix = nodes.new("ShaderNodeMixRGB")
                mix.blend_type = 'MULTIPLY'
                mix.inputs[0].default_value = 1.0
                mix.inputs[2].default_value = %s
                mix.location = (bsdf.location.x - 200, bsdf.location.y - 100)
                
                if bsdf.inputs["Base Color"].links:
                    old_link = bsdf.inputs["Base Color"].links[0]
                    old_color_output = old_link.from_socket
                    old_color_node = old_link.from_node
                    links.remove(old_link)
                
                    links.new(old_color_output, mix.inputs[1])
                
                links.new(mix.outputs[0], bsdf.inputs["Base Color"])
                
                """, this.modelType, this.name, this.location, this.modelColor);

        if(!dynamicPhysicsEnabled)
        {
            code += """
                    obj.rigid_body.type = 'PASSIVE'
                    """;
        }

        return code;
    }
}
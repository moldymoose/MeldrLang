package org.meldr.compiler;

import java.util.ArrayList;
import java.util.List;

public class BlenderObject
{

    private String modelType = "EMPTY";
    private String location = "(0, 0, 0)";
    private String modelColor = "(1.0, 1.0, 1.0, 0.0)";
    private String size = "1.0";
    private String xRot = "0";
    private String yRot = "0";
    private String zRot = "0";
    private boolean dynamicPhysicsEnabled = false;
    private final String name;

    public final List<Keyframe> keyframes = new ArrayList<>();

    public BlenderObject(String name)
    {
        this.name = name;
    }

    public void addKeyframe(Keyframe keyframe) {
        this.keyframes.add(keyframe);
    }

    public String getName()
    {
        return name;
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

    public void setxRot(String xRot) {
        this.xRot = xRot;
    }
    public void setyRot(String yRot) {
        this.yRot = yRot;
    }
    public void setzRot(String zRot) {
        this.zRot = zRot;
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
        String code = String.format(
            "model = \"%s\"\n" +
            "\n" +
            "bpy.ops.wm.append(\n" +
            "    filepath=os.path.join(asset_directory, model),\n" +
            "    directory=asset_directory,\n" +
            "    filename=model\n" +
            ")\n" +
            "\n" +
            "obj = bpy.context.selected_objects[0]\n" +
            "obj.name = \"%s\"\n" +
            "obj.location = %s\n" +
            "obj.scale.x = obj.scale.y = obj.scale.z = %s\n" +
            "obj.rotation_euler[0] += math.radians(%s)\n" +
            "obj.rotation_euler[1] += math.radians(%s)\n" +
            "obj.rotation_euler[2] += math.radians(%s)\n" +
            "\n" +
            "mat = obj.data.materials[0]\n" +
            "new_mat = mat.copy()\n" +
            "obj.data.materials[0] = new_mat\n" +
            "\n" +
            "nodes = new_mat.node_tree.nodes\n" +
            "links = new_mat.node_tree.links\n" +
            "\n" +
            "bsdf = nodes.get(\"Principled BSDF\")\n" +
            "\n" +
            "mix = nodes.new(\"ShaderNodeMixRGB\")\n" +
            "mix.blend_type = 'COLOR'\n" +
            "mix.inputs[0].default_value = 1.0\n" +
            "mix.inputs[2].default_value = %s\n" +
            "mix.location = (bsdf.location.x - 200, bsdf.location.y - 100)\n" +
            "\n" +
            "if bsdf.inputs[\"Base Color\"].links:\n" +
            "    old_link = bsdf.inputs[\"Base Color\"].links[0]\n" +
            "    old_color_output = old_link.from_socket\n" +
            "    old_color_node = old_link.from_node\n" +
            "    links.remove(old_link)\n" +
            "\n" +
            "    links.new(old_color_output, mix.inputs[1])\n" +
            "\n" +
            "links.new(mix.outputs[0], bsdf.inputs[\"Base Color\"])\n",
            this.modelType, this.name, this.location, this.size, this.xRot, this.yRot, this.zRot, this.modelColor
        );

        if(!dynamicPhysicsEnabled)
        {
            code += "obj.rigid_body.type = 'PASSIVE'\n";
        }

        if(!keyframes.isEmpty()) {
            code += "obj.keyframe_insert(\"location\", frame = 0)\n" +
                    "mix.inputs[2].keyframe_insert(\"default_value\", frame = 0)\n" +
                    "obj.keyframe_insert(\"rotation_euler\", frame = 0)\n" +
                    "obj.keyframe_insert(\"scale\", frame = 0)\n" +
                    "obj.rigid_body.kinematic = True\n";
        }
        for(Keyframe key : keyframes) {
            code += key.getPythonCode();
        }

        return code;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
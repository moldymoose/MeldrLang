package org.meldr.compiler;

public class Keyframe {
    private int frameNumber;
    
    private String location = null;
    private String modelColor = null;
    private String size = null;
    private String xRot = null;
    private String yRot = null;
    private String zRot = null;

    public Keyframe (int number) {
        this.frameNumber = number;
    }

    public int getFrameNumber() {
        return frameNumber;
    }
    public void setFrameNumber(int frameNumber) {
        this.frameNumber = frameNumber;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getModelColor() {
        return modelColor;
    }
    public void setModelColor(String modelColor) {
        this.modelColor = modelColor;
    }
    public String getSize() {
        return size;
    }
    public void setSize(String size) {
        this.size = size;
    }
    public String getxRot() {
        return xRot;
    }
    public void setxRot(String xRot) {
        this.xRot = xRot;
    }
    public String getyRot() {
        return yRot;
    }
    public void setyRot(String yRot) {
        this.yRot = yRot;
    }
    public String getzRot() {
        return zRot;
    }
    public void setzRot(String zRot) {
        this.zRot = zRot;
    }

    public String getPythonCode() {
        String code = "";

        if(location != null) {
            code += String.format("obj.location = %s\n" + "obj.keyframe_insert(\"location\", frame = %d)\n", location, frameNumber);
        }
        if(modelColor != null) {
            code += String.format("mix.inputs[2].default_value = %s\n" + "mix.inputs[2].keyframe_insert(\"default_value\", frame = %d)\n", modelColor, frameNumber);
        }
        if(size != null) {
            code += String.format("obj.scale.x = obj.scale.y = obj.scale.z = %s\n" + "obj.keyframe_insert(\"scale\", frame = %d)\n", size, frameNumber);
        }
        if( zRot != null) {
            code += String.format("obj.rotation_euler.z += math.radians(%s)\n", zRot);
            if (yRot != null) {
            code += String.format("obj.rotation_euler.x += math.radians(%s)\n", xRot);
            code += String.format("obj.rotation_euler.y += math.radians(%s)\n", yRot);
            }
            code += String.format("obj.keyframe_insert(\"rotation_euler\", frame = %d)\n", frameNumber);
        }

        return code;
    }
}

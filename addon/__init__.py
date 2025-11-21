bl_info = {
    "name": "MeldrLang Runner",
    "author": "Antonio and Phil",
    "version": (1, 4),
    "blender": (2, 80, 0),
    "location": "Text Editor > Sidebar",
    "description": "Runs MeldrLang JAR next to .blend file and executes generated Python",
    "category": "Development"
}

import bpy
import subprocess
import os
import glob
import shutil

class ML_OT_RunMeldrLang(bpy.types.Operator):
    bl_idname = "ml.run_meldrlang"
    bl_label = "Run MeldrLang"
    bl_options = {'REGISTER', 'UNDO'}

    def execute(self, context):
        text_block = context.space_data.text
        if not text_block:
            self.report({'ERROR'}, "No text block selected.")
            return {'CANCELLED'}

        user_input = text_block.as_string()

        addon_dir = os.path.dirname(__file__)

        # JAR path next to the .blend file
        jar_name = "compiler.jar"
        jar_path = os.path.join(addon_dir, jar_name)

        if not os.path.exists(jar_path):
            self.report({'ERROR'}, f"JAR not found:\n{jar_path}")
            return {'CANCELLED'}

        # Detect Java
        java_path = shutil.which("java")
        if java_path is None:
            self.report({'ERROR'}, "Java executable not found in system PATH.")
            return {'CANCELLED'}

        # Run the JAR
        try:
            process = subprocess.Popen(
                [java_path, "-jar", jar_path],
                stdin=subprocess.PIPE,
                stdout=subprocess.PIPE,
                stderr=subprocess.PIPE,
                text=True,
                cwd=addon_dir
            )
            stdout, stderr = process.communicate(user_input)
            if stderr:
                self.report({'ERROR'}, stderr)
                return {'CANCELLED'}
        except Exception as e:
            self.report({'ERROR'}, f"Failed to run JAR: {e}")
            return {'CANCELLED'}

        # Locate output folder
        output_dir = os.path.join(addon_dir, "output")
        if not os.path.exists(output_dir):
            self.report({'ERROR'}, f"Output folder not found:\n{output_dir}")
            return {'CANCELLED'}

        # Find .py files in output folder
        print(stdout.rstrip())
        py_files = glob.glob(os.path.join(output_dir, stdout.rstrip()))
        print(py_files)
        if not py_files:
            self.report({'ERROR'}, "No Python files found in output folder.")
            return {'CANCELLED'}

        py_path = os.path.normpath(py_files[0])  # take first .py file

        # Load the .py file into Blender Text Editor
        try:
            new_text = bpy.data.texts.load(py_path)
        except RuntimeError:
            # Already loaded, find existing
            for t in bpy.data.texts:
                if bpy.path.abspath(t.filepath) == py_path:
                    new_text = t
                    break

        # Execute the Python script safely
        try:
            exec(new_text.as_string(), {})
        except Exception as e:
            self.report({'ERROR'}, f"Generated Python script failed:\n{e}")
            return {'CANCELLED'}

        self.report({'INFO'}, f"Executed: {os.path.basename(py_path)}")
        return {'FINISHED'}


class ML_PT_MeldrLangPanel(bpy.types.Panel):
    bl_label = "MeldrLang Runner"
    bl_space_type = 'TEXT_EDITOR'
    bl_region_type = 'UI'
    bl_category = "MeldrLang"

    def draw(self, context):
        self.layout.operator("ml.run_meldrlang", icon="FILE_SCRIPT")


classes = [ML_OT_RunMeldrLang, ML_PT_MeldrLangPanel]

def register():
    for cls in classes:
        bpy.utils.register_class(cls)


def unregister():
    for cls in reversed(classes):
        bpy.utils.unregister_class(cls)


if __name__ == "__main__":
    register()
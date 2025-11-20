bl_info = {
    "name": "Meldr Compiler",
    "author": "Phil",
    "version": (1, 0),
    "blender": (3, 0, 0),
    "location": "Text Editor > Sidebar",
    "description": "Compile Meldr files from Blender Text Editor",
    "category": "Text Editor",
}

import bpy
import subprocess
import os
from bpy.props import StringProperty

# ----------------------
# Add-on preferences
# ----------------------
class MELDER_Preferences(bpy.types.AddonPreferences):
    bl_idname = __name__

    jar_path: StringProperty(
        name="Compiler Jar Path",
        description="Path to your Meldr compiler fat jar",
        default="",
        subtype='FILE_PATH'
    )

    def draw(self, context):
        layout = self.layout
        layout.prop(self, "jar_path")

# ----------------------
# Operator
# ----------------------
class MELDER_OT_compile(bpy.types.Operator):
    bl_idname = "text.compile_melder"
    bl_label = "Compile Meldr"

    def execute(self, context):
        prefs = bpy.context.preferences.addons[__name__].preferences
        jar_path = prefs.jar_path
        if not jar_path or not os.path.exists(jar_path):
            self.report({'ERROR'}, "Compiler jar path is not set or invalid")
            return {'CANCELLED'}

        text = context.space_data.text
        if not text:
            self.report({'WARNING'}, "No text block open")
            return {'CANCELLED'}

        meldr_file = os.path.join(os.path.expanduser("~"), "temp.meldr")
        py_file = os.path.join(os.path.expanduser("~"), "temp.py")

        with open(meldr_file, "w") as f:
            f.write(text.as_string())

        # Run compiler
        result = subprocess.run(
            ["java", "-jar", jar_path, meldr_file],
            capture_output=True,
            text=True
        )

        # Compiler failed
        if result.returncode != 0:
            self.report({'ERROR'}, result.stderr or "Compiler failed with no output")
            return {'CANCELLED'}

        # No output produced
        if not os.path.exists(py_file):
            self.report({'ERROR'}, "Compiler finished but no Python file was produced")
            return {'CANCELLED'}

        # Run compiler output
        with open(py_file, "r") as f:
            exec(f.read(), {})

        self.report({'INFO'}, "Meldr compiled successfully")
        return {'FINISHED'}


# ----------------------
# Sidebar Panel
# ----------------------
class MELDER_PT_panel(bpy.types.Panel):
    bl_label = "Meldr Compiler"
    bl_idname = "TEXT_PT_melder"
    bl_space_type = 'TEXT_EDITOR'
    bl_region_type = 'UI'
    bl_category = "Meldr"

    def draw(self, context):
        layout = self.layout
        layout.operator(MELDER_OT_compile.bl_idname, icon='FILE_SCRIPT')

# ----------------------
# Hotkey
# ----------------------
addon_keymaps = []

def register_hotkey():
    wm = bpy.context.window_manager
    km = wm.keyconfigs.addon.keymaps.new(name='Text', space_type='TEXT_EDITOR')
    kmi = km.keymap_items.new(MELDER_OT_compile.bl_idname, 'C', 'PRESS', ctrl=True, alt=True)
    addon_keymaps.append((km, kmi))

def unregister_hotkey():
    for km, kmi in addon_keymaps:
        km.keymap_items.remove(kmi)
    addon_keymaps.clear()

# ----------------------
# Registration
# ----------------------
classes = [MELDER_Preferences, MELDER_OT_compile, MELDER_PT_panel]

def register():
    for cls in classes:
        bpy.utils.register_class(cls)
    register_hotkey()

def unregister():
    unregister_hotkey()
    for cls in reversed(classes):
        bpy.utils.unregister_class(cls)

if __name__ == "__main__":
    register()

# Meldr

Meldr is a simple scripting language designed for creating interactive 3D scenes in Blender without requiring programming or 3D modeling experience. Write intuitive scripts that describe your scene, and Meldr handles the rest.

---

## What Can You Do With Meldr?

Create 3D scenes with:
- **Multiple objects** - Spheres, cubes, ramps, rockets, and more
- **Precise positioning** - Using simplified coordinate syntax
- **Creative colors** - Assign presset colors, RGB percentages, or hex codes
- **Physics** - Dynamic objects that fall and collide
- **Interactive levels** - Golf courses, slides, and more

---

## Quick Start

1. **Install the add-on** → See the [Installation Guide](docs/installation.md)
2. **Learn the basics** → Check out the [Meldr-Script Guide](docs/script_quide.md)
3. **Use the add-on** → Follow the [Add-On Usage Guide](docs/addon_usage.md)

---

### Example Script

```meldr
SCENE Example_Scene
LEVEL 2

    OBJECT red_ball
        MODEL = ball
        COLOR = red
        LOCATION = (21, -12, 35)
        DYNAMIC = TRUE
    END OBJECT

    OBJECT blue_ball
        MODEL = ball
        COLOR = blue
        LOCATION = (18, -11, 50)
        DYNAMIC = TRUE
    END OBJECT

END SCENE
```

---

## Documentation

### [Installation Guide](docs/installation.md)
Get Meldr set up on your system. This guide covers:
- Prerequisites (Blender, Java)
- Downloading the add-on
- Installing into Blender

### [Meldr-Script Guide](docs/script_quide.md)
Learn the Meldr scripting language. Covers:
- Basic syntax and structure
- Scenes and objects
- Properties: Models, locations, rotations, colors, and more
- Complete examples

### [Add-On Usage Guide](docs/addon_usage.md)
How to use the Meldr add-on in Blender. Includes:
- Setting up your workspace
- Running scripts
- Tips and tricks

---

## Project Source Code Structure

- **`compiler/`** - The Meldr-Lang compiler (Built in Java utilizing an Antlr4 Grammer)
- **`addon/`** - The Blender add-on (Python)
- **`docs/`** - Documentation and guides

---
import os
from PIL import Image

def generate_icons(source_image_path, res_dir):
    try:
        img = Image.open(source_image_path).convert("RGBA")
    except Exception as e:
        print(f"Error opening image: {e}")
        return

    # Legacy icon sizes
    legacy_sizes = {
        "mdpi": 48,
        "hdpi": 72,
        "xhdpi": 96,
        "xxhdpi": 144,
        "xxxhdpi": 192,
    }

    # Adaptive icon foreground sizes (108dp base)
    adaptive_foreground_sizes = {
        "mdpi": 108,
        "hdpi": 162,
        "xhdpi": 216,
        "xxhdpi": 324,
        "xxxhdpi": 432,
    }

    # Generate legacy icons
    for density, size in legacy_sizes.items():
        mipmap_dir = os.path.join(res_dir, f"mipmap-{density}")
        os.makedirs(mipmap_dir, exist_ok=True)
        
        resized_img = img.resize((size, size), Image.Resampling.LANCZOS)
        resized_img.save(os.path.join(mipmap_dir, "ic_launcher.png"), "PNG")
        resized_img.save(os.path.join(mipmap_dir, "ic_launcher_round.png"), "PNG")
        
        # Make a slightly scaled down version for the adaptive foreground so the logo fits inside the safe zone (66% of the foreground is the safe zone, which is 72dp out of 108dp).
        # We'll just scale the image to the foreground size directly.
        fg_size = adaptive_foreground_sizes[density]
        fg_img = img.resize((fg_size, fg_size), Image.Resampling.LANCZOS)
        fg_img.save(os.path.join(mipmap_dir, "ic_launcher_foreground.png"), "PNG")

    # Create anydpi-v26 for adaptive icons
    anydpi_dir = os.path.join(res_dir, "mipmap-anydpi-v26")
    os.makedirs(anydpi_dir, exist_ok=True)
    
    adaptive_icon_xml = """<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
"""
    with open(os.path.join(anydpi_dir, "ic_launcher.xml"), "w") as f:
        f.write(adaptive_icon_xml)
        
    with open(os.path.join(anydpi_dir, "ic_launcher_round.xml"), "w") as f:
        f.write(adaptive_icon_xml)

    # Ensure color exists
    values_dir = os.path.join(res_dir, "values")
    os.makedirs(values_dir, exist_ok=True)
    colors_file = os.path.join(values_dir, "colors.xml")
    
    color_entry = '<color name="ic_launcher_background">#FFFFFF</color>'
    
    if os.path.exists(colors_file):
        with open(colors_file, "r") as f:
            content = f.read()
        if "ic_launcher_background" not in content:
            content = content.replace("</resources>", f"    {color_entry}\\n</resources>")
            with open(colors_file, "w") as f:
                f.write(content)
    else:
        with open(colors_file, "w") as f:
            f.write(f'<?xml version="1.0" encoding="utf-8"?>\\n<resources>\\n    {color_entry}\\n</resources>')

    print("Icons generated successfully!")

if __name__ == "__main__":
    generate_icons("Crescent Connect Logo.png", "app/src/main/res")

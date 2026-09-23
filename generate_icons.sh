#!/bin/bash
SRC="Crescent Connect Logo.png"
RES="app/src/main/res"

# Function to generate legacy and foreground icons
generate() {
    DENSITY=$1
    SIZE=$2
    FG_SIZE=$3
    
    DIR="$RES/mipmap-$DENSITY"
    mkdir -p "$DIR"
    
    convert "$SRC" -resize ${SIZE}x${SIZE} "$DIR/ic_launcher.png"
    convert "$SRC" -resize ${SIZE}x${SIZE} "$DIR/ic_launcher_round.png"
    
    # Foreground adaptive icon
    convert "$SRC" -resize ${FG_SIZE}x${FG_SIZE} "$DIR/ic_launcher_foreground.png"
}

# Generate for each density
# MDPI: 48, FG: 108
generate "mdpi" 48 108
# HDPI: 72, FG: 162
generate "hdpi" 72 162
# XHDPI: 96, FG: 216
generate "xhdpi" 96 216
# XXHDPI: 144, FG: 324
generate "xxhdpi" 144 324
# XXXHDPI: 192, FG: 432
generate "xxxhdpi" 192 432

# Adaptive XML setup
ANYDPI="$RES/mipmap-anydpi-v26"
mkdir -p "$ANYDPI"

cat << 'XML' > "$ANYDPI/ic_launcher.xml"
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
XML

cp "$ANYDPI/ic_launcher.xml" "$ANYDPI/ic_launcher_round.xml"

# Background color in colors.xml
VALUES="$RES/values"
mkdir -p "$VALUES"
COLORS="$VALUES/colors.xml"

if [ ! -f "$COLORS" ]; then
    echo '<?xml version="1.0" encoding="utf-8"?>' > "$COLORS"
    echo '<resources>' >> "$COLORS"
    echo '</resources>' >> "$COLORS"
fi

if ! grep -q "ic_launcher_background" "$COLORS"; then
    sed -i 's/<\/resources>/    <color name="ic_launcher_background">#FFFFFF<\/color>\n<\/resources>/' "$COLORS"
fi

# Clean up any leftover drawable defaults that might conflict
rm -f "$RES/drawable/ic_launcher_background.xml"
rm -f "$RES/drawable/ic_launcher_foreground.xml"
rm -f "$RES/drawable-v24/ic_launcher_foreground.xml"

echo "Icons generated using ImageMagick!"

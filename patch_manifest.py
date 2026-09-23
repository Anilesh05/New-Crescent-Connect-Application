import xml.etree.ElementTree as ET

tree = ET.parse('app/src/main/AndroidManifest.xml')
root = tree.getroot()

has_camera = False
for uses_permission in root.findall('uses-permission'):
    if uses_permission.get('{http://schemas.android.com/apk/res/android}name') == 'android.permission.CAMERA':
        has_camera = True
        break

if not has_camera:
    elem = ET.Element('uses-permission', {'android:name': 'android.permission.CAMERA'})
    root.insert(1, elem)

tree.write('app/src/main/AndroidManifest.xml', encoding='utf-8', xml_declaration=True)

with open('app/src/main/AndroidManifest.xml', 'r') as f:
    content = f.read()

content = content.replace('xmlns:ns0', 'xmlns:android')
content = content.replace('ns0:', 'android:')
content = content.replace('xmlns:android="http://schemas.android.com/apk/res/android"', '')

final_content = '<?xml version="1.0" encoding="utf-8"?>\\n<manifest xmlns:android="http://schemas.android.com/apk/res/android">\\n' + content.split('<manifest')[1]

with open('app/src/main/AndroidManifest.xml', 'w') as f:
    f.write(final_content)

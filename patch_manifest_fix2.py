with open('app/src/main/AndroidManifest.xml', 'r') as f:
    content = f.read()

content = content.replace('\\n<manifest xmlns:android="http://schemas.android.com/apk/res/android">\\n >', '<manifest xmlns:android="http://schemas.android.com/apk/res/android">')
content = content.replace('\\n', '\n')
content = content.replace(' >', '>')

with open('app/src/main/AndroidManifest.xml', 'w') as f:
    f.write(content)

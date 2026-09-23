with open('gradle/libs.versions.toml', 'r') as f:
    content = f.read()

content = content.replace('googleid = "1.1.1"\\n', 'googleid = "1.1.1"\\nzxing = "3.5.3"\\nmlkitBarcode = "17.3.0"\\n')
content = content.replace('googleid = { group = "com.google.android.libraries.identity.googleid", name = "googleid", version.ref = "googleid" }\\n', 'googleid = { group = "com.google.android.libraries.identity.googleid", name = "googleid", version.ref = "googleid" }\\nzxing-core = { group = "com.google.zxing", name = "core", version.ref = "zxing" }\\nmlkit-barcode = { group = "com.google.mlkit", name = "barcode-scanning", version.ref = "mlkitBarcode" }\\n')

with open('gradle/libs.versions.toml', 'w') as f:
    f.write(content)

with open('gradle/libs.versions.toml', 'r') as f:
    content = f.read()

import re

if 'zxing' not in content:
    content = re.sub(
        r'(googleid\s*=\s*"[^"]*")',
        r'\1\nzxing = "3.5.3"\nmlkitBarcode = "17.3.0"',
        content
    )

if 'zxing-core' not in content:
    content = re.sub(
        r'(googleid\s*=\s*\{[^\}]*\})',
        r'\1\nzxing-core = { group = "com.google.zxing", name = "core", version.ref = "zxing" }\nmlkit-barcode = { group = "com.google.mlkit", name = "barcode-scanning", version.ref = "mlkitBarcode" }',
        content
    )

with open('gradle/libs.versions.toml', 'w') as f:
    f.write(content)

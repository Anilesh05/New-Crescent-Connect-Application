with open('app/src/main/java/com/example/ui/verification/QrScannerScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)', '@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class.java)')
# Wait, actually Kotlin's @OptIn takes KClass, while Java's @androidx.annotation.OptIn takes java.lang.Class
# I should just use @kotlin.OptIn or @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class) 
# The error was "Annotation argument must be a compile-time constant."
# The correct way is to use `@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)` ? No, Kotlin's standard is `@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)`. Let's just import it and use @androidx.annotation.OptIn(ExperimentalGetImage::class) ?
# The best way is to use Kotlin's `@OptIn`:
content = content.replace('@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)', '@kotlin.OptIn(androidx.camera.core.ExperimentalGetImage::class)')

with open('app/src/main/java/com/example/ui/verification/QrScannerScreen.kt', 'w') as f:
    f.write(content)

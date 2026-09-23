sed -i 's/private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()//' app/src/main/java/com/example/data/repository/AuthRepositoryImpl.kt
sed -i 's/val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()/val firebaseAuth = FirebaseAuth.getInstance()\n            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()/' app/src/main/java/com/example/data/repository/AuthRepositoryImpl.kt
sed -i 's/firebaseAuth.signOut()/FirebaseAuth.getInstance().signOut()/' app/src/main/java/com/example/data/repository/AuthRepositoryImpl.kt

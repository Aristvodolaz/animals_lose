package com.application.lose_animals.data.repository
import com.application.lose_animals.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) {

    suspend fun registerUser(email: String, password: String, username: String): Boolean {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        result.user?.let { user ->
            val userRef = database.getReference("users").child(user.uid)
            val newUser = User(id = user.uid, email = email, username = username)
            userRef.setValue(newUser).await()
            return true
        }
        return false
    }


    // Авторизация пользователя
    suspend fun loginUser(email: String, password: String): Boolean {
        auth.signInWithEmailAndPassword(email, password).await()
        return auth.currentUser != null
    }

    // Получение текущего пользователя
    suspend fun getCurrentUser(): User? {
        val uid = auth.currentUser?.uid ?: return null
        val snapshot = database.getReference("users").child(uid).get().await()
        return snapshot.getValue(User::class.java)
    }

    // Выход из аккаунта
    fun logout() {
        auth.signOut()
    }
}

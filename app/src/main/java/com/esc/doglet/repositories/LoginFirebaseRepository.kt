package com.esc.doglet.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginFirebaseRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore) {

    suspend fun authenticate(email: String): Boolean {
        return firebaseAuth.fetchSignInMethodsForEmail(email).await()
            .signInMethods.orEmpty().isEmpty()
    }

    suspend fun createUser(email: String, password: String): String {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        return firebaseAuth.currentUser?.uid ?:
            throw FirebaseAuthException("", "")
    }

    fun setUserDetails(hashMap: HashMap<String, String>, uid: String): Boolean {
        return firestore.collection("users").document(uid).set(hashMap).isSuccessful
    }

    suspend fun signInUser(email: String, password: String): FirebaseUser {
        firebaseAuth.signInWithEmailAndPassword(
            email, password).await()
        return firebaseAuth.currentUser ?:
            throw FirebaseAuthException("", "")
    }
    
}
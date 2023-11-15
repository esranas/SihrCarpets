package com.esrannas.capstoneproject.data.repository

import com.esrannas.capstoneproject.common.Resource
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class AuthRepository @Inject constructor (private val firebaseAuth: FirebaseAuth){

    fun isUserLoggedIn() = firebaseAuth.currentUser != null

    fun getUserId() = firebaseAuth.currentUser?.uid.orEmpty()

    suspend fun signIn(email: String, password: String): Resource<Unit> {

        return try {

            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()

            if (result.user != null) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Something went wrong!")
            }
        } catch (e: Exception) {
            Resource.Error("This user is not registered")
        }
    }

    suspend fun signUp(email: String, password: String): Resource<Unit> {

        return try {

            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            if (result.user != null) {
                Resource.Success(Unit)
            } else {
                Resource.Error("This email address is registered!")
            }
        } catch (e: Exception) {
            Resource.Error("This email address is registered!")
        }
    }

    fun logOut() = firebaseAuth.signOut()
}
package com.esc.doglet.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esc.doglet.repositories.LoginFirebaseRepository
import com.esc.doglet.utils.login.LoginPreferences
import com.esc.doglet.utils.login.LoginUtils
import com.esc.doglet.utils.login.LoginUtils.EMAIL_EXPRESSION
import com.google.firebase.auth.FirebaseAuthException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

private const val TAG = "myT"

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseRepository: LoginFirebaseRepository,
    private val loginPreferences: LoginPreferences
) : ViewModel()  {

    private var validEmail = false
    private val emailError = MutableLiveData<String>()
    private val nameError = MutableLiveData<String>()
    private val passwordError = MutableLiveData<String>()
    private val passConError = MutableLiveData<String>()
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var passCon: String
    private lateinit var name: String
    private val loggedIn = MutableLiveData<Boolean>()
    private val preferencesFlow = loginPreferences.preferencesFlow

    fun isValidEmail(tEmail : String) {
        val pattern = Pattern.compile(EMAIL_EXPRESSION, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(tEmail)
        if (matcher.matches()) {
            viewModelScope.launch(Dispatchers.IO) {
                if (!firebaseRepository.authenticate(tEmail)) emailError.postValue("Email exists")
                validEmail = true
            }
        }
    }

    private fun validateEmail(): Boolean {
        return if (email.isEmpty()) {
            emailError.value = "Email can not be empty"
            false
        } else if (!validEmail) {
            emailError.value = "Please enter a valid email"
            false
        } else if (!emailError.value.equals("Email exists")) {
            emailError.value = ""
            Log.d("myTagEmails", "New email is:$email")
            true
        } else {
            emailError.value = "This email already exists"
            Log.d("myTagEmails", "email already exists")
            false
        }
    }

    private fun validatePassCon(): Boolean {
        val checkPassCon = passCon
        val checkPass = password
        return if (checkPassCon == checkPass) {
            Log.d("myT", "passwords match")
            passConError.value = ""
            true
        } else {
            passConError.value = "Passwords do not match"
            false
        }
    }

    private fun validateName(): Boolean {
        return if (name.isEmpty()) {
            nameError.value = "Please enter your name"
            false
        } else {
            nameError.value = ""
            true
        }
    }

    fun setPassword(tPassword : String) {
        val checkError = LoginUtils.validatePassword(tPassword)
        if (checkError.isEmpty()) {
            password = tPassword
            passwordError.value = checkError
        } else passwordError.value = checkError
    }

    fun submitNewUser() {
        if (!validateEmail() || !validatePassCon() || !validateName()) return
        else createUser()
    }

    private fun createUser() {
        if (email.isNotEmpty() && password.isNotEmpty()) viewModelScope.launch(Dispatchers.IO) {
            try {
                firebaseRepository.createUser(email, password).let { uid ->
                    val hashMap = hashMapOf("name" to name)
                    if (firebaseRepository.setUserDetails(hashMap, uid)) {
                        loggedIn.postValue(true)
                        loginPreferences.updateUserData(name, email, password, uid)
                    } else {
                        Log.d(TAG, "user details not stored")
                        firebaseRepository.setUserDetails(hashMap, uid)
                        loggedIn.postValue(false)
                    }
                }
            } catch (e: FirebaseAuthException) {
                Log.d(TAG, "user not created: ${e.message}")
            }
        }
    }

    fun setEmail(tEmail: String) { email = tEmail }
    fun setName(tName: String) {name = tName}
    fun setPassCon(tPassCon: String) { passCon = tPassCon }

    fun getLoggedIn() : LiveData<Boolean> { return loggedIn}
    fun getEmailError() : LiveData<String> { return emailError }
    fun getPasswordError() : LiveData<String> { return passwordError}
    fun getNameError() : LiveData<String> { return nameError}
    fun getPassConError() : LiveData<String> { return passConError }

}
package com.esc.doglet.utils.login

import android.util.Log
import java.util.*

object LoginUtils {

    const val EMAIL_EXPRESSION = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$"
    private const val UPPERCASE_EXPRESSION = ".*\\d.*"

    fun validatePassword(checkPass : String) : String {
        Log.d("myTagEmails", "password is: $checkPass")
        return if (checkPass.isEmpty()) {
            "Password cannot be empty"
        } else if (checkPass == checkPass.lowercase(Locale.getDefault()) && !checkPass.matches(
                UPPERCASE_EXPRESSION.toRegex())) {
            "Password must contain an uppercase and number"
        } else if (checkPass == checkPass.lowercase(Locale.getDefault())) {
            "Password must contain an uppercase"
        } else if (!checkPass.matches(UPPERCASE_EXPRESSION.toRegex())) {
            "Password must contain a number"
        } else if (checkPass.length < 6) {
            "Password must contain at least 6 characters"
        } else {
            "" //return true
        }
    }
}
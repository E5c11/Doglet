package com.esc.doglet.utils.login

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext

data class UserPreferences(val name: String, val email: String, val password: String, val uid: String)

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_details")

@Singleton
class LoginPreferences @Inject constructor(@ApplicationContext context: Context){

    private val userData: DataStore<Preferences> = context.dataStore

    val preferencesFlow: Flow<Unit> = userData.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else throw it
        }.map { preferences ->
            val name = preferences[PreferenceKeys.USER_NAME]?: "guest"
            val email = preferences[PreferenceKeys.USER_EMAIL]?: "guest@email.com"
            val password = preferences[PreferenceKeys.USER_PASSWORD]?: "password123"
            val uid = preferences[PreferenceKeys.USER_UID]?: "uid"
            UserPreferences(name, email, password, uid)
        }

    suspend fun updateUserData(name: String, email: String, password: String, uid: String) {
        userData.edit { preferences ->
            preferences[PreferenceKeys.USER_NAME] = name
            preferences[PreferenceKeys.USER_EMAIL] = email
            preferences[PreferenceKeys.USER_PASSWORD] = password
            preferences[PreferenceKeys.USER_UID] = uid
        }
    }

    private object PreferenceKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PASSWORD = stringPreferencesKey("user_password")
        val USER_UID = stringPreferencesKey("user_uid")
    }
}
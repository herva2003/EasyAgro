package com.puc.easyagro.datastore

import android.content.Context
import androidx.core.content.edit

private const val USER_PREFERENCES_NAME = "prefs_tokens"

private const val UID_KEY = "uid"
private const val USER_ID = "userID"
private const val USER_NAME = "nickname"
private const val FCMTOKEN_KEY = "fcmToken"

/**
 * Class that handles saving and retrieving user preferences
 */
class UserPreferencesRepository private constructor(context: Context) {

    private val sharedPreferences =
        context.applicationContext.getSharedPreferences(USER_PREFERENCES_NAME, Context.MODE_PRIVATE)

    /**
     * Obter o UID.
     */
    var token: String = ""
        get() {
            return sharedPreferences.getString(UID_KEY, "")!!
        }

    var userId: String = ""
        get() {
            return sharedPreferences.getString(USER_ID, "")!!
        }

    var userName: String = ""
        get() {
            return sharedPreferences.getString(USER_NAME, "")!!
        }

    fun updateUserId(newUid: String) {
        sharedPreferences.edit {
            putString(USER_ID, newUid)
        }
    }

    fun updateToken(newUid: String) {
        sharedPreferences.edit {
            putString(UID_KEY, newUid)
        }
    }

    var fcmToken: String = ""
        get() {
            return sharedPreferences.getString(FCMTOKEN_KEY, "")!!
        }

    fun updateFcmToken(newFcmToken: String) {
        sharedPreferences.edit {
            putString(FCMTOKEN_KEY, newFcmToken)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreferencesRepository? = null
        fun getInstance(context: Context): UserPreferencesRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE?.let {
                    return it
                }

                val instance = UserPreferencesRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}
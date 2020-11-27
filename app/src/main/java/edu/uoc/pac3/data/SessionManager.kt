package edu.uoc.pac3.data


import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import edu.uoc.pac3.R


/**
 * Created by alex on 06/09/2020.
 */

// Save a load tokens from sharedpreferences. A context where the values are stored is an input parametter.
// It is used the applicationcontext since it is unique and alive during all the lifecycle of the activity.
// Other context as the one attached to the main activity could have been used

class SessionManager(context: Context) {

    private val mContext: Context = context

    // Use of encrypted sharedpreferences in order hide the values accessible in /data/data/edu.uoc.pac3/shared_prefs
    //private val sharedPref: SharedPreferences = mContext.getSharedPreferences("tokens", Context.MODE_PRIVATE)
    // this is equivalent to using deprecated MasterKeys.AES256_GCM_SPEC
    var masterKey: MasterKey = MasterKey.Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

    private val sharedPref: SharedPreferences = EncryptedSharedPreferences.create(
            context,
            "secret_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val editor = sharedPref.edit()


    // Understand the user is not available sharedpreferences is null
    fun isUserAvailable(): Boolean {
        // TODO: Implement
        Log.d("cfauli", "isUserAvailable " + (getAccessToken() != null).toString())
        //return false
        return getAccessToken()!=null
    }


    fun getAccessToken(): String? {
        // TODO: Implement
        return sharedPref.getString(mContext.getString(R.string.accessToken), null)
    }


    fun saveAccessToken(accessToken: String) {
        //TODO("Save Access Token")
        with(editor) {
                putString(mContext.getString(R.string.accessToken), accessToken)
                commit()
        }
    }

    fun clearAccessToken() {
        //TODO("Clear Access Token")
        with(editor) {
            remove(mContext.getString(R.string.accessToken))
            commit()
        }
    }

    fun getRefreshToken(): String? {
        //TODO("Get Refresh Token")
        return sharedPref.getString(mContext.getString(R.string.refreshToken), null)
    }

    fun saveRefreshToken(refreshToken: String) {
        //TODO("Save Refresh Token")
        with(editor) {
            putString(mContext.getString(R.string.refreshToken), refreshToken)
            commit()
        }
    }

    fun clearRefreshToken() {
        //TODO("Clear Refresh Token")
        with(editor) {
            remove(mContext.getString(R.string.refreshToken))
            commit()
        }
    }


}
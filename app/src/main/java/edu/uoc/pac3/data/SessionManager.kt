package edu.uoc.pac3.data


import android.content.Context
import android.content.SharedPreferences
import edu.uoc.pac3.R
import kotlin.coroutines.coroutineContext


/**
 * Created by alex on 06/09/2020.
 */

class SessionManager(context: Context) {

    private val mContext: Context = context
    private val sharedPref: SharedPreferences = mContext.getSharedPreferences("adfsd", Context.MODE_PRIVATE)
    private val editor = sharedPref.edit()
    private val defaultValue = ""

    fun isUserAvailable(): Boolean {
        // TODO: Implement
        return false
    }

    fun getAccessToken(): String? {
        // TODO: Implement
        return sharedPref.getString(mContext.getString(R.string.accessToken), defaultValue)
    }


    fun saveAccessToken(accessToken: String) {
        //TODO("Save Access Token")
        with(editor) {
                putString(mContext.getString(R.string.accessToken), accessToken)
                commit()
        }
    }

    fun clearAccessToken() {
        TODO("Clear Access Token")
    }

    fun getRefreshToken(): String? {
        //TODO("Get Refresh Token")
        return sharedPref.getString(mContext.getString(R.string.refreshToken), defaultValue)
    }

    fun saveRefreshToken(refreshToken: String) {
        //TODO("Save Refresh Token")
        with (editor) {
            putString(mContext.getString(R.string.refreshToken), refreshToken)
            commit()
        }
    }

    fun clearRefreshToken() {
        TODO("Clear Refresh Token")
    }

}
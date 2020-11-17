package edu.uoc.pac3.data


import android.content.Context
import android.content.SharedPreferences
import edu.uoc.pac3.R


/**
 * Created by alex on 06/09/2020.
 */

class SessionManager(context: Context) {

    private val mContext: Context = context
    private val sharedPref: SharedPreferences = mContext.getSharedPreferences("tokens", Context.MODE_PRIVATE)
    private val editor = sharedPref.edit()
    private val defaultValue = ""

    fun isUserAvailable(): Boolean {
        // TODO: Implement
        return getAccessToken()!=null
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
        //TODO("Clear Access Token")
        with(editor) {
            remove(mContext.getString(R.string.accessToken))
            commit()
        }
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
        //TODO("Clear Refresh Token")
        with(editor) {
            remove(mContext.getString(R.string.refreshToken))
            commit()
        }
    }


}
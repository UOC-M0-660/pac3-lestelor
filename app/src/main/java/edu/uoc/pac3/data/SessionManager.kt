package edu.uoc.pac3.data


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.lifecycle.lifecycleScope
import edu.uoc.pac3.R
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.data.oauth.OAuthConstants
import edu.uoc.pac3.data.user.User
import edu.uoc.pac3.oauth.OAuthActivity
import edu.uoc.pac3.twitch.streams.StreamsActivity
import io.ktor.client.features.*
import kotlinx.android.synthetic.main.activity_oauth.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.coroutines.coroutineContext


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
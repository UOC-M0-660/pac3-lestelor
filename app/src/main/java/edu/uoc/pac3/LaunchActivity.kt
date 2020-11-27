package edu.uoc.pac3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import edu.uoc.pac3.oauth.LoginActivity
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.twitch.streams.StreamsActivity
import kotlinx.android.synthetic.main.activity_profile.*

class LaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)
        checkUserSession()
    }


    // Every time the app is closed the tokens are removed
    // Initially, the cookies are keps in order to avoid the registration process every time the
    // user enters the activity

    override fun onDestroy() {
        super.onDestroy()
        val sessionManager = SessionManager(applicationContext)

        Log.d("cfauli", "OnDestroy")
        sessionManager.clearAccessToken()
        sessionManager.clearRefreshToken()
    }

    // if there are no tokens, they must be renewed. It is performed in the LoginActivity.
    // Also, in the LoginActivity if there is no cache, the authentication process is initiated.

    private fun checkUserSession() {
        if (SessionManager(this).isUserAvailable()) {
            // User is available, open Streams Activity
            startActivity(Intent(this, StreamsActivity::class.java))
        } else {
            // User not available, request Login
            startActivity(Intent(this, LoginActivity::class.java))
        }
        finish()
    }

}

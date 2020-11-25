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

    override fun onDestroy() {
        super.onDestroy()
        val sessionManager = SessionManager(applicationContext)

        Log.d("cfauli", "OnDestroy")
        sessionManager.clearAccessToken()
        sessionManager.clearRefreshToken()
    }


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

package edu.uoc.pac3.twitch.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.CookieManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import edu.uoc.pac3.R
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.data.user.UserResponse
import edu.uoc.pac3.oauth.LoginActivity
import io.ktor.client.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.launch


class ProfileActivity : AppCompatActivity() {

    private val TAG = "ProfileActivity"
    private lateinit var httpClient: HttpClient
    private lateinit var twitchApiService: TwitchApiService

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        httpClient = Network.createHttpClient()
        twitchApiService = TwitchApiService(httpClient, applicationContext)

        getUser()
        setUpdateDescriptionButtonListener()
        setLogoutButtonListener()
    }

    fun getUser() {

        var userResponse: UserResponse? = null

        lifecycleScope.launch {
            userResponse = twitchApiService.getUser()
            Log.d("cfauli", TAG + " user size " + userResponse?.data?.size)
            userResponse?.let{
                updateView(it)
            }
        }
    }

    fun updateView(userResponse: UserResponse) {
        Picasso.get().load(userResponse.data?.get(0)?.imageUrl).into(imageView)
        userNameTextView.text = userResponse.data?.get(0)?.userName
        viewsText.text = userResponse.data?.get(0)?.views.toString()
        userDescriptionEditText.setText(userResponse.data?.get(0)?.description)
    }

    fun setUpdateDescriptionButtonListener() {
        updateDescriptionButton.setOnClickListener {
            lifecycleScope.launch {
                twitchApiService.updateUserDescription(userDescriptionEditText.text.toString())
            }
        }
    }
    fun setLogoutButtonListener(){
        val sessionManager = SessionManager(applicationContext)
        logoutButton.setOnClickListener {
            sessionManager.clearAccessToken()
            sessionManager.clearRefreshToken()

            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()

            val intent = Intent(this, LoginActivity::class.java)
            this.startActivity(intent)
        }
    }


}

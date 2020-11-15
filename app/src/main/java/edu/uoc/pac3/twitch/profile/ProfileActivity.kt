package edu.uoc.pac3.twitch.profile

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import edu.uoc.pac3.R
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.data.streams.StreamsResponse
import edu.uoc.pac3.data.user.User
import edu.uoc.pac3.data.user.UserResponse
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private val TAG = "ProfileActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        getUser()
    }

    fun getUser() {
        val httpClient = Network.createHttpClient()
        val twitchApiService = TwitchApiService(httpClient, applicationContext)
        var userResponse: UserResponse? = null

        lifecycleScope.launch {
            userResponse = twitchApiService.getUser()
            Log.d("cfauli", TAG + " user size" + userResponse?.data?.size)
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
}
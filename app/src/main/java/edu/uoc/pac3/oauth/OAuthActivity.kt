package edu.uoc.pac3.oauth

import android.app.Activity
import android.app.Application
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import edu.uoc.pac3.R
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Network.createHttpClient
import edu.uoc.pac3.data.oauth.OAuthConstants
import edu.uoc.pac3.data.oauth.OAuthConstants.authorizationUrl
import edu.uoc.pac3.data.oauth.OAuthTokensResponse
import edu.uoc.pac3.twitch.streams.StreamsActivity
import io.ktor.client.features.*
import io.ktor.client.features.get
import io.ktor.client.request.*
import kotlinx.android.synthetic.main.activity_oauth.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class OAuthActivity : AppCompatActivity() {

    private val TAG = "OAuthActivity"
    private val uniqueState = UUID.randomUUID().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oauth)
        launchOAuthAuthorization()
    }

    fun buildOAuthUri(): Uri {
        // TODO: Create URI
        val uri = Uri.parse(authorizationUrl)
                .buildUpon()
                .appendQueryParameter("client_id", OAuthConstants.clientID)
                .appendQueryParameter("redirect_uri", OAuthConstants.redirectUri)
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("scope", OAuthConstants.scopes.joinToString(separator = " "))
                .appendQueryParameter("state", uniqueState)
                .build()
        return uri
    }

    private fun launchOAuthAuthorization() {
        //  Create URI
        val uri = buildOAuthUri()

        // TODO: Set webView Redirect Listener
        // Set Redirect Listener
        val redirectUri = "http://localhost"
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                request?.let {
                    // Check if this url is our OAuth redirect, otherwise ignore it
                    if (request.url.toString().startsWith(redirectUri)) {
                        // To prevent CSRF attacks, check that we got the same state value we sent, otherwise ignore it
                        val responseState = request.url.getQueryParameter("state")
                        if (responseState == uniqueState) {
                            // This is our request, obtain the code!
                            request.url.getQueryParameter("code")?.let { code ->
                                // Got it!
                                Log.d("OAuth", "Here is the authorization code! $code")
                                onAuthorizationCodeRetrieved(code)
                                startActivityStreamsActivity()
                            } ?: run {
                                // User cancelled the login flow
                                // TODO: Handle error
                            }
                        }
                    }
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
        // Load OAuth Uri
        webView.settings.javaScriptEnabled = true
        webView.loadUrl(uri.toString())

    }

    // Call this method after obtaining the authorization code
    // on the WebView to obtain the tokens
    private fun onAuthorizationCodeRetrieved(authorizationCode: String) {

        // Show Loading Indicator
        progressBar.visibility = View.VISIBLE

        // TODO: Create Twitch Service
        val httpClient = createHttpClient()


        // TODO: Get Tokens from Twitch
        // TODO: Save access token and refresh token using the SessionManager class
        GlobalScope.launch(Dispatchers.IO) {
            val response = TwitchApiService(httpClient).getTokens(authorizationCode)
            response?.accessToken?.let { SessionManager(this@OAuthActivity).saveAccessToken(it) }
            response?.refreshToken?.let { SessionManager(this@OAuthActivity).saveRefreshToken(it) }
        }


    }


    fun startActivityStreamsActivity() {
        val intent = Intent(this, StreamsActivity::class.java)
        this.startActivity(intent)
    }
}


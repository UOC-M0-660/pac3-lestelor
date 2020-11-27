package edu.uoc.pac3.oauth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import edu.uoc.pac3.R
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Endpoints
import edu.uoc.pac3.data.network.Network.createHttpClient
import edu.uoc.pac3.data.oauth.OAuthConstants
import edu.uoc.pac3.twitch.streams.StreamsActivity
import io.ktor.client.features.*
import kotlinx.android.synthetic.main.activity_oauth.*
import kotlinx.coroutines.Dispatchers
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

    // The httpclient is not used. The login is performed directly via a web conexion
    // It launches a webview with Java enabled and a listener for the response, that contains the authorization code
    // necessary to retrieve the tokens (see that this code is not necessary for refreshing the tokens since only the
    // refresh code is used)


    private fun launchOAuthAuthorization() {
        //  Create URI
        val uri = buildOAuthUri()

        // Load OAuth Uri
        with(Dispatchers.IO) {
            // TODO: Set webView Redirect Listener
            // Set Redirect Listener
            setWebViewRedirectListener()
            webView.settings.javaScriptEnabled = true
            webView.loadUrl(uri.toString())
        }

    }

    fun buildOAuthUri(): Uri {
        // TODO: Create URI
        val uri = Uri.parse(Endpoints.authorizationUrl)
                .buildUpon()
                .appendQueryParameter("client_id", OAuthConstants.clientID)
                .appendQueryParameter("redirect_uri", OAuthConstants.redirectUri)
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("scope", OAuthConstants.scopes.joinToString(separator = " "))
                .appendQueryParameter("state", uniqueState)
                .build()
        return uri
    }


    // Listens for the parameter code in the http response
    // If the case, stops the progressbar and starts the process for getting the tokens

    fun setWebViewRedirectListener() {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                request?.let {
                    // Check if this url is our OAuth redirect, otherwise ignore it
                    if (request.url.toString().startsWith(OAuthConstants.redirectUri)) {
                        // To prevent CSRF attacks, check that we got the same state value we sent, otherwise ignore it
                        val responseState = request.url.getQueryParameter("state")
                        if (responseState == uniqueState) {
                            // This is our request, obtain the code!
                            request.url.getQueryParameter("code")?.let { code ->
                                // Got it!
                                // Show Loading Indicator
                                progressBar.visibility = View.VISIBLE
                                try {
                                    onAuthorizationCodeRetrieved(code)
                                    webView.visibility = View.GONE
                                    progressBar.visibility = View.VISIBLE
                                }catch (e: ClientRequestException){
                                    e.printStackTrace()
                                    Log.d("cfauli", TAG + " " + getString(R.string.error_oauth))
                                }
                            } ?: run {
                                // User cancelled the login flow
                                // TODO: Handle error
                                // Not sure what to do, but better start the login again
                                Log.d("cfauli", TAG + " " + getString(R.string.error_oauth))
                                Toast.makeText(this@OAuthActivity,  "Login not successful. Please, try again", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@OAuthActivity, LoginActivity::class.java))
                            }
                        }
                    }
                }
                return super.shouldOverrideUrlLoading(view, request)
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                val cookies = CookieManager.getInstance().getCookie(url)
                Log.d("cfauli", TAG + " cookies $cookies")
            }
        }
    }


    // Call this method after obtaining the authorization code
    // on the WebView to obtain the tokens
    private fun onAuthorizationCodeRetrieved(authorizationCode: String) {

        // TODO: Create Twitch Service
        val httpClient = createHttpClient()
        val twitchApiService = TwitchApiService(httpClient)

        // TODO: Get Tokens from Twitch
        // TODO: Save access token and refresh token using the SessionManager class
        lifecycleScope.launch {
            val response = twitchApiService.getTokens(authorizationCode)
            val sessionManager = SessionManager(applicationContext)
            response?.let {
                sessionManager.saveAccessToken(it.accessToken)
                it.refreshToken?.let { it1 -> sessionManager.saveRefreshToken(it1) }
                httpClient.close()
                startActivityStreamsActivity()
            } ?: run {
                webView.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
                Log.d("cfauli", TAG + " " + getString(R.string.error_oauth))
            }
        }


    }

    // Show streams activity. Must be put out of the coroutine context.
    fun startActivityStreamsActivity() {
        val intent = Intent(this, StreamsActivity::class.java)
        startActivity(intent)
    }

}


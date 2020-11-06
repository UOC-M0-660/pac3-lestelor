package edu.uoc.pac3.data

import android.util.Log
import edu.uoc.pac3.data.oauth.OAuthConstants.clientID
import edu.uoc.pac3.data.oauth.OAuthConstants.clientSecret
import edu.uoc.pac3.data.oauth.OAuthConstants.redirectUri
import edu.uoc.pac3.data.oauth.OAuthConstants.tokenUrl
import edu.uoc.pac3.data.oauth.OAuthTokensResponse
import edu.uoc.pac3.data.oauth.UnauthorizedException
import edu.uoc.pac3.data.streams.StreamsResponse
import edu.uoc.pac3.data.user.User
import io.ktor.client.*
import io.ktor.client.request.*

/**
 * Created by alex on 24/10/2020.
 */

class TwitchApiService(private val httpClient: HttpClient) {
    private val TAG = "TwitchApiService"

    /// Gets Access and Refresh Tokens on Twitch
    suspend fun getTokens(authorizationCode: String): OAuthTokensResponse? {
        //TODO("Get Tokens from Twitch")
        val response =  httpClient.post<OAuthTokensResponse>(tokenUrl) {
            parameter("client_id", clientID)
            parameter("client_secret", clientSecret)
            parameter("code", authorizationCode)
            parameter("grant_type", "authorization_code")
            parameter("redirect_uri", redirectUri)
        }
        Log.d("OAuth", "Access Token: ${response.accessToken}. Refresh Token: ${response.refreshToken}")
        return response
    }

    /// Gets Streams on Twitch
    @Throws(UnauthorizedException::class)
    suspend fun getStreams(cursor: String? = null): StreamsResponse? {
        TODO("Get Streams from Twitch")
        TODO("Support Pagination")
    }

    /// Gets Current Authorized User on Twitch
    @Throws(UnauthorizedException::class)
    suspend fun getUser(): User? {
        TODO("Get User from Twitch")
    }

    /// Gets Current Authorized User on Twitch
    @Throws(UnauthorizedException::class)
    suspend fun updateUserDescription(description: String): User? {
        TODO("Update User Description on Twitch")
    }
}
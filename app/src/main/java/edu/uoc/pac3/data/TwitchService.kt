package edu.uoc.pac3.data

import android.content.Context
import android.net.Uri
import android.util.Log
import edu.uoc.pac3.data.network.Endpoints
import edu.uoc.pac3.data.oauth.OAuthConstants
import edu.uoc.pac3.data.oauth.OAuthTokensResponse
import edu.uoc.pac3.data.oauth.UnauthorizedException
import edu.uoc.pac3.data.streams.StreamsResponse
import edu.uoc.pac3.data.user.UserResponse
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*

/**
 * Created by alex on 24/10/2020.
 */

class TwitchApiService(private val httpClient: HttpClient, context: Context) {
    private val TAG = "TwitchApiService"
    val accessToken = SessionManager(context).getAccessToken()
    val refreshToken = SessionManager(context).getRefreshToken()


    /// Gets Access and Refresh Tokens on Twitch
    suspend fun getTokens(authorizationCode: String): OAuthTokensResponse? {
        //TODO("Get Tokens from Twitch")
        val response =  httpClient.post<OAuthTokensResponse>(Endpoints.tokensTwitchUrl) {
            headers {
                append("Authorization", "token")
            }

            parameter("client_id", OAuthConstants.clientID)
            parameter("client_secret", OAuthConstants.clientSecret)
            parameter("code", authorizationCode)
            parameter("grant_type", "authorization_code")
            parameter("redirect_uri", OAuthConstants.redirectUri)
            body = "Command"
        }
        Log.d("cfauli", TAG + " Access Token: ${response.accessToken} Refresh Token: ${response.refreshToken}")
        return response
    }

    /// Gets Streams on Twitch
    @Throws(UnauthorizedException::class)
    suspend fun getStreams(cursor: String? = null): StreamsResponse?  {
        //TODO("Get Streams from Twitch")
        Log.d("cfauli", TAG + " getStreams cursor $cursor")
        var response: StreamsResponse? = null
        try {
            response = httpClient.get<StreamsResponse>(Endpoints.liveStreamsTwitchUrl) {
                headers {
                    append("Client-Id", OAuthConstants.clientID)
                    append("Authorization", "Bearer $accessToken")
                }
                // TODO("Support Pagination")
                cursor?.let {
                    parameter("after", cursor)
                }
            }
            Log.d("cfauli", "getStreams  " +response.data?.size +" "+ response.pagination)
            return response
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is ClientRequestException && e.response?.status?.value == 401) {
                throw UnauthorizedException
            }
            Log.d("cfauli", "getStreams error $e")
            return null
        }

    }



    /// Gets Current Authorized User on Twitch
    @Throws(UnauthorizedException::class)
    suspend fun getUser(): UserResponse? {
        //TODO("Get User from Twitch")
        Log.d("cfauli", TAG + " getUser " + accessToken)
        var response: UserResponse? = null
        try {
            response = httpClient.get<UserResponse>(Endpoints.userTwitchUrl) {
                headers {
                    append("Authorization", "Bearer $accessToken")
                    append("Client-Id", OAuthConstants.clientID)
                }
            }
            Log.d("cfauli", "getUser size " + response.data?.size)
            return response
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("cfauli", TAG + " getUser error $e")
            return null
        }


    }

    /// Gets Current Authorized User on Twitch
    @Throws(UnauthorizedException::class)
    suspend fun updateUserDescription(description: String): UserResponse? {
        //TODO("Update User Description on Twitch")
        Log.d("cfauli", TAG + " updateUser " + accessToken)
        var response: UserResponse? = null
        try {
            response = httpClient.put<UserResponse>(Endpoints.userTwitchUrl) {
                headers {
                    append("Authorization", "Bearer $accessToken")
                    append("Client-Id", OAuthConstants.clientID)
                }
                parameter("description",description)
            }
            Log.d("cfauli", "updateUser size " + response.data?.size)
            return response
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("cfauli", TAG + " updateUser error $e")
            return null
        }
    }


    @Throws(ClientRequestException::class)
    suspend fun getRefreshToken(refreshToken: String): OAuthTokensResponse? {
        var url = Uri.parse(Endpoints.tokensTwitchUrl)
            .buildUpon()
            .appendQueryParameter("client_id", OAuthConstants.clientID)
            .appendQueryParameter("client_secret", OAuthConstants.clientSecret)
            .appendQueryParameter("refresh_token", refreshToken)
            .appendQueryParameter("grant_type", "refresh_token")
            .build()

        val response = httpClient.post<OAuthTokensResponse>(url.toString())
        return response
    }

}
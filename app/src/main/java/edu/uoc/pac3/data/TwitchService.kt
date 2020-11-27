package edu.uoc.pac3.data

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
import kotlinx.coroutines.Dispatchers

/**
 * Created by alex on 24/10/2020.
 */

class TwitchApiService(private val httpClient: HttpClient) {
    private val TAG = "TwitchApiService"


    /// Gets Access and Refresh Tokens on Twitch
    //https://dev.twitch.tv/docs/authentication/getting-tokens-oauth#oauth-authorization-code-flow
    //https://medium.com/l-r-engineering/oauth2-in-android-authorization-code-flow-ffc4355dd473
    
    suspend fun getTokens(authorizationCode: String): OAuthTokensResponse? = with (Dispatchers.IO){
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
        Log.d("cfauli", TAG + "getTokens Access Token: ${response.accessToken} Refresh Token: ${response.refreshToken}")
        return response
    }

    /// Gets Streams on Twitch
    // https://dev.twitch.tv/docs/api/reference#get-streams
    // The output must be the same as in https://www.twitch.tv/directory/all?sort=VIEWER_COUNT

    // In case of error, check if 401 and then refresh tokens and try again
    // Actually, if 401, it keeps trying until it get the streams. It could be improved in order to
    // repeat the cycle only a specific number of times

    @Throws(UnauthorizedException::class)
    suspend fun getStreams(cursor: String? = null, accessToken: String, refreshToken: String): StreamsResponse? = with (Dispatchers.IO)  {
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
                Log.d("cfauli",TAG + " getStreams error 401 tokensi $accessToken $refreshToken")
                val oAuthTokensResponse: OAuthTokensResponse? = refreshTokens(refreshToken)
                oAuthTokensResponse?.let {
                    Log.d("cfauli",TAG + " getStreams error 401 tokens2 " + oAuthTokensResponse.accessToken + " " + oAuthTokensResponse.refreshToken)
                    oAuthTokensResponse.refreshToken?.let { it1 -> response = getStreams(cursor,oAuthTokensResponse.accessToken, it1) }
                }?: throw UnauthorizedException
                return response
            } else return null
        }

    }



    /// Gets data of Current Authorized User on Twitch: username, description, view_count and profile_image_url
    // As in https://dev.twitch.tv/docs/api/reference#get-users
    // It shows the list of users so it is stored in a list (possibility to send different users to fetch the data)
    // The first in the list is the actual user.

    @Throws(UnauthorizedException::class)
    suspend fun getUser(accessToken: String): UserResponse? = with (Dispatchers.IO) {
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

    /// Update User Data on Twitch
    // https://dev.twitch.tv/docs/api/reference#update-user

    @Throws(UnauthorizedException::class)
    suspend fun updateUserDescription(description: String, accessToken: String): UserResponse? = with (Dispatchers.IO){
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

    // https://medium.com/l-r-engineering/oauth2-in-android-authorization-code-flow-ffc4355dd473
    // The refresh token is used to get new tokens

    @Throws(ClientRequestException::class)
    suspend fun refreshTokens(refreshToken: String): OAuthTokensResponse? = with (Dispatchers.IO) {
        val url = Uri.parse(Endpoints.tokensTwitchUrl)
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
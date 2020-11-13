package edu.uoc.pac3.data

import android.util.Log
import edu.uoc.pac3.data.network.Endpoints
import edu.uoc.pac3.data.oauth.OAuthConstants.clientID
import edu.uoc.pac3.data.oauth.OAuthConstants.clientSecret
import edu.uoc.pac3.data.oauth.OAuthConstants.redirectUri
import edu.uoc.pac3.data.oauth.OAuthTokensResponse
import edu.uoc.pac3.data.oauth.UnauthorizedException
import edu.uoc.pac3.data.streams.Pagination
import edu.uoc.pac3.data.streams.StreamsResponse
import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*

/**
 * Created by alex on 24/10/2020.
 */

class TwitchApiService(private val httpClient: HttpClient) {
    private val TAG = "TwitchApiService"

    /// Gets Access and Refresh Tokens on Twitch
    suspend fun getTokens(authorizationCode: String): OAuthTokensResponse? {
        //TODO("Get Tokens from Twitch")
        val response =  httpClient.post<OAuthTokensResponse>(Endpoints.tokensTwitch) {
            headers {
                append("Authorization", "token")
            }

            parameter("client_id", clientID)
            parameter("client_secret", clientSecret)
            parameter("code", authorizationCode)
            parameter("grant_type", "authorization_code")
            parameter("redirect_uri", redirectUri)
            body = "Command"
        }
        Log.d("OAuth", "Access Token: ${response.accessToken}. Refresh Token: ${response.refreshToken}")
        return response
    }

    /// Gets Streams on Twitch
    @Throws(UnauthorizedException::class)
    suspend fun getStreams(accessToken: String, cursor: String? = null): StreamsResponse?  {
        //TODO("Get Streams from Twitch")
        Log.d("cfauli", "getStreams  accesstoken $accessToken cursor $cursor")
        var response: StreamsResponse? = null
        try {
            response = httpClient.get<StreamsResponse>(Endpoints.liveStreamsTwitch) {
                headers {
                    append("Client-Id", clientID)
                    append("Authorization", "Bearer $accessToken")
                }
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

    /*    }else{
            // TODO("Support Pagination")
            val response = httpClient.get<StreamsResponse>(Endpoints.liveStreamsTwitch) {
                headers {
                    append("first", numberStreams)
                    //append("after", "Bearer $cursor")
                    append("after", "Bearer $cursor")
                    append("Client-Id", clientID)
                }
            }
            return response
        }

    }


    /// Gets Streams on Twitch
    @Throws(UnauthorizedException::class)
    suspend fun getStreams2(cursor: String? = null): StreamsResponse? = withContext(Dispatchers.IO){
        return@withContext try {
            httpClient.get<StreamsResponse>(Endpoints.streamsUrl) {
                header("client-id", OAuthConstants.clientId)
                cursor?.let {
                    parameter("after", cursor)
                }
            }
        }catch (e: Exception){
            e.printStackTrace()
            if (e is ClientRequestException && e.response?.status?.value == 401){
                throw UnauthorizedException
            }
            null
        }
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


    @Throws(ClientRequestException::class)
    suspend fun getRefreshToken(refreshToken: String): OAuthTokensResponse? {
        var url = Uri.parse(Endpoints.tokensTwitch)
            .buildUpon()
            .appendQueryParameter("client_id", OAuthConstants.clientID)
            .appendQueryParameter("client_secret", OAuthConstants.clientSecret)
            .appendQueryParameter("refresh_token", refreshToken)
            .appendQueryParameter("grant_type", "refresh_token")
            .build()

        val response = httpClient.post<OAuthTokensResponse>(url.toString())
        return response
    }
*/
}
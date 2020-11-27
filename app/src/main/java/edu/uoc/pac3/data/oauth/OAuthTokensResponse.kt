package edu.uoc.pac3.data.oauth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by alex on 07/09/2020.
 */

// Must be serializable since the TwitchService functions are suspended. Each http response parameter is
// mapped to a variable

@Serializable
data class OAuthTokensResponse (
    @SerialName("access_token") val accessToken: String,
    @SerialName("refresh_token") val refreshToken: String? = null,
    @SerialName("expires_in") val expiresInSeconds: Int? = null,
    @SerialName("token_type") val tokenType: String? = null,
    @SerialName("scope") val scopes: List<String>? = null,
    )
package edu.uoc.pac3.data.user

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by alex on 07/09/2020.
 */

// Must be serializable since the TwitchService functions are suspended. Each http response parameter is
// mapped to a variable

@Serializable
data class User(
        @SerialName("display_name") val userName: String = "",
        @SerialName("description") val description: String = "",
        @SerialName("view_count") val views: Int = 0,
        @SerialName("profile_image_url") val imageUrl: String = "",
)

@Serializable
data class UserResponse(
        @SerialName("data") val data: List<User>? = null,
)
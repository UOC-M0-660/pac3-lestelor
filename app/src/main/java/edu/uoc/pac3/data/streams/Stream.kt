package edu.uoc.pac3.data.streams

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Created by alex on 07/09/2020.
 */
/*
@Serializable
data class Stream(
        @SerialName("user_name") val userName: String?,
        @SerialName("title") val title: String,
        @SerialName("thumbnail_url") val thumbnailUrl: String,

)

@Serializable
data class StreamsResponse(
    val data: List<Stream>? = null,
)*/

@Serializable
data class Stream(
    val userName: String?,
    val title: String,
    val thumbnailUrl: String,
)


data class StreamsResponse(
    val data: List<Stream>? = null,
)
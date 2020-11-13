package edu.uoc.pac3.data.oauth

import android.content.Context
import io.ktor.client.engine.*

/**
 * Created by alex on 07/09/2020.
 */
object OAuthConstants {

    // TODO: Set OAuth2 Variables
    const val tokenUrl = "https://id.twitch.tv/oauth2/token"
    const val authorizationUrl = "https://id.twitch.tv/oauth2/authorize"
    const val streamsUrl = "https://api.twitch.tv/helix/streams"
    const val clientID = "kliszzizapmkgkzpgbs0mlc531qxrj"
    // TODO: seems strange that the client secret is in the app
    const val clientSecret = "w52mk080xjl1m0cg8vrnc25l4480wj"
    const val redirectUri = "http://localhost"
    //val redirectUri = "http://10.0.2.2"
    //val scopes = listOf("user:read:email", "bits:read", "channel:read:hype_train", "analytics:read:games")
    val scopes = listOf("user:read:email","user:edit")

}
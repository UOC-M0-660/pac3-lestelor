package edu.uoc.pac3.data.oauth

/**
 * Created by alex on 07/09/2020.
 */

object OAuthConstants {

        init {
            System.loadLibrary("native-lib")
        }
        external fun stringFromJNI(): String


    // TODO: Set OAuth2 Variables

    const val clientID = "kliszzizapmkgkzpgbs0mlc531qxrj"
    // TODO: seems strange that the client secret is in the app
    val clientSecret = stringFromJNI()
    const val redirectUri = "http://localhost"
    //val redirectUri = "http://10.0.2.2"
    //val scopes = listOf("user:read:email", "bits:read", "channel:read:hype_train", "analytics:read:games")
    val scopes = listOf("user:read:email","user:edit")

}
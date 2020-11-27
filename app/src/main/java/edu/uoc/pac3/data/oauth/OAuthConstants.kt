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
    // The secret is stored in a c++ function native-lib.cpp.
    // See https://www.freecodecamp.org/news/c-usage-in-android-4b57edf84322/
    val clientSecret = stringFromJNI()
    // It has to be the same as declared in twitch
    const val redirectUri = "http://localhost"
    //when you request authorization from users, the URL scope parameter allows you to specify which permissions
    // your app requires. These scopes are tied to the access token you receive on successful authorization. Without
    // specifying scopes, your app can access only basic information about the authenticated user.
    //val scopes = listOf("user:read:email", "bits:read", "channel:read:hype_train", "analytics:read:games")
    val scopes = listOf("user:read:email","user:edit")

}
package edu.uoc.pac3.data.network

/**
 * Created by alex on 07/09/2020.
 */
object Endpoints {

    // OAuth2 API Endpoints
    private const val oauthBaseUrl = "https://id.twitch.tv/oauth2"
    // TODO: Add all remaining endpoints

    // Twitch API Endpoints
    const val twitchBaseUrl = "https://api.twitch.tv/helix"

    // TODO: Add all remaining endpoints

    const val tokensTwitchUrl =  oauthBaseUrl + "/token"
    //Revoke User tokens:
    const val logoutTwitchUrl =  oauthBaseUrl + "/revoke"
    //Live Streams:
    const val liveStreamsTwitchUrl =  twitchBaseUrl + "/streams"
    //Get Info User:
    const val userTwitchUrl =  twitchBaseUrl + "/users"
    // Get authorization
    const val authorizationUrl = oauthBaseUrl + "/authorize"
    // Check user Status
    const val checkUserStatusUrl = oauthBaseUrl + "/validate"


}
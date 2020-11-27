package edu.uoc.pac3.data.network

/**
 * Created by alex on 07/09/2020.
 */
object Endpoints {

    // OAuth2 API Endpoints
    private const val oauthBaseUrl = "https://id.twitch.tv/oauth2"
    // Twitch API Endpoints
    private const val twitchBaseUrl = "https://api.twitch.tv/helix"

    // TODO: Add all remaining endpoints
    //Get tokens
    const val tokensTwitchUrl =  oauthBaseUrl + "/token"
    //Get Streams:
    const val liveStreamsTwitchUrl =  twitchBaseUrl + "/streams"
    //Get Info User:
    const val userTwitchUrl =  twitchBaseUrl + "/users"
    // Get authorization
    const val authorizationUrl = oauthBaseUrl + "/authorize"



}
package edu.uoc.pac3.data.oauth

import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Network
import kotlinx.android.synthetic.main.activity_profile.*

/**
 * Created by alex on 24/10/2020.
 */


/*sealed class OAuthException: Throwable()*/
sealed class OAuthException  : Exception() {

}


// Use this exception to indicate user is not authorized
// Can be throw in the network layer and caught in the Activities
object UnauthorizedException : OAuthException()
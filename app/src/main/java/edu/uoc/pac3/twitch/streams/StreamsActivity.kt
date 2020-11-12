package edu.uoc.pac3.twitch.streams

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.uoc.pac3.R
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.data.streams.Stream
import edu.uoc.pac3.data.streams.StreamsResponse
import edu.uoc.pac3.oauth.LoginActivity
import io.ktor.client.features.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class StreamsActivity : AppCompatActivity() {

    private val TAG = "StreamsActivity"
    private lateinit var adapter: TwitchListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streams)

        // Init RecyclerView
        initRecyclerView()
        // TODO: Get Streams

        val accessToken = SessionManager(this).getAccessToken()
        val refreshToken = SessionManager(this).getRefreshToken()
        loadStreams(accessToken)
        Log.d("cfauli", "access token " + accessToken)


    }

    private fun initRecyclerView(): TwitchListAdapter {
        // TODO: Implement
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        // Set Layout Manager
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        // Init Adapter
        adapter = TwitchListAdapter(listOf())
        recyclerView.adapter = adapter
        return adapter
    }

     fun loadStreams(cursor:String? = null) {
        val httpClient = Network.createHttpClient()
        val twitchApiService = TwitchApiService(httpClient)

        lifecycleScope.launch {
            // Envio cursor
            var streamsResponse: StreamsResponse? = twitchApiService.getStreams()
            try {
                if (cursor==null)        streamsResponse = twitchApiService.getStreams()
                else  streamsResponse = twitchApiService.getStreams(cursor)
            } catch (e: ClientRequestException) {
            val cursor = refreshToken(twitchApiService)
            val httpClient2 = Network.createHttpClient()
            val twitchApiService2 = TwitchApiService(httpClient2)
            try {
                streamsResponse = twitchApiService2.getStreams(cursor)
            } catch (e: ClientRequestException) {
                Toast.makeText( applicationContext, "Es necesario hacer logout", Toast.LENGTH_SHORT).show()
            } finally {
                httpClient2.close()
            }
        } finally {
            httpClient.close()
        }
            streamsResponse?.let {
                streamsResponse.data?.let {streams->
                        adapter.setStreams(streams)
                }
                /*streamsResponse.pagination?.cursor?.let {
                    cursor = it
                }*/
            }?: run {
                Toast.makeText(this@StreamsActivity, getString(R.string.error_streams), Toast.LENGTH_SHORT).show()
            }
        }



     }


    private suspend fun refreshToken(service: TwitchApiService): String? {
        val sessionManager = SessionManager(this)
        sessionManager.clearAccessToken()
        var cursor: String? = null
        try {
            sessionManager.getRefreshToken()?.let {
                service.getRefreshToken(it)?.let { tokensResponse ->
                    Log.i(TAG, "refreshToken: nuevo access token ${tokensResponse.accessToken}")
                    sessionManager.saveAccessToken(tokensResponse.accessToken)
                    tokensResponse.refreshToken?.let {
                        sessionManager.saveRefreshToken(it)
                        cursor = tokensResponse.accessToken
                    }
                }

            }
        }catch (e: ClientRequestException){
            Toast.makeText( applicationContext, "Ocurrio un error al momento de refrescar Token", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }
        return cursor
    }

}






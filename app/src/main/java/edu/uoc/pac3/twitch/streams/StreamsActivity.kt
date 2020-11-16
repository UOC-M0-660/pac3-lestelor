package edu.uoc.pac3.twitch.streams

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import edu.uoc.pac3.R
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.data.streams.Pagination
import edu.uoc.pac3.data.streams.StreamsResponse
import edu.uoc.pac3.twitch.profile.ProfileActivity
import io.ktor.client.features.*
import kotlinx.android.synthetic.main.activity_streams.*
import kotlinx.coroutines.launch


class StreamsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = "StreamsActivity"
    private lateinit var adapter: TwitchListAdapter
    private var cursor: Pagination? = null
    private lateinit var scrollListener: RecyclerView.OnScrollListener
    private lateinit var layoutManager: LinearLayoutManager
    private  var streamsResponseComplete: StreamsResponse = StreamsResponse()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_streams)

        val toolbar = initToolbar()
        initDrawerLayout(toolbar)
        initMenuLayout()
        initSwipeRefreshListener()

        // Init RecyclerView
        initRecyclerView()
        // TODO: Get Streams
        updateStreams()
    }



    private fun initRecyclerView(): TwitchListAdapter {
        // TODO: Implement
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        // Set Layout Manager
        layoutManager= LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        // Init Adapter
        adapter = TwitchListAdapter(StreamsResponse(),0,0)
        recyclerView.adapter = adapter
        // set onscroll listener
        setRecyclerViewScrollListener(recyclerView)
        return adapter
    }

    fun updateStreams() {
        Log.d("cfauli", TAG + " cursor: " + cursor?.cursor )
        lifecycleScope.launch {
            val streamResponse = getStreams(cursor)
            Log.d("cfauli", "StreamsActivity " + streamResponse?.data?.size)
            streamResponse?.let{
                updateRecyclerView(it)
                cursor = streamResponse.pagination
            }
        }
    }

     suspend fun getStreams(cursor: Pagination? = null): StreamsResponse? {
        val httpClient = Network.createHttpClient()
        val twitchApiService = TwitchApiService(httpClient, applicationContext)
        var streamsResponse: StreamsResponse? = null


        try {
            streamsResponse = twitchApiService.getStreams(cursor?.cursor)
            httpClient.close()
        } catch (e: ClientRequestException) {
            Log.d("cfauli", "getStreams error $e")
        }


         return streamsResponse

            /*if (cursor==null)        streamsResponse = twitchApiService.getStreams()
            else  streamsResponse = twitchApiService.getStreams(cursor)

            refreshToken(twitchApiService)
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
                //streamsResponse.pagination?.cursor?.let {
                    //cursor = it
                //}
                cursor = 10.toString()
            }?: run {
                Toast.makeText(this@StreamsActivity, getString(R.string.error_streams), Toast.LENGTH_SHORT).show()
            }
        }*/



     }

    private  fun updateRecyclerView(streamsResponse: StreamsResponse) {
        val itemStart = streamsResponseComplete.data?.size ?: 0
        val itemCount = streamsResponse.data?.size ?: 0

        streamsResponseComplete.data = streamsResponseComplete.data.orEmpty() + streamsResponse.data.orEmpty()
        adapter.setStreams(streamsResponseComplete, itemStart, itemCount)
    }


    /*private suspend fun refreshToken(service: TwitchApiService): String? {
        val sessionManager = SessionManager(this)
        sessionManager.clearAccessToken()
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
    }*/

    private fun setRecyclerViewScrollListener(recyclerView: RecyclerView) {
        scrollListener = object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val lastItem = layoutManager.findLastCompletelyVisibleItemPosition()
                val itemCount = adapter.itemCount
                Log.d("cfauli", "StreamsActivity scroll $dx $dy $lastItem $itemCount")
                if (lastItem + 1 == itemCount) {
                    Log.d("cfauli", "StreamsActivity cursor $cursor")
                    updateStreams()
                }
            }
        }
        recyclerView.addOnScrollListener(scrollListener)
    }


    private fun initToolbar(): Toolbar {
        val toolbar = findViewById<Toolbar>(R.id.activity_streams_toolbar)
        toolbar.title = getString(R.string.activity_streams_title)
        return toolbar
    }

    fun initDrawerLayout(toolbar: Toolbar) {
        val toggle = ActionBarDrawerToggle(
                this, activity_streams_drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        activity_streams_drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    fun initMenuLayout() {
        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        //TODO("Implement menu")
        when (item.itemId) {

            R.id.menu_user -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            /*R.id.menu_exit -> {
                finish()
                exitProcess(0)
            }*/
            else -> {
                Toast.makeText(this,"menu option not implemented!!",Toast.LENGTH_SHORT).show()
            }
        }


        if (activity_streams_drawer.isDrawerOpen(GravityCompat.START))
            activity_streams_drawer.closeDrawer(GravityCompat.START)
        return true
    }

    fun initSwipeRefreshListener () {
        swipeRefreshLayout.setOnRefreshListener {
            TODO("Implement action")
            // refresh your list contents somehow

            // reset the SwipeRefreshLayout (stop the loading spinner)
            swipeRefreshLayout.isRefreshing = false
        }
    }

}







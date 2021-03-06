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
import edu.uoc.pac3.data.SessionManager
import edu.uoc.pac3.data.TwitchApiService
import edu.uoc.pac3.data.network.Network
import edu.uoc.pac3.data.streams.Pagination
import edu.uoc.pac3.data.streams.StreamsResponse
import edu.uoc.pac3.oauth.OAuthActivity
import edu.uoc.pac3.twitch.profile.ProfileActivity
import io.ktor.client.features.*
import kotlinx.android.synthetic.main.activity_streams.*
import kotlinx.coroutines.launch
import kotlin.system.exitProcess


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

        // A toolbar is necessary to put the menu ant the left side
        val toolbar = initToolbar()

        // Init the menu options as dropdown where the user can go to their profile, exit the activity etc.
        initDrawerLayout(toolbar)
        initMenuLayout()
        // Actions to do when the window is swiped down
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
        adapter = TwitchListAdapter(StreamsResponse(), 0, 0)
        recyclerView.adapter = adapter
        // set onscroll listener
        setRecyclerViewScrollListener(recyclerView)
        return adapter
    }

    // if no parameter is passed, reload is false
    // reload is true when the windows is swiped down and then passed to the updaterecyclerciew function

    fun updateStreams(reload: Boolean = false) {
        Log.d("cfauli", TAG + " cursor: " + cursor?.cursor)
        lifecycleScope.launch {
            val streamResponse = getStreams(cursor)
            Log.d("cfauli", "StreamsActivity " + streamResponse?.data?.size)
            streamResponse?.let{
                updateRecyclerView(reload, it)
                cursor = streamResponse.pagination
            }
        }
    }


    // call the getStreams TwitchService function passing the tokens that are stored in the sharedpreferences
    // If no response call the OAuthActivity where new token would be retrieved

     suspend fun getStreams(cursor: Pagination? = null): StreamsResponse? {
         val httpClient = Network.createHttpClient()
         val twitchApiService = TwitchApiService(httpClient)
         val sessionManager = SessionManager(applicationContext)
         val accessToken= sessionManager.getAccessToken()
         val refreshToken = sessionManager.getRefreshToken()
         Log.d("cfauli", TAG + " tokens $accessToken $refreshToken")
         var streamsResponse: StreamsResponse? = null

        if (accessToken!=null && refreshToken!=null && refreshToken.length > 1) {
            try {
                streamsResponse = twitchApiService.getStreams(
                    cursor?.cursor,
                    accessToken,
                    refreshToken
                )
                httpClient.close()
            } catch (e: ClientRequestException) {
                Log.d("cfauli", "getStreams error $e")
            }
        } else {
            startActivity(Intent(this, OAuthActivity::class.java))
            finish()
        }

         return streamsResponse

     }


    // update the adapter with the 20 new streams
    // If this call comes from a reload then the list of streams is initialised again with the first 20 entries
    private  fun updateRecyclerView(reload: Boolean? = null, streamsResponse: StreamsResponse) {
        val itemStart = streamsResponseComplete.data?.size ?: 0
        val itemCount = streamsResponse.data?.size ?: 0

        if (reload == false) streamsResponseComplete.data = streamsResponseComplete.data.orEmpty() + streamsResponse.data.orEmpty()
            else streamsResponseComplete.data = streamsResponse.data.orEmpty()

        adapter.setStreams(streamsResponseComplete, itemStart, itemCount)
    }


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

   // Init toolbar with the name
    private fun initToolbar(): Toolbar {
        val toolbar = findViewById<Toolbar>(R.id.activity_streams_toolbar)
        toolbar.title = getString(R.string.activity_streams_title)
        return toolbar
    }


    //  Create the menu
    fun initDrawerLayout(toolbar: Toolbar) {
        val toggle = ActionBarDrawerToggle(
            this,
            activity_streams_drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        activity_streams_drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    // Listen the option selected
    fun initMenuLayout() {
        nav_view.setNavigationItemSelectedListener(this)
    }

    // In case profile item selected, launch ProfileActivity
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        //TODO("Implement menu")
        when (item.itemId) {

            R.id.menu_user -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)

            }
            R.id.menu_exit -> {
                finish()
                exitProcess(0)
            }
            else -> {
                Toast.makeText(this, "menu option not implemented!!", Toast.LENGTH_SHORT).show()
            }
        }

        // Close drawer when anything is selected
        if (activity_streams_drawer.isDrawerOpen(GravityCompat.START))
            activity_streams_drawer.closeDrawer(GravityCompat.START)
        return true
    }

    // when reload, update the recyclerview with cursor=null, i.e. start againg with the 20 first
    // reload is set to true in order not to show the complete list but strats a new one

    fun initSwipeRefreshListener () {
        swipeRefreshLayout.setOnRefreshListener {
            //TODO("Implement action")
            // refresh your list contents somehow
            Log.d("cfauli", TAG + " reload true")

            cursor = null
            updateStreams(true)
            // reset the SwipeRefreshLayout (stop the loading spinner)
            swipeRefreshLayout.isRefreshing = false
        }
    }

}







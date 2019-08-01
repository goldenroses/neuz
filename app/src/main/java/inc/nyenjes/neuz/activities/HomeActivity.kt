package inc.nyenjes.neuz.activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import inc.nyenjes.neuz.R
import inc.nyenjes.neuz.activities.registration.LoginActivity
import inc.nyenjes.neuz.adapters.CardAdapter
import inc.nyenjes.neuz.managers.NeusDataManager
import inc.nyenjes.neuz.managers.NeuzDataProvider
import inc.nyenjes.neuz.models.NewsArticle
import inc.nyenjes.neuz.repositories.FavoritesRepository
import inc.nyenjes.neuz.repositories.HistoryRepository
import inc.nyenjes.neuz.repositories.NewsDbOpenHelper
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.nav_header_home.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var adapter: CardAdapter? = CardAdapter()
    private var TAG: String = "HomeActivity"
    private var mAuthListener: FirebaseAuth.AuthStateListener? = null

    var dbHelper: NewsDbOpenHelper? = null
    var favoritesRepository: FavoritesRepository? = null
    var historyRepository: HistoryRepository? = null
    var neuzDataProvider: NeuzDataProvider? = null
    var neuzDataManager: NeusDataManager? = null
        private set

    private var neuzLayoutManager = LinearLayoutManager(this)
    private var neuzLayoutFavoritesManager = GridLayoutManager(this, 3)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        setupFirebaseAuth()

        instantiateDataManager()
        getRandomArticles()

        displayNeuz()

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)
    }

    private fun displayNeuz() {
        recycler.layoutManager = neuzLayoutManager
        recycler.adapter = adapter
    }

    private fun displayFavorites() {
        getFavorites()
        recycler.layoutManager = neuzLayoutFavoritesManager
        recycler.adapter = adapter
    }

    private fun instantiateDataManager() {
        dbHelper = NewsDbOpenHelper(applicationContext)
        favoritesRepository = FavoritesRepository(dbHelper!!)
        historyRepository = HistoryRepository(dbHelper!!)
        neuzDataProvider = NeuzDataProvider()
        neuzDataManager = NeusDataManager(neuzDataProvider!!, favoritesRepository!!, historyRepository!!)

    }

    private fun getRandomArticles() {
        try {

            neuzDataManager?.getTopArticlesOfTheDay { result ->
                adapter!!.currentResults.clear()
                adapter!!.currentResults.addAll(result.articles)
                this.runOnUiThread {
                    adapter!!.notifyDataSetChanged()

                }
            }
        } catch (ex: Exception) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(ex.message).setTitle("oops!")

            var dialog = builder.create()
            dialog.show()
        }
    }

    private fun getFavorites() {
        try {
            val favorites = neuzDataManager?.getFavorites()
            adapter!!.currentResults.clear()
            adapter!!.currentResults.addAll(favorites as ArrayList<NewsArticle>)
            this.runOnUiThread { adapter!!.notifyDataSetChanged() }

        } catch (ex: Exception) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(ex.message).setTitle("oops!")

            var dialog = builder.create()
            dialog.show()
        }
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                displayNeuz()
                nav_view.menu.findItem(R.id.nav_home).isChecked = true
            }
            R.id.nav_favorites -> {
                handleNavSelection("Favorites selected")
                displayFavorites()
                nav_view.menu.findItem(R.id.nav_favorites).isChecked = true

            }
            R.id.nav_history -> {
                handleNavSelection( "Hostory selected")
                displayFavorites()
                nav_view.menu.findItem(R.id.nav_history).isChecked = true

            }
            R.id.nav_account -> {
                handleNavSelection( "Account selected")

            }
            R.id.nav_test -> {
                val intent = Intent(this, NoInternetActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_settings -> {
                handleNavSelection( "What settings?")
                val intent = Intent(this@HomeActivity, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                handleNavSelection( "Kicking you out..")
                FirebaseAuth.getInstance().signOut()

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun handleNavSelection(message: String) {

        Snackbar.make(recycler, message, Snackbar.LENGTH_SHORT).show()

    }

    public override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener!!)
    }

    public override fun onStop() {
        super.onStop()
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener!!)
        }
    }

    override fun onResume() {
        super.onResume()
        checkAuthenticationState()
    }

    //Authentication----

    private fun setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: started.")

        mAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            if (user != null) {
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.uid)
                textEmailAddress.text = FirebaseAuth.getInstance().currentUser!!.email

            } else {
                Log.d(TAG, "onAuthStateChanged:signed_out")
                val intent = Intent(this@HomeActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            // ...
        }
    }

    private fun checkAuthenticationState() {
        Log.d(TAG, "checkAuthenticationState: checking authentication state.")

        val user = FirebaseAuth.getInstance().currentUser

        if (user == null) {
            Log.d(TAG, "checkAuthenticationState: user is null, navigating back to login screen.")

            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        } else {
            Log.d(TAG, "checkAuthenticationState: user is authenticated.")
        }
    }

    /**
     * Sign out the current user
     */
    private fun signOut() {
        Log.d(TAG, "signOut: signing out")
        FirebaseAuth.getInstance().signOut()
    }
}

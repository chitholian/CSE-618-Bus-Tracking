package friendroid.bustracking.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.drawerlayout.widget.DrawerLayout
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.*
import friendroid.bustracking.*
import friendroid.bustracking.R
import friendroid.bustracking.adapters.OnlineBusAdapter
import friendroid.bustracking.fragments.OnlineBusesFragment
import friendroid.bustracking.models.User

abstract class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var onlineBusesFragment: OnlineBusesFragment
    lateinit var onlineBusAdapter: OnlineBusAdapter
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var query: Query

    protected lateinit var observer: RecyclerView.AdapterDataObserver

    // listen to user approval or deletion
    private lateinit var snapshotListener: EventListener<DocumentSnapshot>
    private lateinit var reference: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawer_layout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val nav_view = findViewById<NavigationView>(R.id.nav_view)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        sharedPreferences = getSharedPreferences("msg_time", Context.MODE_PRIVATE)
        if (this is TeacherActivity) {
            query = FirebaseFirestore.getInstance().also { it.firestoreSettings = fireSettings }.collection("users")
                    .whereEqualTo("approved", true).whereEqualTo("role", "driver")
                    .whereEqualTo("online", true).whereArrayContains("subscribers", mUser.uid)
        } else if (this is TransportControllerActivity) {
            query = FirebaseFirestore.getInstance().also { it.firestoreSettings = fireSettings }.collection("users")
                    .whereEqualTo("role", "driver").whereEqualTo("approved", true)
                    .whereEqualTo("online", true)
        }
        // create fragment
        onlineBusesFragment = OnlineBusesFragment()
        // create adapter
        val options = FirestoreRecyclerOptions.Builder<Any>().setQuery(query, Any::class.java)
//                .setLifecycleOwner(this)
                .build()
        onlineBusAdapter = OnlineBusAdapter(this, options) {
            // Start map activity
            startActivity(Intent(this, MapsActivity::class.java).also { intent ->
                intent.putExtra(EXTRA_BUS_ID, it["uid"]?.toString())
            })
        }
        // set adapter
        onlineBusesFragment.setAdapter(onlineBusAdapter)
        observer = object : RecyclerView.AdapterDataObserver() {

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
                setMenuBadges()
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                setMenuBadges()
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                setMenuBadges()
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                setMenuBadges()
            }

            override fun onChanged() {
                super.onChanged()
                setMenuBadges()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                super.onItemRangeChanged(positionStart, itemCount, payload)
                setMenuBadges()
            }
        }

        reference = FirebaseFirestore.getInstance().document("users/${mUser.uid}")

        // create snapshot lister
        snapshotListener = EventListener<DocumentSnapshot> { snapshot, err ->
            if (err != null) {
                err.printStackTrace()
            } else if (snapshot != null && snapshot.exists()) {
                // Check if user approved or not approved
                val user = snapshot.toObject(User::class.java)
                // if approved simply hide the waiting message and show the components
                if (user?.approved == true) {
                    hideWaiting()
                } else if (user?.role != "admin") {
                    displayWaiting()
                }
                val nav_view = findViewById<NavigationView>(R.id.nav_view)
                nav_view.getHeaderView(0).findViewById<AppCompatTextView>(R.id.profileName)?.text = user?.name
                nav_view.getHeaderView(0).findViewById<AppCompatTextView>(R.id.identityText)?.text = user?.identity
            } else {
                // User is deleted or something like that, so logout.
                AuthUI.getInstance().signOut(this)
                toast(R.string.user_deleted)
                mUser = User()
                finish()
            }
        }
    }

    override fun onBackPressed() {
        val drawer_layout = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            confirmBeforeExit()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> logout()
            R.id.menu_about -> showAbout()
            R.id.menu_online_buses -> {
                showOnlineBuses()
            }
        }

        val drawer_layout = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    protected fun hideWaiting() {
        findViewById<View>(R.id.waiting).visibility = View.GONE
        findViewById<View>(R.id.fragment_container).visibility = View.VISIBLE
    }

    protected fun displayWaiting() {
        findViewById<View>(R.id.waiting).visibility = View.VISIBLE
        findViewById<View>(R.id.fragment_container).visibility = View.GONE
    }

    open fun showOnlineBuses() {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, onlineBusesFragment).commit()
    }

    open fun setMenuBadges() {
        val count = onlineBusAdapter.itemCount
        val nav_view = findViewById<NavigationView>(R.id.nav_view)
        (nav_view.menu.findItem(R.id.menu_online_buses).actionView as TextView).text = if (count == 0) "" else count.toString()
    }

    override fun onResume() {
        super.onResume()
        // set observer
        onlineBusAdapter.startListening()
        onlineBusAdapter.registerAdapterDataObserver(observer)
        setMenuBadges()
    }

    override fun onPause() {
        super.onPause()
        // remove observer
        onlineBusAdapter.unregisterAdapterDataObserver(observer)
    }

    override fun onStart() {
        super.onStart()
        reference.addSnapshotListener(this, snapshotListener)
    }

    override fun onRestart() {
        super.onRestart()
        reference.addSnapshotListener(this, snapshotListener)
    }
}

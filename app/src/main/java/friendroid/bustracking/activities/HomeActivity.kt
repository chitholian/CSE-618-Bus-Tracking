package friendroid.bustracking.activities

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.MenuItem
import android.view.View
import friendroid.bustracking.R
import friendroid.bustracking.fragments.OnlineBusesFragment
import kotlinx.android.synthetic.main.activity_home.*

open class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
    }

    override fun onBackPressed() {
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
            R.id.menu_online_buses -> showOnlineBuses()
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    protected fun hideWaiting() {
        waiting.visibility = View.GONE
    }

    fun showOnlineBuses() {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, OnlineBusesFragment()).commit()
    }
}

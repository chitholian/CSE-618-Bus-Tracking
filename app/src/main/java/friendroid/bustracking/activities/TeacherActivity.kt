package friendroid.bustracking.activities

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.Gravity
import android.view.MenuItem
import android.widget.TextView
import friendroid.bustracking.R
import friendroid.bustracking.fragments.SelectBusesFragment
import friendroid.bustracking.mUser
import kotlinx.android.synthetic.main.activity_home.*

class TeacherActivity : HomeActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var user = mUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nav_view.inflateMenu(R.menu.menu_teacher)
        nav_view.menu.findItem(R.id.menu_online_buses).actionView = TextView(this).also {
            it.gravity = Gravity.CENTER
        }

        if (savedInstanceState == null && mUser.approved) {
            showOnlineBuses()
            nav_view.menu.findItem(R.id.menu_online_buses).isChecked = true
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_update_subscription -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SelectBusesFragment()).commit()
                hideWaiting()
            }
            else -> if (!user.approved) {
                displayWaiting()
            }
        }

        return super.onNavigationItemSelected(item)
    }

    override fun showOnlineBuses() {
        if (!user.approved) displayWaiting()
        super.showOnlineBuses()
    }
}

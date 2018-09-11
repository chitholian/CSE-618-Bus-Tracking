package friendroid.bustracking.activities

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.Gravity
import android.view.MenuItem
import android.widget.TextView
import friendroid.bustracking.R
import friendroid.bustracking.fragments.AllBusesFragment
import friendroid.bustracking.fragments.ChangeRegKeyFragment
import friendroid.bustracking.fragments.PendingRequestsFragment
import friendroid.bustracking.fragments.TeacherListFragment
import kotlinx.android.synthetic.main.activity_home.*

class TransportControllerActivity : HomeActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideWaiting()
        nav_view.inflateMenu(R.menu.menu_transport_controller)
        nav_view.menu.findItem(R.id.menu_pending_requests).actionView = TextView(this).also {
            it.gravity = Gravity.CENTER
            it.text = "38"
        }
        nav_view.menu.findItem(R.id.menu_online_buses).actionView = TextView(this).also {
            it.gravity = Gravity.CENTER
            it.text = "15"
        }
        if (savedInstanceState == null) {
            showOnlineBuses()
            nav_view.menu.findItem(R.id.menu_online_buses).isChecked = true
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_change_reg_key -> {
                ChangeRegKeyFragment().also {
                    it.isCancelable = false
                }.show(supportFragmentManager, "ShowInputDialog")
            }
            R.id.menu_all_teachers -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, TeacherListFragment()).commit()
            R.id.menu_all_buses -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AllBusesFragment()).commit()
            R.id.menu_pending_requests -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PendingRequestsFragment()).commit()
        }
        return super.onNavigationItemSelected(item)
    }
}

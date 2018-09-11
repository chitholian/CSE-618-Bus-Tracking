package friendroid.bustracking.activities

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.Gravity
import android.view.MenuItem
import android.widget.TextView
import friendroid.bustracking.R
import friendroid.bustracking.fragments.SelectBusesFragment
import kotlinx.android.synthetic.main.activity_home.*

class TeacherActivity : HomeActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nav_view.inflateMenu(R.menu.menu_teacher)
        nav_view.menu.findItem(R.id.menu_online_buses).actionView = TextView(this).also {
            it.gravity = Gravity.CENTER
            it.text = "15"
        }
        if (savedInstanceState != null) hideWaiting()
        waiting.setOnClickListener {
            hideWaiting()
            showOnlineBuses()
            nav_view.menu.findItem(R.id.menu_online_buses).isChecked = true
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        hideWaiting()
        when (item.itemId) {
            R.id.menu_update_subscription -> {
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SelectBusesFragment()).commit()
            }
        }

        return super.onNavigationItemSelected(item)
    }
}

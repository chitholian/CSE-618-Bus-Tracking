package friendroid.bustracking.activities

import android.os.Bundle
import android.support.design.widget.NavigationView
import android.view.MenuItem
import friendroid.bustracking.R
import friendroid.bustracking.fragments.SelectBusesFragment
import kotlinx.android.synthetic.main.activity_home.*

class TeacherActivity : HomeActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nav_view.inflateMenu(R.menu.menu_teacher)

        if (savedInstanceState != null) hideWaiting()
        waiting.setOnClickListener {
            hideWaiting()
            showOnlineBuses()
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

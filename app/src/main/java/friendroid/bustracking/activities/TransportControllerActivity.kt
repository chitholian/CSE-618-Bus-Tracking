package friendroid.bustracking.activities

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v7.app.AlertDialog
import android.view.MenuItem
import friendroid.bustracking.R
import friendroid.bustracking.fragments.AllBusesFragment
import friendroid.bustracking.fragments.TeacherListFragment
import kotlinx.android.synthetic.main.activity_home.*

class TransportControllerActivity : HomeActivity(), NavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        hideWaiting()
        nav_view.inflateMenu(R.menu.menu_transport_controller)
        if (savedInstanceState == null) showOnlineBuses()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_change_reg_key -> {
                AlertDialog.Builder(this).setView(R.layout.dialog_input_reg).setPositiveButton(R.string.ok) { _, _ ->
                    delayed {
                        toast(R.string.key_changed)
                    }
                }.setNeutralButton(R.string.cancel) { _, _ ->

                }.setTitle(R.string.new_key).setCancelable(false).create().show()
            }
            R.id.menu_all_teachers -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, TeacherListFragment()).commit()
            R.id.menu_all_buses -> supportFragmentManager.beginTransaction().replace(R.id.fragment_container, AllBusesFragment()).commit()
            R.id.menu_pending_requests -> startActivity(Intent(this, PendingRequestActivity::class.java))
        }
        return super.onNavigationItemSelected(item)
    }
}

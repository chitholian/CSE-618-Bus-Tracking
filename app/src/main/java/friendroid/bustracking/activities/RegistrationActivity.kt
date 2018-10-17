package friendroid.bustracking.activities

import android.os.Bundle
import friendroid.bustracking.R
import friendroid.bustracking.fragments.CreateProfileFragment
import friendroid.bustracking.fragments.SelectBusesFragment

class RegistrationActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, CreateProfileFragment()).commit()
    }

    fun showBusSelection() =
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SelectBusesFragment()).addToBackStack("ShowBusSelection").commit()

}

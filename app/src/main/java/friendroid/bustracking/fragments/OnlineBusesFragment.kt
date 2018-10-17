package friendroid.bustracking.fragments

import android.os.Bundle
import friendroid.bustracking.R

open class OnlineBusesFragment : ListFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.online_buses)
    }
}
package friendroid.bustracking.fragments

import android.os.Bundle
import android.view.View
import friendroid.bustracking.R
import friendroid.bustracking.activities.BaseActivity
import friendroid.bustracking.adapters.SimpleBusAdapter
import friendroid.bustracking.entities.Bus
import friendroid.bustracking.utils.confirm
import kotlinx.android.synthetic.main.fragment_list_holder.*


open class AllBusesFragment : ListFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.all_buses)

        val buses = ArrayList<Bus>()
        for (i in 1..10) {
            val bus = Bus()
            bus.uid = "uid_$i"
            bus.name = "Bus Name $i"
            bus.identity = "email_$i@gmail.com"
            buses.add(bus)
        }
        mAdapter = SimpleBusAdapter(buses) { bus ->
            confirm(activity, R.string.confirm_delete) {
                progressBar.visibility = View.VISIBLE
                (activity as BaseActivity).apply {
                    delayed {
                        buses.remove(bus)
                        mAdapter?.notifyDataSetChanged()
                        progressBar.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }
}
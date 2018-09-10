package friendroid.bustracking.fragments

import android.os.Bundle
import android.view.View
import friendroid.bustracking.R
import friendroid.bustracking.activities.BaseActivity
import friendroid.bustracking.adapters.SimpleBusAdapter
import friendroid.bustracking.confirm
import friendroid.bustracking.entities.Bus
import kotlinx.android.synthetic.main.fragment_list_holder.*


open class AllBusesFragment : ListFragment() {
    private lateinit var buses: ArrayList<Bus>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.all_buses)

        buses = ArrayList()
        for (i in 1..10) {
            val bus = Bus()
            bus.uid = "uid_$i"
            bus.name = "Bus Name $i"
            bus.identity = "email_$i@gmail.com"
            buses.add(bus)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar?.visibility = View.VISIBLE
        (activity as BaseActivity).delayed {
            mAdapter = SimpleBusAdapter(buses) { bus ->
                confirm(activity, R.string.confirm_delete) {
                    progressBar.visibility = View.VISIBLE
                    (activity as BaseActivity).apply {
                        delayed {
                            buses.remove(bus)
                            mAdapter?.notifyDataSetChanged()
                            progressBar?.visibility = View.INVISIBLE
                        }
                    }
                }
            }
            setAdapter() // We have to call because it is changed delayed.
            progressBar.visibility = View.INVISIBLE
        }
    }
}
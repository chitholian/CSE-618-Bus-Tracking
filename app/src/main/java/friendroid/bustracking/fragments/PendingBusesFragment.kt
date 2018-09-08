package friendroid.bustracking.fragments

import android.os.Bundle
import friendroid.bustracking.R
import friendroid.bustracking.activities.BaseActivity
import friendroid.bustracking.adapters.PendingBusAdapter
import friendroid.bustracking.entities.Bus
import friendroid.bustracking.utils.confirm


open class PendingBusesFragment : ListFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.pending_buses)

        val buses = ArrayList<Bus>()
        for (i in 1..5)
            Bus().also { b ->
                b.uid = "uid_$i"
                b.name = "Bus Name $i"
                b.identity = "email_$i@gmail.com"
                buses.add(b)
            }
        mAdapter = PendingBusAdapter(buses, { bus ->
            confirm(activity, R.string.confirm_delete) {
                (activity as BaseActivity).delayed {
                    buses.remove(bus)
                    mAdapter?.notifyDataSetChanged()
                }
            }
        }, { bus ->
            (activity as BaseActivity).delayed {
                buses.remove(bus)
                mAdapter?.notifyDataSetChanged()
            }
        })
    }
}
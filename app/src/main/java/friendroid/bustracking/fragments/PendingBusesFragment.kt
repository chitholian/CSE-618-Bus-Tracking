package friendroid.bustracking.fragments

import android.os.Bundle
import android.view.View
import friendroid.bustracking.R
import friendroid.bustracking.activities.BaseActivity
import friendroid.bustracking.adapters.PendingBusAdapter
import friendroid.bustracking.entities.Bus
import friendroid.bustracking.utils.confirm
import kotlinx.android.synthetic.main.fragment_list_holder.*


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
                progressBar.visibility = View.VISIBLE
                (activity as BaseActivity).delayed {
                    buses.remove(bus)
                    mAdapter?.notifyDataSetChanged()
                    progressBar.visibility = View.INVISIBLE
                }
            }
        }, { bus ->
            progressBar.visibility = View.VISIBLE
            (activity as BaseActivity).delayed {
                buses.remove(bus)
                mAdapter?.notifyDataSetChanged()
                progressBar.visibility = View.INVISIBLE
            }
        })
    }
}
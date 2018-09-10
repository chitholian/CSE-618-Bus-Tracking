package friendroid.bustracking.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import friendroid.bustracking.R
import friendroid.bustracking.activities.BaseActivity
import friendroid.bustracking.activities.MapsActivity
import friendroid.bustracking.adapters.OnlineBusAdapter
import friendroid.bustracking.entities.Location
import friendroid.bustracking.entities.OnlineBus
import kotlinx.android.synthetic.main.fragment_list_holder.*


open class OnlineBusesFragment : ListFragment() {
    private lateinit var buses: ArrayList<OnlineBus>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.online_buses)

        buses = ArrayList()
        for (i in 1..8)
            OnlineBus().also { b ->
                b.uid = "uid_$i"
                b.name = "Bus Name $i"
                b.identity = "email_$i@gmail.com"
                b.location = Location().also { l ->
                    l.place = "Bus location $i"
                }
                buses.add(b)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar.visibility = View.VISIBLE
        (activity as BaseActivity).delayed {
            mAdapter = OnlineBusAdapter(buses) {
                val intent = Intent(activity, MapsActivity::class.java)
                intent.putExtra("name", (it as OnlineBus).name)
                intent.putExtra("place", it.location.place)
//            intent.putExtra("lat", it.lat)
//            intent.putExtra("lon", it.lon)
                startActivity(intent)
            }
            setAdapter() // We have to call because it is changed delayed.
            progressBar?.visibility = View.INVISIBLE
        }
    }
}
package friendroid.bustracking.fragments

import android.content.Intent
import android.os.Bundle
import friendroid.bustracking.R
import friendroid.bustracking.activities.MapsActivity
import friendroid.bustracking.adapters.OnlineBusAdapter
import friendroid.bustracking.entities.Location
import friendroid.bustracking.entities.OnlineBus


open class OnlineBusesFragment : ListFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = getString(R.string.online_buses)

        val buses = ArrayList<OnlineBus>()
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
        mAdapter = OnlineBusAdapter(buses) {
            val intent = Intent(activity, MapsActivity::class.java)
            intent.putExtra("name", (it as OnlineBus).name)
            intent.putExtra("place", it.location.place)
//            intent.putExtra("lat", it.lat)
//            intent.putExtra("lon", it.lon)
            startActivity(intent)
        }
    }
}
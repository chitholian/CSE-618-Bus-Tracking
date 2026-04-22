package friendroid.bustracking.fragments

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import friendroid.bustracking.R
import friendroid.bustracking.activities.TransportControllerActivity
import friendroid.bustracking.adapters.TabViewPagerAdapter

class PendingRequestsFragment : Fragment() {
    private lateinit var pbf: PendingUsersFragment
    private lateinit var ptf: PendingUsersFragment
    private var integer: Int = 0

    private var tabs: TabLayout? = null
    private var fragment_container: ViewPager? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e("TAGGGG", "onCreateView")
        return inflater.inflate(R.layout.fragment_pending_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("TAGGGG", "onViewCreated")
        tabs = view.findViewById(R.id.tabs)
        fragment_container = view.findViewById(R.id.fragment_container)

        fragment_container?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs?.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(fragment_container))
    }

    override fun onResume() {
        super.onResume()
        Log.e("TAGGGG", "onResume")

        activity?.title = getString(R.string.pending_requests)

        if (integer == 0) {
            pbf = (activity as TransportControllerActivity).pendingBusesFragment
            ptf = (activity as TransportControllerActivity).pendingTeachersFragment
            setFragments(pbf, ptf)
            integer = 1
        } else {
            pbf.checkItemCount()
            ptf.checkItemCount()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        integer = 0
    }

    fun setFragments(pbf: PendingUsersFragment, ptf: PendingUsersFragment) {
        fragment_container?.adapter = TabViewPagerAdapter(childFragmentManager, pbf, ptf)
        this.ptf = ptf
        this.pbf = pbf
    }
}

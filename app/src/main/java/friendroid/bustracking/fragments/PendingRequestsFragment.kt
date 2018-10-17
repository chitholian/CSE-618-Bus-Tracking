package friendroid.bustracking.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import friendroid.bustracking.R
import friendroid.bustracking.activities.TransportControllerActivity
import friendroid.bustracking.adapters.TabViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_pending_request.*

class PendingRequestsFragment : Fragment() {
    private lateinit var pbf: PendingUsersFragment
    private lateinit var ptf: PendingUsersFragment
    private var integer: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e("TAGGGG", "onCreateView")
        return inflater.inflate(R.layout.fragment_pending_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("TAGGGG", "onViewCreated")
        fragment_container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(fragment_container))
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

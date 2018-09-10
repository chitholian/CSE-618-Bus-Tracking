package friendroid.bustracking.fragments

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import friendroid.bustracking.R
import friendroid.bustracking.activities.BaseActivity
import friendroid.bustracking.adapters.TabViewPagerAdapter
import kotlinx.android.synthetic.main.fragment_pending_request.*

class PendingRequestsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pending_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragment_container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(fragment_container))
        progressBar?.visibility = View.VISIBLE
        (activity as BaseActivity).delayed {
            fragment_container?.adapter = TabViewPagerAdapter(childFragmentManager)
            progressBar?.visibility = View.INVISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        activity?.title = getString(R.string.pending_requests)
    }
}

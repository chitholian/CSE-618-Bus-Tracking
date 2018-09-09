package friendroid.bustracking.activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.view.View
import friendroid.bustracking.R
import friendroid.bustracking.adapters.TabViewPagerAdapter
import kotlinx.android.synthetic.main.activity_pending_request.*

class PendingRequestActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_request)
        fragment_container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(fragment_container))
        progressBar?.visibility = View.VISIBLE
        delayed {
            fragment_container.adapter = TabViewPagerAdapter(supportFragmentManager)
            progressBar?.visibility = View.INVISIBLE
        }
    }
}

package friendroid.bustracking.activities

import android.os.Bundle
import android.support.design.widget.TabLayout
import friendroid.bustracking.R
import friendroid.bustracking.adapters.TabViewPagerAdapter
import kotlinx.android.synthetic.main.activity_pending_request.*

class PendingRequestActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pending_request)
        fragment_container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(fragment_container))
        delayed {
            fragment_container.adapter = TabViewPagerAdapter(supportFragmentManager)
        }
    }
}

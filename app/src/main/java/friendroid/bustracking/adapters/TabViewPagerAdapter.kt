package friendroid.bustracking.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import friendroid.bustracking.fragments.PendingUsersFragment

class TabViewPagerAdapter(fm: FragmentManager, val pbf: PendingUsersFragment, val ptf: PendingUsersFragment) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return if (position == 0) pbf
        else ptf
    }

    override fun getCount(): Int = 2
}

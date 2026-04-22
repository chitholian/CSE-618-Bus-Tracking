package friendroid.bustracking.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import friendroid.bustracking.fragments.PendingUsersFragment

class TabViewPagerAdapter(fm: FragmentManager, val pbf: PendingUsersFragment, val ptf: PendingUsersFragment) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return if (position == 0) pbf
        else ptf
    }

    override fun getCount(): Int = 2
}

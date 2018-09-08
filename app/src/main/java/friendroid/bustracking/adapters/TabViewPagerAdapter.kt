package friendroid.bustracking.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import friendroid.bustracking.fragments.PendingBusesFragment
import friendroid.bustracking.fragments.PendingTeacherFragment

class TabViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return if (position == 0) PendingBusesFragment()
        else PendingTeacherFragment()
    }

    override fun getCount(): Int = 2
}

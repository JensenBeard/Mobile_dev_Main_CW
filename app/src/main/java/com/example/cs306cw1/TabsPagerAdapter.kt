package com.example.cs306cw1

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class TabsPagerAdapter (fm: FragmentManager, private val tabTitles: Array<String>, location: String): FragmentStatePagerAdapter(fm){
    private val countryCode = location

    /**
     * Gets the selected fragment in the tab layout
     * @param index fragment index
     */
    override fun getItem(index: Int): Fragment {
        when (index) {
            0 -> return FragmentOne()
            1 -> return FragmentTwo()
            2 -> return FragmentThree(countryCode)
        }
        return FragmentOne()
    }

    /**
     * Get count of the list
     * @return list size
     */
    override fun getCount(): Int {
        return tabTitles.size
    }

    /**
     * Gets the fragment title
     * @param position fragment index
     * @return title of the fragment
     */
    override fun getPageTitle(position: Int): CharSequence? {
        return tabTitles[position]
    }

}
package org.fablabsantiago.smartcities.app.appmobile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MisAlertasPagerAdapter extends FragmentPagerAdapter
{
//  FragmentPagerAdapter
//    This is best when navigating between sibling screens representing a fixed, small number of pages.
//  FragmentStatePagerAdapter
//    This is best for paging across a collection of objects for which the number of pages is undetermined.
//    It destroys fragments as the user navigates to other pages, minimizing memory usage.

    private static int NUM_ITEMS = 3;

    public MisAlertasPagerAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        switch (position)
        {
            case 0:
                //return MisAlertasCompletasTab2.newInstance(0, "COmpletas");
                return new MisAlertasCompletasTab();
            case 1:
                //return MisAlertasPendientesTab2.newInstance(1, "PEndientes");
                return new MisAlertasPendientesTab();
            case 2:
                //return MisAlertasTodasTab2.newInstance(2,"TOdas");
                return new MisAlertasTodasTab();
            default:
                return null;
        }
    }

    //@Override
    //public CharSequence getPageTitle(int position)
    //{
    //    return "Page" + position;
    //}

    @Override
    public int getCount()
    {
        return NUM_ITEMS;
    }
}

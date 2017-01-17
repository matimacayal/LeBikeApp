package org.fablabsantiago.smartcities.app.appmobile.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.fablabsantiago.smartcities.app.appmobile.UI.Fragments.MisAlertasCompletasTab;
import org.fablabsantiago.smartcities.app.appmobile.UI.Fragments.MisAlertasPendientesTab;
import org.fablabsantiago.smartcities.app.appmobile.UI.Fragments.MisAlertasTodasTab;
import org.fablabsantiago.smartcities.app.appmobile.Interfaces.MisAlertasInterfaces;

public class MisAlertasPagerAdapter extends FragmentPagerAdapter
{
//  FragmentPagerAdapter
//    This is best when navigating between sibling screens representing a fixed, small number of pages.
//  FragmentStatePagerAdapter
//    This is best for paging across a collection of objects for which the number of pages is undetermined.
//    It destroys fragments as the user navigates to other pages, minimizing memory usage.

    private static int NUM_ITEMS = 3;
    private MisAlertasInterfaces.MisAlertasTabListener misAlertasTabListener;

    public MisAlertasPagerAdapter(MisAlertasInterfaces.MisAlertasTabListener alertasTabListener, FragmentManager fm) {
        super(fm);
        misAlertasTabListener = alertasTabListener;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MisAlertasCompletasTab misAlertasCompletasTab = new MisAlertasCompletasTab();
                misAlertasCompletasTab.setAlertasCompletasTabListener(misAlertasTabListener);
                return misAlertasCompletasTab;
            case 1:
                MisAlertasPendientesTab misAlertasPendientesTab = new MisAlertasPendientesTab();
                misAlertasPendientesTab.setAlertasPendientesTabListener(misAlertasTabListener);
                return misAlertasPendientesTab;
            case 2:
                MisAlertasTodasTab misAlertasTodasTab = new MisAlertasTodasTab();
                misAlertasTodasTab.setAlertasTodasTabListener(misAlertasTabListener);
                return misAlertasTodasTab;
            default:
                return null;
        }
    }

    //@Overridee
    //public CharSequence getPageTitle(int position)
    //{
    //    return "Page" + position;
    //}

    @Override
    public int getCount() {
        return NUM_ITEMS;
    }
}

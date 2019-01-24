package com.fervenzagames.apparbitraje.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.fervenzagames.apparbitraje.PhoneFragments.PhoneNextCupsFragment;
import com.fervenzagames.apparbitraje.PhoneFragments.PhoneStatsFragment;

public class SectionsPagerAdapterMobile extends FragmentPagerAdapter {

    public SectionsPagerAdapterMobile(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0: {
                /*SandaFragment sdFragment = new SandaFragment();
                return sdFragment;*/
                PhoneStatsFragment statsFragment = new PhoneStatsFragment();
                return statsFragment;
            }
            case 1: {
                PhoneNextCupsFragment nextCupsFragment = new PhoneNextCupsFragment();
                return nextCupsFragment;
                /*QingdaFragment qdFragment = new QingdaFragment();
                return qdFragment;*/
            }
            /*case 2: {
                KFCFragment kfcFragment = new KFCFragment();
                return kfcFragment;
            }
            case 3: {
                ShuaijiaoFragment sjFragment = new ShuaijiaoFragment();
                return sjFragment;
            }*/
            default:
            {
                return null;
            }
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position)
    {
        switch(position)
        {
            case 0: {
                return "ESTADÍSTICAS";
            }
            case 1: {
                return "PRÓXIMOS EVENTOS";
            }
            /*case 2: {
                return "KUNGFU COMBAT";
            }
            case 3: {
                return "SHUAI JIAO";
            }*/
            default:
                return null;
        }
    }
}

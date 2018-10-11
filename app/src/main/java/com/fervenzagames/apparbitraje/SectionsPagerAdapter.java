package com.fervenzagames.apparbitraje;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0: {
                SandaFragment sdFragment = new SandaFragment();
                return sdFragment;
            }
            case 1: {
                QingdaFragment qdFragment = new QingdaFragment();
                return qdFragment;
            }
            case 2: {
                KFCFragment kfcFragment = new KFCFragment();
                return kfcFragment;
            }
            case 3: {
                ShuaijiaoFragment sjFragment = new ShuaijiaoFragment();
                return sjFragment;
            }
            default:
            {
                return null;
            }
        }
    }

    @Override
    public int getCount() {
        return 4; /* SD, QD, KFC, SJ*/
    }

    public CharSequence getPageTitle(int position)
    {
        switch(position)
        {
            case 0: {
                return "SANDA";
            }
            case 1: {
                return "QINGDA";
            }
            case 2: {
                return "KUNGFU COMBAT";
            }
            case 3: {
                return "SHUAI JIAO";
            }
            default:
                return null;
        }
    }
}

package com.fervenzagames.apparbitraje.Utils;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.fervenzagames.apparbitraje.TabletFragments.TabletCampsListFragment;
import com.fervenzagames.apparbitraje.TabletFragments.TabletNextCupsFragment;
import com.fervenzagames.apparbitraje.TabletFragments.TabletStatsFragment;

public class SectionsPagerAdapterTablet extends FragmentPagerAdapter {

    public SectionsPagerAdapterTablet(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0: { // STATS
                TabletStatsFragment statsFragment = new TabletStatsFragment();
                return statsFragment;
            }
            case 1: { // Listado Campeonatos
                TabletCampsListFragment campsListFragment = new TabletCampsListFragment();
                return campsListFragment;
            }
            /*case 2:{ // Listado Árbitros
                return null;
            }
            case 3:{ // Listado Competidores
                return null;
            }
            case 4:{ // Listado Combates
                return null;
            }
            case 5:{ // Próximos Eventos
                TabletNextCupsFragment nextCupsFragment = new TabletNextCupsFragment();
                return nextCupsFragment;
            }*/

            default:{
                return null;
            }
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch(position){
            case 0:{ // STATS
                return "ESTADÍSTICAS";
            }
            case 1:{
                 return "CAMPEONATOS";
            }/*
            case 2:{ // Listado Árbitros
                return "ÁRBITROS";
            }
            case 3:{ // Listado Competidores
                return "COMPETIDORES";
            }
            case 4:{ // Listado Combates
                return "COMBATES";
            }
            case 5:{ // Próximos Eventos
                return "PRÓXIMOS EVENTOS";
            }
            */
            default:{
                return null;
            }
        }
    }
}

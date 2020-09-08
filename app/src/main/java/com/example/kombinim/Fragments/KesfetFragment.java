package com.example.kombinim.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.kombinim.Fragments.KesfetFragments.KisilerFragment;
import com.example.kombinim.Fragments.KesfetFragments.PaylasimlarFragment;
import com.example.kombinim.R;

import java.util.Objects;

public class KesfetFragment extends Fragment {

    private View rootView;
    private Fragment kisiler, favoriler;

    private void OlusumTasarlayici(){
        kisiler=new KisilerFragment();
        favoriler=new PaylasimlarFragment();
    }

    private void init(){
        BottomNavigationView navigation = rootView.findViewById(R.id.kesfetnav);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        OlusumTasarlayici();

        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frame_kesfet,favoriler).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment=null;

            switch (item.getItemId()) {
                case R.id.navigation_paylasimlar:{
                    if(favoriler!=null){
                        selectedFragment=favoriler;
                    }else{
                        selectedFragment=new PaylasimlarFragment();
                    }
                    break;
                }
                case R.id.navigation_kisiler:{
                    if(kisiler!=null){
                        selectedFragment=kisiler;
                    }else{
                        selectedFragment=new KisilerFragment();
                    }
                    break;
                }
            }
            Objects.requireNonNull(getActivity())
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_kesfet, Objects.requireNonNull(selectedFragment)).commit();
            return true;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_kesfet,container,false);

        init();

        return rootView;
    }
}

package com.example.kombinim.Fragments.ProfilFragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kombinim.Adapters.KombinAdapter;
import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Kombin;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PodyumFragment extends Fragment{

    public static String profil_sahibi;
    private ProgressDialog progress;
    private View rootView;
    private RecyclerView podyum_recycview;
    private List<Kombin> kombinlerim=new ArrayList<>();
    private KombinAdapter adapter;
    private TextView henuzkombinyok;

    private void init(){
        profil_sahibi = Objects.requireNonNull(getArguments()).getString("user_id");

        podyum_recycview=rootView.findViewById(R.id.podyum_recycview);
        henuzkombinyok=rootView.findViewById(R.id.henuzkombinyok);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        podyum_recycview.setHasFixedSize(true);
        podyum_recycview.setLayoutManager(linearLayoutManager);

        adapter = new KombinAdapter(kombinlerim,getContext(),getActivity());
        podyum_recycview.setAdapter(adapter);
    }

    private void ProgressBaslat(){
        progress = new ProgressDialog(getActivity());
        progress.setTitle("Yorumlar Yükleniyor");
        progress.setMessage("Yorumlarınızın yüklenmesi zaman alabilir.");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void KombinleriCek(){
        BaslaActivity.vb.getDbref("Kombinler")
                .child(profil_sahibi)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        kombinlerim.clear();
                        if(dataSnapshot.exists()){
                            podyum_recycview.setVisibility(View.VISIBLE);
                            henuzkombinyok.setVisibility(View.GONE);
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                Kombin a= snapshot.getValue(Kombin.class);
                                kombinlerim.add(a);
                                adapter.notifyDataSetChanged();
                            }
                        }else{
                            podyum_recycview.setVisibility(View.GONE);
                            henuzkombinyok.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        if(progress!=null && progress.isShowing()){
            progress.dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        KombinleriCek();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_podyum,container,false);

        ProgressBaslat();
        init();

        return rootView;
    }
}

package com.example.kombinim.Fragments.KesfetFragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kombinim.Adapters.FavoriAdapter;
import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Kombin;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.skhugh.simplepulltorefresh.PullToRefreshLayout;
import com.skhugh.simplepulltorefresh.PullToRefreshListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PaylasimlarFragment extends Fragment {

    private View rootView;
    private TextView henuzfavoriyok;
    private RecyclerView favorilerrecycview;
    private List<Kombin> kombinler=new ArrayList<>();
    private FavoriAdapter favoriAdapter;
    private ProgressDialog progress;
    private PullToRefreshLayout favorilerrefresh;

    private void ProgressBaslat(){
        progress = new ProgressDialog(getActivity());
        progress.setTitle("Favoriler Yükleniyor");
        progress.setMessage("Favorileriniz yüklenmesi zaman alabilir.");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void init(){
        henuzfavoriyok=rootView.findViewById(R.id.henuzfavoriyok);
        favorilerrecycview=rootView.findViewById(R.id.favorilerrecycview);
        favorilerrefresh=rootView.findViewById(R.id.favorilerrefresh);

        favorilerrecycview.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
        favoriAdapter=new FavoriAdapter(kombinler,getActivity());
        if (favorilerrecycview != null) {
            favorilerrecycview.setAdapter(favoriAdapter);
            favorilerrecycview.setLayoutManager(gridLayoutManager);
        }
    }

    //sayfayı yenilemesini hatırlat
    private void FavorileriCek(){
        ProgressBaslat();
        BaslaActivity.vb.getDbref("FavoriListesi")
                .child(BaslaActivity.vb.getUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        kombinler.clear();
                        if(dataSnapshot.exists()){
                            henuzfavoriyok.setVisibility(View.GONE);
                            favorilerrecycview.setVisibility(View.VISIBLE);
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                String id= Objects.requireNonNull(snapshot.getKey());
                                String kimden= Objects.requireNonNull(snapshot.getValue()).toString();
                                BaslaActivity.vb.getDbref("Kombinler")
                                        .child(kimden)
                                        .orderByChild("id").equalTo(id)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshotz) {
                                                if(dataSnapshotz.exists()){
                                                    for(DataSnapshot snapshotz : dataSnapshotz.getChildren()){
                                                        Kombin favori= snapshotz.getValue(Kombin.class);
                                                        kombinler.add(favori);
                                                        favoriAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }

                        }else{
                            henuzfavoriyok.setVisibility(View.VISIBLE);
                            favorilerrecycview.setVisibility(View.GONE);
                            favoriAdapter.notifyDataSetChanged();
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
        FavorileriCek();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_paylasimlar,container,false);

        init();

        favorilerrefresh.setPullToRefreshListener(new PullToRefreshListener() {
            @Override
            public void onStartRefresh(@Nullable View view) {
                FavorileriCek();
                favorilerrefresh.refreshDone();
            }
        });

        return rootView;
    }
}

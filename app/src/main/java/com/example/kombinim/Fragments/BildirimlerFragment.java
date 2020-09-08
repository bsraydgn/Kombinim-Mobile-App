package com.example.kombinim.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kombinim.Adapters.BildirimAdapter;
import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Bildirim;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.skhugh.simplepulltorefresh.PullToRefreshLayout;
import com.skhugh.simplepulltorefresh.PullToRefreshListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BildirimlerFragment extends Fragment {

    private RecyclerView bildirimlerrecycview;
    private List<Bildirim> bildirimler = new ArrayList<>();
    private BildirimAdapter adapter;
    private TextView henuzbildirimyok;
    private View rootView;
    private ImageButton bildirimlerisil;
    private PullToRefreshLayout bildirimrefresh;

    private RelativeLayout bildirim_sonundaysa_goster;
    private AVLoadingIndicatorView bildirim_sonundaysa_progress;
    private int mevcutsayfa=1;
    private static final int CEKILECEK_DATA = 50;

    private boolean islastItemDisplaying(RecyclerView recyclerView){
        //check if the adapter item count is greater than 0
        if(Objects.requireNonNull(recyclerView.getAdapter()).getItemCount() != 0){
            //get the last visible item on screen using the layoutmanager
            int lastVisibleItemPosition = ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
            //apply some logic here.
            return lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1;
        }
        return  false;
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if(islastItemDisplaying(recyclerView)){
                //so i would call the get data method here
                // show loading progress
                bildirim_sonundaysa_goster.setVisibility(View.VISIBLE);
                bildirim_sonundaysa_progress.show();
                mevcutsayfa++;
                BildirimleriCek();
                bildirim_sonundaysa_progress.hide();
                bildirim_sonundaysa_goster.setVisibility(View.GONE);
            }
        }
    };

    private void init() {
        bildirimlerrecycview = rootView.findViewById(R.id.bildirimlerrecycview);
        henuzbildirimyok = rootView.findViewById(R.id.henuzbildirimyok);
        bildirimlerisil = rootView.findViewById(R.id.bildirimlerisil);
        bildirimrefresh = rootView.findViewById(R.id.bildirimrefresh);

        bildirim_sonundaysa_goster = rootView.findViewById(R.id.bildirim_sonundaysa_goster);
        bildirim_sonundaysa_progress = rootView.findViewById(R.id.bildirim_sonundaysa_progress);

        bildirimlerrecycview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        adapter = new BildirimAdapter(bildirimler, getActivity());
        if (bildirimlerrecycview != null) {
            bildirimlerrecycview.setAdapter(adapter);
            bildirimlerrecycview.setLayoutManager(linearLayoutManager);
        }
    }

    private void BildirimleriSil() {
        BaslaActivity.vb.getDbref("Bildirimler")
                .child(BaslaActivity.vb.getUserID())
                .removeValue().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Bildirimler silinemiyor. Tekrar dene.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void BildirimleriCek() {
        BaslaActivity.vb.getDbref("Bildirimler")
                .child(BaslaActivity.vb.getUserID())
                .limitToLast(mevcutsayfa * CEKILECEK_DATA)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        bildirimler.clear();
                        if (dataSnapshot.exists()) {
                            bildirimlerisil.setVisibility(View.VISIBLE);
                            henuzbildirimyok.setVisibility(View.GONE);
                            bildirimlerrecycview.setVisibility(View.VISIBLE);
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Bildirim a = snapshot.getValue(Bildirim.class);
                                bildirimler.add(a);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            bildirimlerisil.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                            henuzbildirimyok.setVisibility(View.VISIBLE);
                            bildirimlerrecycview.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        BildirimleriCek();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_bildirimler, container, false);

        init();

        bildirimlerrecycview.setOnScrollListener(onScrollListener);

        bildirimlerisil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BildirimleriSil();
            }
        });

        bildirimrefresh.setPullToRefreshListener(new PullToRefreshListener() {
            @Override
            public void onStartRefresh(@Nullable View view) {
                BildirimleriCek();
                bildirimrefresh.refreshDone();
            }
        });

        return rootView;
    }
}

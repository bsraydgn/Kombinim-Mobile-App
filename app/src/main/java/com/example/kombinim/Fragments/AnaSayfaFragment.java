package com.example.kombinim.Fragments;

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
import com.skhugh.simplepulltorefresh.PullToRefreshLayout;
import com.skhugh.simplepulltorefresh.PullToRefreshListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnaSayfaFragment extends Fragment {

    private View rootView;
    private List<Kombin> kombinler = new ArrayList<>();
    private KombinAdapter adapter;
    private ProgressDialog progress;
    private PullToRefreshLayout anasayfarefresh;
    private TextView henuzbirseyyok;

    private void ProgressBaslat() {
        progress = new ProgressDialog(getActivity());
        progress.setTitle("Ana Sayfa Yükleniyor");
        progress.setMessage("Bu biraz zaman alabilir.");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void init() {
        RecyclerView anasayfarecycview = rootView.findViewById(R.id.anasayfarecycview);
        anasayfarefresh = rootView.findViewById(R.id.anasayfarefresh);
        henuzbirseyyok = rootView.findViewById(R.id.gosterilecekbirkombinyok);

        anasayfarecycview.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        adapter = new KombinAdapter(kombinler, getContext(), getActivity());
        anasayfarecycview.setAdapter(adapter);
        anasayfarecycview.setLayoutManager(linearLayoutManager);
    }

    private void KombinleriCek() {
        ProgressBaslat();
        BaslaActivity.vb.getDbref("Arkadaslar")
                .child(BaslaActivity.vb.getUserID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        kombinler.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String idsi = Objects.requireNonNull(snapshot.getKey());
                                BaslaActivity.vb.getDbref("Kombinler")
                                        .child(idsi)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshotz) {
                                                if (dataSnapshotz.exists()) {
                                                    henuzbirseyyok.setVisibility(View.GONE);
                                                    for (DataSnapshot snapshotz : dataSnapshotz.getChildren()) {
                                                        Kombin yeni = snapshotz.getValue(Kombin.class);
                                                        kombinler.add(yeni);
                                                        adapter.notifyDataSetChanged();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        } else {
                            henuzbirseyyok.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        if (progress != null && progress.isShowing()) {
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
        rootView = inflater.inflate(R.layout.fragment_anasayfa, container, false);

        init();

        anasayfarefresh.setPullToRefreshListener(new PullToRefreshListener() {
            @Override
            public void onStartRefresh(@Nullable View view) {
                KombinleriCek();
                anasayfarefresh.refreshDone();
            }
        });

        return rootView;
    }
}

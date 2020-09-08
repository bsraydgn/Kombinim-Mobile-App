package com.example.kombinim.Activities;

import android.app.ProgressDialog;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kombinim.Adapters.ArkadasAdapter;
import com.example.kombinim.Adapters.SohbetlerAdapter;
import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Kullanici;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SohbetlerActivity extends AppCompatActivity {

    private RecyclerView sohbetlerrecycview;
    private TextView arkadaslarsize, henuzkonusmayok;
    private List<Kullanici> arkadaslar = new ArrayList<>();
    private ArkadasAdapter adapter;
    private ProgressDialog progress;
    private BottomSheetBehavior bottomSheetBehavior;
    private SohbetlerAdapter sohbetadapter;
    private List<Kullanici> mesajlar=new ArrayList<>();

    private void ProgressBaslat(){
        progress = new ProgressDialog(SohbetlerActivity.this);
        progress.setTitle("Sohbet Geçmişiniz Yükleniyor");
        progress.setMessage("Sohbetleriniz ve arkadaşlarınızın yüklenmesi zaman alabilir.");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void init() {
        View bottomSheet = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        sohbetlerrecycview = findViewById(R.id.sohbetlerrecycview);
        RecyclerView arkadaslarrecycview = findViewById(R.id.arkadaslarrecycview);
        LinearLayout managerarkadaslar = findViewById(R.id.managerarkadaslar);
        Toolbar actionbarsohbet = findViewById(R.id.actionbarsohbet);
        arkadaslarsize = findViewById(R.id.arkadaslarsize);
        henuzkonusmayok=findViewById(R.id.henuzkonusmayok);

        setSupportActionBar(actionbarsohbet);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Sohbetler");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //sohbetlerrecycview.setHasFixedSize(true);
        LinearLayoutManager sohbetmanager = new LinearLayoutManager(this);
        sohbetadapter=new SohbetlerAdapter(mesajlar,this,this);
        if(sohbetlerrecycview!=null){
            sohbetlerrecycview.setAdapter(sohbetadapter);
            sohbetlerrecycview.setLayoutManager(sohbetmanager);
        }

        arkadaslarrecycview.setHasFixedSize(true);
        GridLayoutManager linearLayoutManager = new GridLayoutManager(managerarkadaslar.getContext(), 3);
        adapter = new ArkadasAdapter(arkadaslar, this);
        arkadaslarrecycview.setAdapter(adapter);
        arkadaslarrecycview.setLayoutManager(linearLayoutManager);
    }

    private void ArkadaslariCek() {
        BaslaActivity
                .vb
                .getDbref("Arkadaslar")
                .child(BaslaActivity.vb.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String arkadassize = String.valueOf("Arkadaşlar (" + dataSnapshot.getChildrenCount() + ")");
                        arkadaslarsize.setText(arkadassize);
                        arkadaslar.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String uid = Objects.requireNonNull(snapshot.getKey());
                                BaslaActivity
                                        .vb
                                        .getDbref("Kullanicilar")
                                        .child(uid)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshotz) {
                                                Kullanici arkadas = dataSnapshotz.getValue(Kullanici.class);
                                                arkadaslar.add(arkadas);
                                                adapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        }else{
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SohbetleriCek() {
        BaslaActivity.vb.getDbref("Mesajlar")
                .child(BaslaActivity.vb.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mesajlar.clear();
                        if (dataSnapshot.exists()) {
                            sohbetlerrecycview.setVisibility(View.VISIBLE);
                            henuzkonusmayok.setVisibility(View.GONE);
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String uid = Objects.requireNonNull(snapshot.getKey());
                                BaslaActivity.vb.getDbref("Kullanicilar")
                                        .child(uid)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                                Kullanici e = dataSnapshot2.getValue(Kullanici.class);
                                                mesajlar.add(e);
                                                sohbetadapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        } else {
                            sohbetadapter.notifyDataSetChanged();
                            sohbetlerrecycview.setVisibility(View.GONE);
                            henuzkonusmayok.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void TumVerileriCek(){
        ProgressBaslat();
        ArkadaslariCek();
        SohbetleriCek();
        if(progress!=null && progress.isShowing()){
            progress.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    protected void onStart() {
        super.onStart();
        TumVerileriCek();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.onBackPressed();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sohbetler);

        init();

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED){
                    arkadaslarsize.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_asagi,0,0,0);
                }else{
                    arkadaslarsize.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_yukari,0,0,0);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

    }
}

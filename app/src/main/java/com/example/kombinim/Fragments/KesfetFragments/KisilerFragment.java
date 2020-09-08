package com.example.kombinim.Fragments.KesfetFragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.kombinim.Adapters.TumKullanicilarAdapter;
import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Kullanici;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class KisilerFragment extends Fragment  {

    private List<Kullanici> tumkullanicilar= new ArrayList<>();
    private RecyclerView listtumkullanicilar;
    private TumKullanicilarAdapter adapter;
    private TextView henuzkullaniciyok;
    private View rootView;
    private EditText kisiler_arama_text;
    private LinearLayout aramadadevreyegirer;
    private AVLoadingIndicatorView progress_kisi_araniyor;
    private TextView kisi_araniyor_text;
    
    private void TumunuListele(){
        BaslaActivity.vb.getDbref("Kullanicilar")
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tumkullanicilar.clear();
                if(dataSnapshot.exists()){
                    henuzkullaniciyok.setVisibility(View.GONE);
                    listtumkullanicilar.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Kullanici users = snapshot.getValue(Kullanici.class);
                        if(!BaslaActivity.vb.getUserID().equals(Objects.requireNonNull(users).getUserID())){
                            tumkullanicilar.add(users);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }else{
                    adapter.notifyDataSetChanged();
                    listtumkullanicilar.setVisibility(View.GONE);
                    henuzkullaniciyok.setVisibility(View.VISIBLE);
                }

                //ada göre sıralama
                Collections.sort(tumkullanicilar, new Comparator<Kullanici>() {
                    @Override
                    public int compare(Kullanici o1, Kullanici o2) {
                        String oName1=o1.getKullaniciadi();
                        String oName2=o2.getKullaniciadi();
                        return oName1.compareTo(oName2);
                    }
                });
                //ada göre sıralama
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init(){
        henuzkullaniciyok=rootView.findViewById(R.id.henuzkullaniciyok);
        kisiler_arama_text = rootView.findViewById(R.id.kisiler_arama_text);
        aramadadevreyegirer=rootView.findViewById(R.id.aramadadevreyegirer);
        progress_kisi_araniyor=rootView.findViewById(R.id.progress_kisi_araniyor);
        kisi_araniyor_text=rootView.findViewById(R.id.kisi_aranıyor_text);

        listtumkullanicilar= rootView.findViewById(R.id.listtumkullanicilar);
        listtumkullanicilar.setHasFixedSize(true);
        Context adapter_icin = getContext();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        adapter = new TumKullanicilarAdapter(tumkullanicilar, adapter_icin,getActivity());
        if (listtumkullanicilar != null) {
            listtumkullanicilar.setAdapter(adapter);
            listtumkullanicilar.setLayoutManager(linearLayoutManager);
        }
    }
    
    private void AramaDevrede(String text, boolean show, boolean goster){
        if(goster){
            aramadadevreyegirer.setVisibility(View.VISIBLE);
            kisi_araniyor_text.setText(text);
            if(show){
                progress_kisi_araniyor.show();
            }else{
                progress_kisi_araniyor.hide();
            }
        }else{
            aramadadevreyegirer.setVisibility(View.GONE);
        }
    }

    private void KullaniciAra(String kullaniciadi){
        AramaDevrede("Aranıyor...",true,true);
        BaslaActivity.vb.getDbref("Kullanicilar")
                .orderByChild("kullaniciadi")
                .equalTo(kullaniciadi)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tumkullanicilar.clear();
                        if(dataSnapshot.exists()){
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                Kullanici aranmis = snapshot.getValue(Kullanici.class);
                                tumkullanicilar.add(aranmis);
                                AramaDevrede(null,false,false);
                                adapter.notifyDataSetChanged();
                            }
                        }else{
                            AramaDevrede("Aramanızla eşleşen kayıt bulunamadı.",false,true);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_kisiler,container,false);

        init();

        kisiler_arama_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(TextUtils.isEmpty(s)){
                    AramaDevrede(null,false,false);
                    TumunuListele();
                }else{
                    KullaniciAra(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        TumunuListele();
    }
}

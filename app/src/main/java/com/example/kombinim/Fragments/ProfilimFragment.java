package com.example.kombinim.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.kombinim.Activities.ProfilDuzenlemeActivity;
import com.example.kombinim.Auth.LoginActivity;
import com.example.kombinim.BaslaActivity;
import com.example.kombinim.Fragments.ProfilFragments.PodyumFragment;
import com.example.kombinim.Fragments.ProfilFragments.YorumlarFragment;
import com.example.kombinim.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilimFragment extends Fragment {

    private CircleImageView profilresmim;
    private TextView kullaniciadim,durumum,kombinsayim,arkadassayim,yorumsayim;
    private ImageButton profilimmenu;
    private PopupMenu a;
    private Bundle bundle;
    private ProgressDialog progress;
    private View rootView;
    private Fragment podyum,yorumlar;

    private void ArkSayiCek(){
        BaslaActivity.vb.getDbref("Arkadaslar")
                .child(BaslaActivity.vb.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Long arksayisi=dataSnapshot.getChildrenCount();
                        arkadassayim.setText(String.valueOf(arksayisi));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void BilgilerimiCek(){
        BaslaActivity.vb.getDbref("Kullanicilar")
                .child(BaslaActivity.vb.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String profilresmi= Objects.requireNonNull(dataSnapshot.child("profilresmi").getValue()).toString();
                        String kullaniciadi= Objects.requireNonNull(dataSnapshot.child("kullaniciadi").getValue()).toString();
                        String durumu= Objects.requireNonNull(dataSnapshot.child("durum").getValue()).toString();

                        if(!profilresmi.equals("default")){
                            Picasso.get().load(profilresmi).networkPolicy(NetworkPolicy.OFFLINE)
                                    .resize(profilresmim.getWidth(), profilresmim.getHeight()).into(profilresmim, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get().load(profilresmi).resize(profilresmim.getWidth(), profilresmim.getHeight()).into(profilresmim);
                                }
                            });
                        }

                        kullaniciadim.setText(kullaniciadi);
                        durumum.setText(durumu);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void YorumSayiCek(){
        BaslaActivity.vb.getDbref("Yorumlar")
                .child("Profil_Yorumlari")
                .child(BaslaActivity.vb.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Long yorumsayisi=dataSnapshot.getChildrenCount();
                        yorumsayim.setText(String.valueOf(yorumsayisi));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void KombinSayiCek(){
        BaslaActivity.vb.getDbref("Kombinler")
                .child(BaslaActivity.vb.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Long kombinsayisi=dataSnapshot.getChildrenCount();
                        kombinsayim.setText(String.valueOf(kombinsayisi));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void ProfiliTasarla(){
        BilgilerimiCek();
        ArkSayiCek();
        YorumSayiCek();
        KombinSayiCek();
    }

    private void OlusumTasarlayici(){
        bundle=new Bundle();
        bundle.putString("user_id",BaslaActivity.vb.getUserID());

        podyum=new PodyumFragment();
        yorumlar=new YorumlarFragment();

        podyum.setArguments(bundle);
        yorumlar.setArguments(bundle);
    }

    private void ProgressBaslat(){
        progress = new ProgressDialog(getActivity());
        progress.setTitle("Profil Yükleniyor");
        progress.setMessage("Profilinizin yüklenmesi zaman alabilir.");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void init(){
        profilresmim=rootView.findViewById(R.id.profilresmim);
        kullaniciadim=rootView.findViewById(R.id.kullaniciadim);
        durumum=rootView.findViewById(R.id.durumum);
        kombinsayim=rootView.findViewById(R.id.profilimkombinsayim);
        arkadassayim=rootView.findViewById(R.id.profilimarkadassayim);
        yorumsayim=rootView.findViewById(R.id.profilimyorumsayisi);
        profilimmenu=rootView.findViewById(R.id.profilimmenu);
        BottomNavigationView navigation = rootView.findViewById(R.id.profilikinav);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        OlusumTasarlayici();

        Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frame_profil,podyum).commit();
    }

    @Override
    public void onStart() {
        super.onStart();
        ProfiliTasarla();
        if(progress!=null && progress.isShowing()){
            progress.dismiss();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment=null;

            switch (item.getItemId()) {
                case R.id.navigation_yorumlar:{
                    if(yorumlar!=null){
                        selectedFragment=yorumlar;
                    }else{
                        selectedFragment=new YorumlarFragment();
                        selectedFragment.setArguments(bundle);
                    }
                    break;
                }
                case R.id.navigation_podyum:{
                    if(podyum!=null){
                        selectedFragment=podyum;
                    }else{
                        selectedFragment=new PodyumFragment();
                        selectedFragment.setArguments(bundle);
                    }
                    break;
                }
            }
            Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction().replace(R.id.frame_profil, Objects.requireNonNull(selectedFragment)).commit();
            return true;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profilim,container,false);

        ProgressBaslat();

        init();

        profilimmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a=new PopupMenu(getContext(),profilimmenu);
                a.inflate(R.menu.profilmenu);
                a.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.cikisyap:{
                                BaslaActivity.vb.getAuth().signOut();
                                Intent loginintent=new Intent(getActivity(), LoginActivity.class);
                                startActivity(loginintent);
                                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                                getActivity().finish();
                                break;
                            }case R.id.profiliduzenle:{
                                Intent go=new Intent(getActivity(), ProfilDuzenlemeActivity.class);
                                startActivity(go);
                                Objects.requireNonNull(getActivity()).overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                break;
                            }
                        }
                        return false;
                    }
                });
                a.show();
            }
        });

        return rootView;
    }
}

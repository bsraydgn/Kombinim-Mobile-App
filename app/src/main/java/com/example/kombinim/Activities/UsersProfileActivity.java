package com.example.kombinim.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kombinim.BaslaActivity;
import com.example.kombinim.Fragments.ProfilFragments.PodyumFragment;
import com.example.kombinim.Fragments.ProfilFragments.YorumlarFragment;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Bildirim;
import com.example.kombinim.VeriSiniflari.Kullanici;
import com.example.kombinim.VeriSiniflari.ProfilRapor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersProfileActivity extends AppCompatActivity {

    private CircleImageView userpp;
    private TextView kombinsayisi;
    private TextView arkadassayisi;
    private TextView yorumsayisi;
    private TextView hakkinda;
    private TextView durumu;
    private Button arkadasekle;
    private Button arkadassilsil;
    private Button istekreddet;
    private Button arkadassil;
    private Button profilraporgonder;
    private ImageButton profilraporkapat;
    private CheckBox spam, diger;
    private EditText profilraporsebep;
    private Dialog profilrapordialog;
    private Kullanici guest=new Kullanici();
    private ProgressDialog dialog;
    private Bundle bundle;
    private RelativeLayout gorunurlukozelligi;
    private LinearLayout gorunurlukkapaliysa;
    private Fragment podyum, yorumlar;
    private String raporsebebi;
    private final static LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            5);
    private final static LinearLayout.LayoutParams paramse=new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            10);


    private void ArkadasSilDialog(){
        final Dialog ceDialog=new Dialog(UsersProfileActivity.this);
        ceDialog.setContentView(R.layout.arkadas_sil_dialog);
        ceDialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(ceDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        ceDialog.show();

        TextView arkadassilmedialog = ceDialog.findViewById(R.id.arkadassilmedialog);
        arkadassilsil=ceDialog.findViewById(R.id.arkadassilsil);
        Button arkadassilkapat = ceDialog.findViewById(R.id.arkadassilkapat);
        arkadassilsil.setEnabled(true);

        String birlestir=guest.getKullaniciadi()+" kişisini silmek istiyor musunuz ?";
        arkadassilmedialog.setText(birlestir);

        arkadassilsil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arkadassilsil.setEnabled(false);
                BaslaActivity.vb.getDbref("Arkadaslar")
                        .child(BaslaActivity.vb.getUserID())
                        .child(guest.getUserID())
                        .removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        BaslaActivity.vb.getDbref("Arkadaslar")
                                .child(guest.getUserID())
                                .child(BaslaActivity.vb.getUserID())
                                .removeValue()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(ceDialog.isShowing()){
                                    ceDialog.cancel();
                                }
                                Toast.makeText(UsersProfileActivity.this, guest.getKullaniciadi()+" arkadaşlıktan çıkarıldı !", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                if(ceDialog.isShowing()){
                                    ceDialog.cancel();
                                }
                                Toast.makeText(UsersProfileActivity.this, "Bir hata meydana geldi !", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(ceDialog.isShowing()){
                            ceDialog.cancel();
                        }
                        Toast.makeText(UsersProfileActivity.this, "Arkadaş silinemedi !", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        arkadassilkapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ceDialog.isShowing()){
                    ceDialog.cancel();
                }
            }
        });
    }

    private void IstekRedDialog(){

        final Dialog MeDialog=new Dialog(UsersProfileActivity.this);
        MeDialog.setContentView(R.layout.istek_reddetme_dialog);
        MeDialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(MeDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        MeDialog.show();

        TextView istekredtxt = MeDialog.findViewById(R.id.istekreddetmedialog);
        Button istekredkapat = MeDialog.findViewById(R.id.istekredkapat);
        istekreddet=MeDialog.findViewById(R.id.istekredreddet);
        istekreddet.setEnabled(true);

        String birlestirme=guest.getKullaniciadi()+" kişisinden gelen istek reddedilsin mi ?";
        istekredtxt.setText(birlestirme);

        istekreddet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                istekreddet.setEnabled(false);
                BaslaActivity.vb.getDbref("İstekler")
                        .child(BaslaActivity.vb.getUserID())
                        .child(guest.getUserID())
                        .child("istek").removeValue()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                BaslaActivity.vb.getDbref("İstekler")
                                        .child(guest.getUserID())
                                        .child(BaslaActivity.vb.getUserID()).child("istek")
                                        .removeValue()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                if(MeDialog.isShowing()){
                                                    MeDialog.cancel();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        if(MeDialog.isShowing()){
                                            MeDialog.cancel();
                                        }
                                        Toast.makeText(UsersProfileActivity.this, "Bir hata oluştu !", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(MeDialog.isShowing()){
                            MeDialog.cancel();
                        }
                        Toast.makeText(UsersProfileActivity.this, "İstek reddedilmiyor !", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        istekredkapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MeDialog.isShowing()){
                    MeDialog.cancel();
                }
            }
        });
    }

    private void BilgileriCek(){
        BaslaActivity.vb.getDbref("Kullanicilar")
                .child(guest.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        guest.setKullaniciadi(Objects.requireNonNull(dataSnapshot.child("kullaniciadi").getValue()).toString());
                        guest.setDurum(Objects.requireNonNull(dataSnapshot.child("durum").getValue()).toString());
                        guest.setHakkimda(Objects.requireNonNull(dataSnapshot.child("hakkimda").getValue()).toString());
                        guest.setProfilresmi(Objects.requireNonNull(dataSnapshot.child("profilresmi").getValue()).toString());

                        if (guest.getProfilresmi().equals("default")) {
                            userpp.setImageResource(R.drawable.ic_person_black_24dp);
                        } else {
                            Picasso.get().load(guest.getProfilresmi()).into(userpp);
                        }

                        durumu.setText(guest.getDurum());
                        if(guest.getHakkimda().equals("")){
                            hakkinda.setVisibility(View.GONE);
                        }else{
                            hakkinda.setText(guest.getHakkimda());
                        }
                        String toolbar=guest.getKullaniciadi();
                        Objects.requireNonNull(getSupportActionBar()).setTitle(toolbar);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        //arkadas sayisi cekme
        BaslaActivity.vb.getDbref("Arkadaslar")
                .child(guest.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Long arksayisi=dataSnapshot.getChildrenCount();
                        arkadassayisi.setText(String.valueOf(arksayisi));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        //arkadas sayisi cekme

        //yorum sayisi cekme
        BaslaActivity.vb.getDbref("Yorumlar")
                .child("Profil_Yorumlari")
                .child(guest.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Long yorumsayi=dataSnapshot.getChildrenCount();
                        yorumsayisi.setText(String.valueOf(yorumsayi));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        //yorum sayisi cekme

        //kombin sayisi cekme
        BaslaActivity.vb.getDbref("Kombinler")
                .child(guest.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Long kombinsayi=dataSnapshot.getChildrenCount();
                        kombinsayisi.setText(String.valueOf(kombinsayi));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        //kombin sayisi cekme
    }

    private void ArkadasEkle(){
        BaslaActivity.vb.getDbref("İstekler")
                .child(BaslaActivity.vb.getUserID())
                .child(guest.getUserID())
                .child("istek")
                .setValue("Giden İstek")
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        BaslaActivity.vb.getDbref("İstekler")
                                .child(guest.getUserID())
                                .child(BaslaActivity.vb.getUserID())
                                .child("istek").setValue("Gelen İstek")
                                .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UsersProfileActivity.this, "İstek Gönderilemedi. Bağlantını Kontrol Et !", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Bildirim bildirim=new Bildirim(BaslaActivity.vb.getUserID(),"istek"," sana arkadaşlık isteği gönderdi.");
                                BaslaActivity.vb.getDbref("Bildirimler")
                                        .child(guest.getUserID())
                                        .push()
                                        .setValue(bildirim);

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UsersProfileActivity.this, "İstek gönderilirken bir hata meydana geldi !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void IstegiGeriCek(){
        BaslaActivity.vb.getDbref("İstekler")
                .child(BaslaActivity.vb.getUserID())
                .child(guest.getUserID()).child("istek")
                .setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                BaslaActivity.vb.getDbref("İstekler")
                        .child(guest.getUserID())
                        .child(BaslaActivity.vb.getUserID())
                        .child("istek")
                        .setValue(null)
                        .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UsersProfileActivity.this, "Bir hata meydana geldi !", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UsersProfileActivity.this, "İstek Geri Çekilemiyor. Bağlantını Kontrol Et !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void IstegiKabulEt(){
        final String currentDate= DateFormat.getDateTimeInstance().format(new Date());
        BaslaActivity.vb.getDbref("Arkadaslar")
                .child(BaslaActivity.vb.getUserID())
                .child(guest.getUserID())
                .setValue(currentDate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        BaslaActivity.vb.getDbref("Arkadaslar")
                                .child(guest.getUserID())
                                .child(BaslaActivity.vb.getUserID())
                                .setValue(currentDate)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                BaslaActivity.vb.getDbref("İstekler")
                                        .child(BaslaActivity.vb.getUserID())
                                        .child(guest.getUserID())
                                        .child("istek")
                                        .setValue(null)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        BaslaActivity.vb.getDbref("İstekler")
                                                .child(guest.getUserID())
                                                .child(BaslaActivity.vb.getUserID())
                                                .child("istek").setValue(null)
                                                .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(UsersProfileActivity.this, "Bir hata meydana geldi !", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UsersProfileActivity.this, "İstek Geri Çekilemiyor. Bağlantını Kontrol Et !", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UsersProfileActivity.this, "Bir hata meydana geldi !", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UsersProfileActivity.this, "İstek kabul edilemedi !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void EtkilesimKontrolu(){
        BaslaActivity.vb.getDbref("İstekler")
                .child(BaslaActivity.vb.getUserID())
                .child(guest.getUserID())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String giden_istek_varmi= Objects.requireNonNull(dataSnapshot.child("istek").getValue()).toString();
                    if(giden_istek_varmi.equals("Giden İstek")){//eğer mevcut kullanıcı istek yollamışsa
                        arkadasekle.setBackground(getResources().getDrawable(R.drawable.kirmiziedittextbtn));
                        String text = "İsteği Geri Çek";
                        arkadasekle.setText(text);
                        arkadasekle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                IstegiGeriCek();
                            }
                        });
                        arkadassil.setVisibility(View.GONE);
                        arkadasekle.setLayoutParams(paramse);
                    }else{//eğer profildeki kişi, mevcut kullanıcıya istek yollamışsa
                        arkadasekle.setBackground(getResources().getDrawable(R.drawable.yesiledittextbtn));
                        String text = "İsteği Kabul Et";
                        arkadasekle.setText(text);
                        arkadasekle.setLayoutParams(params);
                        arkadasekle.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                IstegiKabulEt();
                            }
                        });
                        arkadassil.setVisibility(View.VISIBLE);
                        String txt = "İsteği Reddet";
                        arkadassil.setText(txt);
                        arkadassil.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                IstekRedDialog();
                            }
                        });
                    }
                }else{
                    BaslaActivity.vb.getDbref("Arkadaslar")
                            .child(BaslaActivity.vb.getUserID())
                            .child(guest.getUserID())
                            .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){//Eğer arkadaşlarsa
                                arkadasekle.setBackground(getResources().getDrawable(R.drawable.edittexticinbtndesign));
                                String text = "Mesaj Gönder";
                                arkadasekle.setText(text);
                                arkadasekle.setLayoutParams(params);
                                arkadasekle.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent go=new Intent(UsersProfileActivity.this, MesajActivity.class);
                                        go.putExtra("user_id",guest.getUserID());
                                        startActivity(go);
                                        overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                                    }
                                });
                                arkadassil.setVisibility(View.VISIBLE);
                                String txt = "Arkadaşlıktan Çıkar";
                                arkadassil.setText(txt);
                                arkadassil.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ArkadasSilDialog();
                                    }
                                });
                            }else{//ne arkadaş nede aralarında istek varsa
                                arkadasekle.setBackground(getResources().getDrawable(R.drawable.maviedittextbtn));
                                String txt = "Arkadaş Ekle";
                                arkadasekle.setText(txt);
                                arkadasekle.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ArkadasEkle();
                                    }
                                });
                                arkadassil.setVisibility(View.GONE);
                                arkadasekle.setLayoutParams(paramse);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void OlusumTasarlayici(){
        bundle=new Bundle();
        bundle.putString("user_id",guest.getUserID());

        podyum=new PodyumFragment();
        yorumlar=new YorumlarFragment();

        podyum.setArguments(bundle);
        yorumlar.setArguments(bundle);
    }

    private void GorunurlukKontrolu(){
        BaslaActivity.vb.getDbref("Kullanicilar")
                .child(guest.getUserID())
                .child("profilgorunurlugu")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(Objects.equals(dataSnapshot.getValue(), true)){
                            gorunurlukozelligi.setVisibility(View.VISIBLE);
                            gorunurlukkapaliysa.setVisibility(View.GONE);
                        }else{
                            BaslaActivity.vb.getDbref("Arkadaslar")
                                    .child(BaslaActivity.vb.getUserID())
                                    .child(guest.getUserID())
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshotz) {
                                            if(dataSnapshotz.exists()){
                                                gorunurlukozelligi.setVisibility(View.VISIBLE);
                                                gorunurlukkapaliysa.setVisibility(View.GONE);
                                            }else{
                                                gorunurlukozelligi.setVisibility(View.GONE);
                                                gorunurlukkapaliysa.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void ArkadaslikKontrolu(){
        BaslaActivity.vb.getDbref("Arkadaslar")
                .child(BaslaActivity.vb.getUserID())
                .child(guest.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            gorunurlukozelligi.setVisibility(View.VISIBLE);
                            gorunurlukkapaliysa.setVisibility(View.GONE);
                        }else{
                            BaslaActivity.vb.getDbref("Kullanicilar")
                                    .child(guest.getUserID())
                                    .child("profilgorunurlugu")
                                    .addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshoth) {
                                            if(Objects.equals(dataSnapshoth.getValue(), true)){
                                                gorunurlukozelligi.setVisibility(View.VISIBLE);
                                                gorunurlukkapaliysa.setVisibility(View.GONE);
                                            }else{
                                                gorunurlukozelligi.setVisibility(View.GONE);
                                                gorunurlukkapaliysa.setVisibility(View.VISIBLE);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void init(){
        dialog = new ProgressDialog(UsersProfileActivity.this);
        dialog.setTitle("Profil Yükleniyor");
        dialog.setMessage("Bu biraz zaman alabilir.");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        profilrapordialog=new Dialog(this);
        profilrapordialog.setContentView(R.layout.profil_raporlama_dialog);
        Objects.requireNonNull(profilrapordialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        profilrapordialog.setCanceledOnTouchOutside(false);

        Toolbar actionbarusersprofile = findViewById(R.id.actionbarusersprofile);
        setSupportActionBar(actionbarusersprofile);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        guest.setUserID(getIntent().getStringExtra("user_id"));

        userpp=findViewById(R.id.userpp);
        kombinsayisi=findViewById(R.id.userkombinsayisi);
        arkadassayisi=findViewById(R.id.userarkadassayisi);
        yorumsayisi=findViewById(R.id.useryorumsayisi);
        hakkinda=findViewById(R.id.userhakkinda);
        durumu=findViewById(R.id.userdurum);
        arkadasekle=findViewById(R.id.arkadasekle);
        arkadassil=findViewById(R.id.arkadassil);
        gorunurlukozelligi=findViewById(R.id.gorunurlukozelligi);
        gorunurlukkapaliysa=findViewById(R.id.gorunurlukkapaliysa);
        profilraporkapat=profilrapordialog.findViewById(R.id.profilraporkapat);
        profilraporgonder=profilrapordialog.findViewById(R.id.profilraporgonder);
        profilraporsebep=profilrapordialog.findViewById(R.id.profilraporsebep);
        spam=profilrapordialog.findViewById(R.id.profilraporspam);
        diger=profilrapordialog.findViewById(R.id.profilrapordiger);

        BottomNavigationView navigation = findViewById(R.id.userikinav);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        OlusumTasarlayici();

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_user,podyum).commit();
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
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_user, Objects.requireNonNull(selectedFragment)).commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_profile);

        init();

        spam.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(spam.isChecked()){
                    diger.setChecked(false);
                    profilraporgonder.setEnabled(true);
                    raporsebebi=spam.getText().toString();
                }else{
                    profilraporgonder.setEnabled(false);
                }
            }
        });

        diger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(diger.isChecked()){
                    spam.setChecked(false);
                    profilraporsebep.setEnabled(true);
                    profilraporgonder.setEnabled(false);
                }else{
                    profilraporgonder.setEnabled(false);
                    profilraporsebep.setEnabled(false);
                }
            }
        });

        profilraporsebep.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==0){
                    profilraporgonder.setEnabled(false);
                }else{
                    raporsebebi=profilraporsebep.getText().toString();
                    profilraporgonder.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        profilraporkapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(profilrapordialog.isShowing()){
                    profilrapordialog.cancel();
                }
            }
        });

        profilraporgonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfilRapor rapor=new ProfilRapor(guest.getUserID(),raporsebebi);
                Raporla(rapor);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        BilgileriCek();
        GorunurlukKontrolu();
        ArkadaslikKontrolu();
        EtkilesimKontrolu();

        if(dialog!=null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.usersmenu, menu);
        return true;
    }

    private void Raporla(ProfilRapor rapor){
        BaslaActivity.vb.getDbref("Raporlar")
                .child("Profil_Raporlari")
                .push()
                .setValue(rapor)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        if(profilrapordialog.isShowing()){
                            profilrapordialog.cancel();
                        }
                        Toast.makeText(UsersProfileActivity.this, "Raporlandı !", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(profilrapordialog.isShowing()){
                    profilrapordialog.cancel();
                }
                Toast.makeText(UsersProfileActivity.this, "Raporlanamadı. Tekrar Dene !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //engelle ve şikayet et butonları seçildiğinde
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                this.onBackPressed();
                break;
            }
            case R.id.sikayetet:{
                profilrapordialog.show();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    //engelle ve şikayet et butonları seçildiğinde
}

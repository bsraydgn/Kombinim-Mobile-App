package com.example.kombinim.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kombinim.Adapters.BegenenAdapter;
import com.example.kombinim.Adapters.KombinPaylasAdapter;
import com.example.kombinim.Adapters.KombinYorumAdapter;
import com.example.kombinim.AppTemel.GetTimeAgo;
import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Begeni;
import com.example.kombinim.VeriSiniflari.Bildirim;
import com.example.kombinim.VeriSiniflari.Favori;
import com.example.kombinim.VeriSiniflari.Kombin;
import com.example.kombinim.VeriSiniflari.Kullanici;
import com.example.kombinim.VeriSiniflari.Magaza;
import com.example.kombinim.VeriSiniflari.Rapor;
import com.example.kombinim.VeriSiniflari.Yorum;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.skhugh.simplepulltorefresh.PullToRefreshLayout;
import com.skhugh.simplepulltorefresh.PullToRefreshListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class KombinActivity extends AppCompatActivity {

    private TextView kombinsahibiadi, aciklama, tarih, begenisayisi, favorisayisi, tarz, yorumsayisi;
    private ImageView kombinresmi;
    private CircleImageView kombinsahibiresmi;
    private ImageButton begeni, favori, sil, menu, kombin_yorumgonder, paylas;
    private ProgressDialog progress;
    private Context context = this;
    private Button kombinsilsil, kombinsilkapat, urunegit;
    private CheckBox uygunsuzicerik, aykiripaylasim, diger;
    private EditText sebep;
    private Button raporgonder;
    private String raporlamasebebi;
    private ImageButton raporkapat;
    private PopupMenu c;
    private List<Yorum> yorumlar = new ArrayList<>();
    private List<Kullanici> begenenler = new ArrayList<>();
    private List<Kullanici> favorileyenler = new ArrayList<>();
    private KombinYorumAdapter adapter;
    private BegenenAdapter begenenAdapter, favorileyenAdapter;
    private LinearLayout begenenyoksacikar;
    private LinearLayout favoriyoksacikar;
    private RelativeLayout kombin_magaza_yoksa_cikar;
    private EditText kombin_yorumyazmaedittxt;
    private PullToRefreshLayout kombin_refresh;
    private AVLoadingIndicatorView kombin_yukleniyor;

    //
    private GetTimeAgo getTimeAgo;
    public static String kombin_kimden, kombin_id;
    private String kombin_aciklama, kombin_resmi, kombin_tarz;
    private long kombin_tarih;
    private Magaza kombin_magaza;
    //

    private static int PERMISSION_STORAGE_CODE=56;

    private void ProgressBaslat() {
        progress = new ProgressDialog(KombinActivity.this);
        progress.setTitle("Kombin Yükleniyor");
        progress.setMessage("Kombinin yüklenmesi zaman alabilir.");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void ExtralariCek() {
        kombin_id = getIntent().getStringExtra("kombin_id");
        kombin_kimden = getIntent().getStringExtra("kombin_kimden");
    }

    private void init() {
        RecyclerView yorumlarrecycview = findViewById(R.id.k_yorumlarrecycview);
        Toolbar apbar = findViewById(R.id.kombin_activity_bar);
        kombinsahibiadi = findViewById(R.id.k_isim);
        aciklama = findViewById(R.id.k_aciklama);
        tarih = findViewById(R.id.k_tarih);
        begenisayisi = findViewById(R.id.k_begeni_sayisi);
        favorisayisi = findViewById(R.id.k_favori_sayisi);
        tarz = findViewById(R.id.k_tarz);
        kombinresmi = findViewById(R.id.k_activity_kombin_resmi);
        kombinsahibiresmi = findViewById(R.id.k_kisi_resim);
        begeni = findViewById(R.id.k_begen);
        favori = findViewById(R.id.k_favori);
        paylas=findViewById(R.id.k_paylas);
        sil = findViewById(R.id.k_sil);
        kombin_yukleniyor=findViewById(R.id.k_activity_kombin_yukleniyor);
        menu = findViewById(R.id.k_menu);
        kombin_yorumyazmaedittxt = findViewById(R.id.kombin_yorumyazmaedit);
        kombin_yorumgonder = findViewById(R.id.kombin_yorumgonderımgbtn);
        RecyclerView begenenlerrecycview = findViewById(R.id.begenenlerrecycview);
        RecyclerView favorileyenlerrecycview = findViewById(R.id.favorileyenlerrecycview);
        begenenyoksacikar = findViewById(R.id.begenenyoksacikar);
        favoriyoksacikar = findViewById(R.id.favorileyenyoksacikar);
        yorumsayisi = findViewById(R.id.kombin_yorum_sayisi);
        kombin_refresh = findViewById(R.id.kombin_refresh);
        urunegit=findViewById(R.id.urunegit);
        kombin_magaza_yoksa_cikar = findViewById(R.id.kombin_magaza_yoksa_cikar);

        setSupportActionBar(apbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Kombin");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        begenenlerrecycview.setHasFixedSize(true);
        LinearLayoutManager begenenManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        begenenAdapter = new BegenenAdapter(begenenler, this);
        begenenlerrecycview.setAdapter(begenenAdapter);
        begenenlerrecycview.setLayoutManager(begenenManager);

        favorileyenlerrecycview.setHasFixedSize(true);
        LinearLayoutManager favorileyenManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        favorileyenAdapter = new BegenenAdapter(favorileyenler, this);//clickeventlerifarklı olacak
        favorileyenlerrecycview.setAdapter(favorileyenAdapter);
        favorileyenlerrecycview.setLayoutManager(favorileyenManager);

        yorumlarrecycview.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        adapter = new KombinYorumAdapter(yorumlar, this);
        yorumlarrecycview.setAdapter(adapter);
        yorumlarrecycview.setLayoutManager(linearLayoutManager);

        ExtralariCek();
    }

    private void PaylasDialog() {
        Kombin gonderilecekKombin = new Kombin(kombin_aciklama, kombin_resmi,kombin_tarz,null);
        gonderilecekKombin.setId(kombin_id);
        gonderilecekKombin.setKimden(kombin_kimden);
        final Dialog MyDialog = new Dialog(context);
        MyDialog.setContentView(R.layout.paylas_dialog);
        Objects.requireNonNull(MyDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        MyDialog.setCanceledOnTouchOutside(true);
        MyDialog.show();

        EditText paylasara = MyDialog.findViewById(R.id.paylas_dialog_ara);
        RecyclerView paylasarkliste = MyDialog.findViewById(R.id.paylas_dialog_ark_liste);

        final List<Kullanici> arkadaslar = new ArrayList<>();
        final KombinPaylasAdapter kombinPaylasAdapter = new KombinPaylasAdapter(arkadaslar, gonderilecekKombin);
        paylasarkliste.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        paylasarkliste.setAdapter(kombinPaylasAdapter);
        paylasarkliste.setLayoutManager(linearLayoutManager);


        BaslaActivity.vb
                .getDbref("Arkadaslar")
                .child(BaslaActivity.vb.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        arkadaslar.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String uid = Objects.requireNonNull(snapshot.getKey());
                                BaslaActivity.vb
                                        .getDbref("Kullanicilar")
                                        .child(uid)
                                        .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshotz) {
                                                Kullanici arkadas = dataSnapshotz.getValue(Kullanici.class);
                                                arkadaslar.add(arkadas);
                                                kombinPaylasAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        } else {
                            kombinPaylasAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void GaleriyeKaydet(ImageView iv) throws IOException {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(context),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            BitmapDrawable draw = (BitmapDrawable) iv.getDrawable();
            Bitmap bitmap = draw.getBitmap();

            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/Kombinim");
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
            String fileName = System.currentTimeMillis() + ".jpg";
            File outFile = new File(dir, fileName);
            FileOutputStream outStream = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();

            Toast.makeText(this, "Fotoğraf galeriye kaydedildi.", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(outFile));
            sendBroadcast(intent);
        } else {
            //dosya izni runtime permission
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(context)
                    .setTitle("İzin gerekli !")
                    .setMessage("Bu izin cihazınıza resim kaydetmek için gereklidir.")
                    .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(KombinActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
                        }
                    }).setNegativeButton("Kapat", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setIcon(R.drawable.ic_folder).create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
        }
    }

    private void KombiniCek() {
        BaslaActivity.vb.getDbref("Kombinler")
                .child(kombin_kimden)
                .orderByChild("id")
                .equalTo(kombin_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                kombin_aciklama = Objects.requireNonNull(snapshot.child("aciklama").getValue()).toString();
                                kombin_tarz = Objects.requireNonNull(snapshot.child("tarz").getValue()).toString();
                                kombin_tarih = Long.parseLong(Objects.requireNonNull(snapshot.child("tarih").getValue()).toString());
                                kombin_resmi = Objects.requireNonNull(snapshot.child("resim").getValue()).toString();
                                if (snapshot.child("magaza").exists()) {
                                    kombin_magaza = snapshot.child("magaza").getValue(Magaza.class);
                                    kombin_magaza_yoksa_cikar.setVisibility(View.VISIBLE);
                                    urunegit.setText(kombin_magaza.getIsmi());
                                    urunegit.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (!kombin_magaza.getLink().startsWith("http://") && !kombin_magaza.getLink().startsWith("https://")){
                                                String url = "http://" + kombin_magaza.getLink();
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                                startActivity(browserIntent);
                                            }else{
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(kombin_magaza.getLink()));
                                                startActivity(browserIntent);
                                            }

                                        }
                                    });
                                } else {
                                    kombin_magaza = null;
                                    kombin_magaza_yoksa_cikar.setVisibility(View.GONE);
                                }
                                //kullanıcıya göre nelere erişebileceği kontrolü
                                if (BaslaActivity.vb.getUserID().equals(kombin_kimden)) {
                                    sil.setVisibility(View.VISIBLE);
                                    menu.setVisibility(View.GONE);
                                } else {
                                    sil.setVisibility(View.GONE);
                                    menu.setVisibility(View.VISIBLE);
                                }

                                if (kombin_aciklama.equals("")) {
                                    aciklama.setVisibility(View.GONE);
                                } else {
                                    aciklama.setVisibility(View.VISIBLE);
                                    aciklama.setText(kombin_aciklama);
                                }

                                if (kombin_tarz.equals("Bir giyim tarzı seç...")) {
                                    tarz.setVisibility(View.GONE);
                                } else {
                                    tarz.setVisibility(View.VISIBLE);
                                    tarz.setText(kombin_tarz);
                                }

                                Picasso.get().load(kombin_resmi)
                                        .into(kombinresmi, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        kombin_yukleniyor.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError(Exception e) {

                                    }
                                });
                                getTimeAgo=new GetTimeAgo();
                                final String trh = getTimeAgo.getTimeAgo(kombin_tarih);
                                tarih.setText(trh);
                                //kullanıcıya göre nelere erişebileceği kontrolü

                                BaslaActivity.vb.getDbref("Kullanicilar")
                                        .child(kombin_kimden)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                String profilresmi = Objects.requireNonNull(dataSnapshot.child("profilresmi").getValue()).toString();
                                                String kullaniciadi = Objects.requireNonNull(dataSnapshot.child("kullaniciadi").getValue()).toString();

                                                if (profilresmi.equals("default")) {
                                                    kombinsahibiresmi.setImageResource(R.drawable.ic_person_black_24dp);
                                                } else {
                                                    Picasso.get().load(profilresmi).into(kombinsahibiresmi);
                                                }
                                                kombinsahibiadi.setText(kullaniciadi);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }

                        } else {
                            Toast.makeText(context, "Kombin kullanıcı tarafından kaldırıldı !", Toast.LENGTH_SHORT).show();
                            Intent go = new Intent(KombinActivity.this, BaslaActivity.class);
                            startActivity(go);
                            finish();
                            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void BegeniveFavoriKontrolu() {
        //aktif kullanici o kombini begenmismi kontrolü
        BaslaActivity.vb.getDbref("Begeniler")
                .child("Kombin_Beğenileri")
                .child(kombin_id)
                .orderByChild("begenen")
                .equalTo(BaslaActivity.vb.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            begeni.setImageResource(R.drawable.ic_like_okay);
                            begeni.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().removeValue();
                                    }
                                    BegenenleriCek();
                                }
                            });
                        } else {
                            begeni.setImageResource(R.drawable.ic_like);
                            begeni.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                                    Begeni yenibegeni = new Begeni(BaslaActivity.vb.getUserID(), currentDate);
                                    BaslaActivity.vb.getDbref("Begeniler")
                                            .child("Kombin_Beğenileri")
                                            .child(kombin_id)
                                            .push()
                                            .setValue(yenibegeni);
                                    if (!kombin_kimden.equals(BaslaActivity.vb.getUserID())) {
                                        Bildirim bildirim = new Bildirim(kombin_id, "kombin_begeni", " senin bir paylaşımını beğendi.");
                                        BaslaActivity.vb.getDbref("Bildirimler")
                                                .child(kombin_kimden)
                                                .push()
                                                .setValue(bildirim);
                                    }
                                    BegenenleriCek();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        //aktif kullanici o kombini begenmismi kontrolü

        //aktif kullanici o kombini favorilemişmi kontrolü
        BaslaActivity.vb.getDbref("Favoriler")
                .child(kombin_id)
                .orderByChild("favorileyen")
                .equalTo(BaslaActivity.vb.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            favori.setImageResource(R.drawable.ic_bookmark_okay);
                            favori.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().removeValue();
                                    }
                                    BaslaActivity.vb.getDbref("FavoriListesi")
                                            .child(BaslaActivity.vb.getUserID())
                                            .child(kombin_id).removeValue();
                                    FavorileyenleriCek();
                                }
                            });
                        } else {
                            favori.setImageResource(R.drawable.ic_bookmark);
                            favori.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                                    Favori yenifavori = new Favori(BaslaActivity.vb.getUserID(), currentDate);
                                    //Kombinin Favorilenmesi
                                    BaslaActivity.vb.getDbref("Favoriler")
                                            .child(kombin_id)
                                            .push()
                                            .setValue(yenifavori);
                                    //Kombinin Favorilenmesi
                                    if (!kombin_kimden.equals(BaslaActivity.vb.getUserID())) { //kendine bildirim gönderilmesin diye
                                        //Bildirim Gönderilmesi
                                        Bildirim bildirim = new Bildirim(kombin_id, "kombin_favori", " senin bir paylaşımını favorilere ekledi.");
                                        BaslaActivity.vb.getDbref("Bildirimler")
                                                .child(kombin_kimden)
                                                .push()
                                                .setValue(bildirim);
                                        //Bildirim Gönderilmesi
                                    }
                                    //Veritabanında kisinin listesine eklenmesi
                                    BaslaActivity.vb.getDbref("FavoriListesi")
                                            .child(BaslaActivity.vb.getUserID())
                                            .child(kombin_id)
                                            .setValue(kombin_kimden);
                                    //Veritabanında kisinin listesine eklenmesi
                                    FavorileyenleriCek();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        //aktif kullanici o kombini favorilemişmi kontrolü
    }

    public void YorumlariCek() {
        BaslaActivity.vb.getDbref("Yorumlar")
                .child("Kombin_Yorumlari")
                .child(kombin_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        yorumlar.clear();

                        Long sayi = dataSnapshot.getChildrenCount();
                        String yorumsays = "Yorumlar (" + sayi.toString() + ")";
                        yorumsayisi.setText(yorumsays);

                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Yorum a = snapshot.getValue(Yorum.class);
                                yorumlar.add(a);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void HepsiniTamamla() {
        ProgressBaslat();
        KombiniCek();
        BegeniveFavoriKontrolu();
        YorumlariCek();
        BegenenleriCek();
        FavorileyenleriCek();
        if (progress.isShowing()) {
            progress.cancel();
        }
    }

    private void BegenenleriCek() {
        BaslaActivity.vb.getDbref("Begeniler")
                .child("Kombin_Beğenileri")
                .child(kombin_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        begenenler.clear();

                        Long sayi = dataSnapshot.getChildrenCount();
                        String begenisays = sayi.toString() + " Beğeni";
                        if (begenisays.equals("0 Beğeni")) {
                            begenisayisi.setText("");
                        } else {
                            begenisayisi.setText(begenisays);
                        }

                        if (dataSnapshot.exists()) {
                            begenenyoksacikar.setVisibility(View.VISIBLE);
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String uid = Objects.requireNonNull(snapshot.child("begenen").getValue()).toString();
                                BaslaActivity.vb.getDbref("Kullanicilar")
                                        .child(uid)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Kullanici nr = dataSnapshot.getValue(Kullanici.class);
                                                begenenler.add(nr);
                                                begenenAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        } else {
                            begenenAdapter.notifyDataSetChanged();
                            begenenyoksacikar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void FavorileyenleriCek() {
        BaslaActivity.vb.getDbref("Favoriler")
                .child(kombin_id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        favorileyenler.clear();

                        Long sayie = dataSnapshot.getChildrenCount();
                        String favorisays = sayie.toString() + " Favori";
                        if (favorisays.equals("0 Favori")) {
                            favorisayisi.setText("");
                        } else {
                            favorisayisi.setText(favorisays);
                        }

                        if (dataSnapshot.exists()) {
                            favoriyoksacikar.setVisibility(View.VISIBLE);
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String uniid = Objects.requireNonNull(snapshot.child("favorileyen").getValue()).toString();
                                BaslaActivity.vb.getDbref("Kullanicilar")
                                        .child(uniid)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                Kullanici ekle = dataSnapshot.getValue(Kullanici.class);
                                                favorileyenler.add(ekle);
                                                favorileyenAdapter.notifyDataSetChanged();
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                            }
                        } else {
                            favorileyenAdapter.notifyDataSetChanged();
                            favoriyoksacikar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kombin);

        init();

        HepsiniTamamla();

        paylas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaylasDialog();
            }
        });

        kombin_refresh.setPullToRefreshListener(new PullToRefreshListener() {
            @Override
            public void onStartRefresh(@Nullable View view) {
                HepsiniTamamla();
                kombin_refresh.refreshDone();
            }
        });

        kombin_yorumgonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(kombin_yorumyazmaedittxt.getText().toString())) {
                    final String text = kombin_yorumyazmaedittxt.getText().toString();
                    final Yorum yeniyorum = new Yorum(text, BaslaActivity.vb.getUserID());
                    BaslaActivity.vb.getDbref("Yorumlar")
                            .child("Kombin_Yorumlari")
                            .child(kombin_id)
                            .push()
                            .setValue(yeniyorum)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    kombin_yorumgonder.setEnabled(true);
                                    Toast.makeText(KombinActivity.this, "Yorum gönderilemiyor !", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            kombin_yorumyazmaedittxt.setText(null);
                            kombin_yorumgonder.setEnabled(true);
                            Bildirim bildirim = new Bildirim(kombin_id, "kombine_yorum", " paylaşımına yeni bir yorum bıraktı.");
                            BaslaActivity.vb.getDbref("Bildirimler")
                                    .child(kombin_kimden)
                                    .push()
                                    .setValue(bildirim);
                            YorumlariCek();
                        }
                    });
                } else {
                    kombin_yorumgonder.setEnabled(true);
                }
            }
        });

        if (!kombin_kimden.equals(BaslaActivity.vb.getUserID())) {
            kombinsahibiresmi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent go = new Intent(KombinActivity.this, UsersProfileActivity.class);
                    go.putExtra("user_id", kombin_kimden);
                    startActivity(go);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            });

            kombinsahibiadi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent go = new Intent(KombinActivity.this, UsersProfileActivity.class);
                    go.putExtra("user_id", kombin_kimden);
                    startActivity(go);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            });
        }

        sil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog MyDialog = new Dialog(context);
                MyDialog.setContentView(R.layout.kombin_silme_dialog);
                Objects.requireNonNull(MyDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
                MyDialog.setCanceledOnTouchOutside(false);
                MyDialog.show();

                kombinsilsil = MyDialog.findViewById(R.id.kombinsilsil);
                kombinsilkapat = MyDialog.findViewById(R.id.kombinsilkapat);

                kombinsilkapat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MyDialog.isShowing()) {
                            MyDialog.cancel();
                        }
                    }
                });

                kombinsilsil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Önce beğenileri silinir
                        BaslaActivity.vb.getDbref("Begeniler")
                                .child("Kombin_Beğenileri")
                                .child(kombin_id)
                                .removeValue();

                        //Sonra yorumlari silinir
                        BaslaActivity.vb.getDbref("Yorumlar")
                                .child("Kombin_Yorumlari")
                                .child(kombin_id)
                                .removeValue();

                        //ardından bu kombini favorileyenlerin özel listesinden silinir
                        BaslaActivity.vb.getDbref("Favoriler")
                                .child(kombin_id)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                String id = Objects.requireNonNull(snapshot.child("favorileyen").getValue()).toString();
                                                BaslaActivity.vb.getDbref("FavoriListesi")
                                                        .child(id)
                                                        .child(kombin_id)
                                                        .removeValue();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                        //ardından favoriler silinir
                        BaslaActivity.vb.getDbref("Favoriler")
                                .child(kombin_id)
                                .removeValue();

                        //ardından kombin silinir
                        BaslaActivity.vb.getDbref("Kombinler")
                                .child(BaslaActivity.vb.getUserID())
                                .orderByChild("id").equalTo(kombin_id)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            snapshot.getRef().removeValue();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                        //en son storageden resim silinir
                        BaslaActivity.vb.getFbstorage().child("Kombinler")
                                .child(BaslaActivity.vb.getUserID())
                                .child(kombin_id)
                                .delete();

                        if (MyDialog.isShowing()) {
                            MyDialog.cancel();
                        }

                    }
                });
            }
        });

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c = new PopupMenu(v.getContext(), menu);
                c.inflate(R.menu.kombinmenu);
                c.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.uygunsuz_icerik: {
                                final Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.raporlama_dialog);
                                dialog.setCanceledOnTouchOutside(false);
                                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
                                dialog.show();

                                uygunsuzicerik = dialog.findViewById(R.id.raporuygunsuz);
                                aykiripaylasim = dialog.findViewById(R.id.raporaykiripaylasim);
                                diger = dialog.findViewById(R.id.rapordiger);
                                sebep = dialog.findViewById(R.id.raporsebep);
                                raporgonder = dialog.findViewById(R.id.raporgonder);
                                raporkapat = dialog.findViewById(R.id.raporkapat);

                                raporkapat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (dialog.isShowing()) {
                                            dialog.cancel();
                                        }
                                    }
                                });

                                uygunsuzicerik.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (uygunsuzicerik.isChecked()) {
                                            aykiripaylasim.setChecked(false);
                                            diger.setChecked(false);
                                            raporlamasebebi = uygunsuzicerik.getText().toString();
                                            raporgonder.setEnabled(true);
                                        } else {
                                            raporgonder.setEnabled(false);
                                        }
                                    }
                                });

                                aykiripaylasim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (aykiripaylasim.isChecked()) {
                                            uygunsuzicerik.setChecked(false);
                                            diger.setChecked(false);
                                            raporlamasebebi = aykiripaylasim.getText().toString();
                                            raporgonder.setEnabled(true);
                                        } else {
                                            raporgonder.setEnabled(false);
                                        }
                                    }
                                });

                                diger.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                        if (diger.isChecked()) {
                                            aykiripaylasim.setChecked(false);
                                            uygunsuzicerik.setChecked(false);
                                            sebep.setEnabled(true);
                                            raporgonder.setEnabled(false);
                                        } else {
                                            sebep.setEnabled(false);
                                            raporgonder.setEnabled(false);
                                        }
                                    }
                                });

                                sebep.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        if (s.length() != 0) {
                                            raporgonder.setEnabled(true);
                                            raporlamasebebi = sebep.getText().toString();
                                        } else {
                                            raporgonder.setEnabled(false);
                                        }
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }
                                });

                                raporgonder.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(final View v) {
                                        Rapor rapor = new Rapor(kombin_kimden, kombin_id, raporlamasebebi);
                                        BaslaActivity.vb.getDbref("Raporlar")
                                                .child("Kombin_Raporlari")
                                                .push()
                                                .setValue(rapor)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        if (dialog.isShowing()) {
                                                            dialog.cancel();
                                                        }
                                                        Toast.makeText(v.getContext(), "Raporlandı !", Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                if (dialog.isShowing()) {
                                                    dialog.cancel();
                                                }
                                                Toast.makeText(v.getContext(), "Raporlananamadı. Tekrar Dene !", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                                break;
                            }case R.id.galeriye_kaydet:{
                                try {
                                    GaleriyeKaydet(kombinresmi);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                        return false;
                    }
                });
                c.show();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
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
}

package com.example.kombinim.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kombinim.Activities.KombinActivity;
import com.example.kombinim.Activities.UsersProfileActivity;
import com.example.kombinim.AppTemel.GetTimeAgo;
import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Begeni;
import com.example.kombinim.VeriSiniflari.Bildirim;
import com.example.kombinim.VeriSiniflari.Favori;
import com.example.kombinim.VeriSiniflari.Kombin;
import com.example.kombinim.VeriSiniflari.Kullanici;
import com.example.kombinim.VeriSiniflari.Rapor;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
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

public class KombinAdapter extends RecyclerView.Adapter<KombinAdapter.KombinlerViewHolder> {

    private List<Kombin> kombinler;
    private PopupMenu c;
    private List<String> favoriIDs;
    private Context context;
    private Button kombinsilsil, kombinsilkapat;
    private CheckBox uygunsuzicerik, aykiripaylasim, diger;
    private EditText sebep;
    private Button raporgonder;
    private String raporlamasebebi;
    private ImageButton raporkapat;
    private Activity activity;
    private static int PERMISSION_STORAGE_CODE = 3;

    private void PaylasDialog(Kombin gonderilecekKombin) {
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

            Toast.makeText(activity, "Fotoğraf galeriye kaydedildi.", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(outFile));
            activity.sendBroadcast(intent);
        } else {
            //dosya izni runtime permission
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(activity),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(context)
                    .setTitle("İzin gerekli !")
                    .setMessage("Bu izin cihazınıza resim kaydetmek için gereklidir.")
                    .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Objects.requireNonNull(activity),
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
                        }
                    }).setNegativeButton("Kapat", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setIcon(R.drawable.ic_folder).create().show();
        } else {
            ActivityCompat.requestPermissions(Objects.requireNonNull(activity),
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
        }
    }

    public KombinAdapter(List<Kombin> kombinler, Context context, Activity activity) {
        this.kombinler = kombinler;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public KombinlerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_kombin_layout, parent, false);
        return new KombinlerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final KombinlerViewHolder holder, int position) {
        final Kombin a = kombinler.get(position);

        //kullanıcıya göre nelere erişebileceği kontrolü
        if (BaslaActivity.vb.getUserID().equals(a.getKimden())) {
            holder.sil.setVisibility(View.VISIBLE);
            holder.menu.setVisibility(View.GONE);
        } else {
            holder.sil.setVisibility(View.GONE);
            holder.menu.setVisibility(View.VISIBLE);
        }
        //kullanıcıya göre nelere erişebileceği kontrolü

        //aktif kullanici o kombini begenmismi kontrolü
        BaslaActivity.vb.getDbref("Begeniler")
                .child("Kombin_Beğenileri")
                .child(a.getId())
                .orderByChild("begenen")
                .equalTo(BaslaActivity.vb.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            holder.begen.setImageResource(R.drawable.ic_like_okay);
                            holder.begen.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().removeValue();
                                    }
                                }
                            });
                        } else {
                            holder.begen.setImageResource(R.drawable.ic_like);
                            holder.begen.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                                    Begeni yenibegeni = new Begeni(BaslaActivity.vb.getUserID(), currentDate);
                                    BaslaActivity.vb.getDbref("Begeniler")
                                            .child("Kombin_Beğenileri")
                                            .child(a.getId())
                                            .push()
                                            .setValue(yenibegeni);
                                    if (!a.getKimden().equals(BaslaActivity.vb.getUserID())) {
                                        Bildirim bildirim = new Bildirim(a.getId(), "kombin_begeni", " senin bir paylaşımını beğendi.");
                                        BaslaActivity.vb.getDbref("Bildirimler")
                                                .child(a.getKimden())
                                                .push()
                                                .setValue(bildirim);
                                    }
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
                .child(a.getId())
                .orderByChild("favorileyen")
                .equalTo(BaslaActivity.vb.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            holder.favori.setImageResource(R.drawable.ic_bookmark_okay);
                            holder.favori.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().removeValue();
                                    }
                                    BaslaActivity.vb.getDbref("FavoriListesi")
                                            .child(BaslaActivity.vb.getUserID())
                                            .child(a.getId()).removeValue();
                                }
                            });
                        } else {
                            holder.favori.setImageResource(R.drawable.ic_bookmark);
                            holder.favori.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                                    Favori yenifavori = new Favori(BaslaActivity.vb.getUserID(), currentDate);
                                    //Kombinin Favorilenmesi
                                    BaslaActivity.vb.getDbref("Favoriler")
                                            .child(a.getId())
                                            .push()
                                            .setValue(yenifavori);
                                    //Kombinin Favorilenmesi
                                    if (!a.getKimden().equals(BaslaActivity.vb.getUserID())) { //kendine bildirim gönderilmesin diye
                                        //Bildirim Gönderilmesi
                                        Bildirim bildirim = new Bildirim(a.getId(), "kombin_favori", " senin bir paylaşımını favorilere ekledi.");
                                        BaslaActivity.vb.getDbref("Bildirimler")
                                                .child(a.getKimden())
                                                .push()
                                                .setValue(bildirim);
                                        //Bildirim Gönderilmesi
                                    }
                                    //Veritabanında kisinin listesine eklenmesi
                                    BaslaActivity.vb.getDbref("FavoriListesi")
                                            .child(BaslaActivity.vb.getUserID())
                                            .child(a.getId())
                                            .setValue(a.getKimden());
                                    //Veritabanında kisinin listesine eklenmesi
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        //aktif kullanici o kombini favorilemişmi kontrolü

        //begenisayisi & favorisayisi
        BaslaActivity.vb.getDbref("Begeniler")
                .child("Kombin_Beğenileri")
                .child(a.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Long sayi = dataSnapshot.getChildrenCount();
                        String begenisayisi = sayi.toString();
                        if (begenisayisi.equals("0")) {
                            holder.begenisayisi.setText("");
                        } else {
                            holder.begenisayisi.setText(begenisayisi);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        BaslaActivity.vb.getDbref("Favoriler")
                .child(a.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Long sayie = dataSnapshot.getChildrenCount();
                        String favorisayisi = sayie.toString();
                        if (favorisayisi.equals("0")) {
                            holder.favorisayisi.setText("");
                        } else {
                            holder.favorisayisi.setText(favorisayisi);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        BaslaActivity.vb.getDbref("Yorumlar")
                .child("Kombin_Yorumlari")
                .child(a.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Long sayiec = dataSnapshot.getChildrenCount();
                        String yorumsays = sayiec.toString();
                        if (yorumsays.equals("0")) {
                            holder.yorumsayisi.setText("");
                        } else {
                            holder.yorumsayisi.setText(yorumsays);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        //begenisayisi & favorisayisi & yorumsayisi

        //kombinin silinmesi
        holder.sil.setOnClickListener(new View.OnClickListener() {
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
                        favoriIDs = new ArrayList<>();//favoriIDs listesi tuşa tıklanılınca bağlanır

                        //Önce beğenileri silinir
                        BaslaActivity.vb.getDbref("Begeniler")
                                .child("Kombin_Beğenileri")
                                .child(a.getId())
                                .removeValue();

                        //Sonra yorumlari silinir
                        BaslaActivity.vb.getDbref("Yorumlar")
                                .child("Kombin_Yorumlari")
                                .child(a.getId())
                                .removeValue();

                        //ardından bu kombini favorileyenlerin özel listesinden silinir
                        BaslaActivity.vb.getDbref("Favoriler")
                                .child(a.getId())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                String id = Objects.requireNonNull(snapshot.child("favorileyen").getValue()).toString();
                                                favoriIDs.add(id);
                                            }
                                            for (int i = 0; i < favoriIDs.size(); i++) {
                                                BaslaActivity.vb.getDbref("FavoriListesi")
                                                        .child(favoriIDs.get(i))
                                                        .child(a.getId())
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
                                .child(a.getId())
                                .removeValue();

                        //ardından kombin silinir
                        BaslaActivity.vb.getDbref("Kombinler")
                                .child(BaslaActivity.vb.getUserID())
                                .orderByChild("id").equalTo(a.getId())
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
                                .child(a.getId())
                                .delete();

                        if (MyDialog.isShowing()) {
                            MyDialog.cancel();
                        }

                    }
                });


            }
        });
        //kombinin silinmesi

        BaslaActivity.vb.getDbref("Kullanicilar")
                .child(a.getKimden())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String profilresmi = Objects.requireNonNull(dataSnapshot.child("profilresmi").getValue()).toString();
                        String kullaniciadi = Objects.requireNonNull(dataSnapshot.child("kullaniciadi").getValue()).toString();

                        if (profilresmi.equals("default")) {
                            holder.kisininresmi.setImageResource(R.drawable.ic_person_black_24dp);
                        } else {
                            Picasso.get()
                                    .load(profilresmi)
                                    .resize(holder.kisininresmi.getWidth(), holder.kisininresmi.getHeight())
                                    .into(holder.kisininresmi);
                        }
                        holder.kisininadi.setText(kullaniciadi);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        if (a.getAciklama().equals("")) {
            holder.aciklama.setVisibility(View.GONE);
        } else {
            holder.aciklama.setVisibility(View.VISIBLE);
            holder.aciklama.setText(a.getAciklama());
        }

        if (a.getTarz().equals("Bir giyim tarzı seç...")) {
            holder.kombintarzi.setVisibility(View.GONE);
        } else {
            holder.kombintarzi.setVisibility(View.VISIBLE);
            holder.kombintarzi.setText(a.getTarz());
        }

        if (a.getMagaza() != null) {
            holder.magazasayisi.setVisibility(View.VISIBLE);
        } else {
            holder.magazasayisi.setVisibility(View.GONE);
        }

        Picasso.get().load(a.getResim()).into(holder.kombinresmi, new Callback() {
            @Override
            public void onSuccess() {
                holder.yukleniyor.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {

            }
        });

        GetTimeAgo getTimeAgo = new GetTimeAgo();
        final String trh = getTimeAgo.getTimeAgo(a.getTarih());
        holder.tarih.setText(trh);

        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                c = new PopupMenu(v.getContext(), holder.menu);
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
                                        Rapor rapor = new Rapor(a.getKimden(), a.getId(), raporlamasebebi);
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
                            }
                            case R.id.galeriye_kaydet: {
                                try {
                                    GaleriyeKaydet(holder.kombinresmi);
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

        holder.yorum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(activity, KombinActivity.class);
                go.putExtra("kombin_id", a.getId());
                go.putExtra("kombin_kimden", a.getKimden());
                go.putExtra("kombin_aciklama", a.getAciklama());
                go.putExtra("kombin_tarz", a.getTarz());
                go.putExtra("kombin_resmi", a.getResim());
                go.putExtra("kombin_tarih", trh);
                activity.startActivity(go);
                activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        if (!a.getKimden().equals(BaslaActivity.vb.getUserID())) {
            if (!this.activity.getClass().equals(UsersProfileActivity.class)) {
                holder.kisininresmi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent go = new Intent(activity, UsersProfileActivity.class);
                        go.putExtra("user_id", a.getKimden());
                        activity.startActivity(go);
                        activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    }
                });

                holder.kisininadi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent go = new Intent(activity, UsersProfileActivity.class);
                        go.putExtra("user_id", a.getKimden());
                        activity.startActivity(go);
                        activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    }
                });
            }
        }

        holder.paylas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaylasDialog(a);
            }
        });
    }

    @Override
    public int getItemCount() {
        return kombinler.size();
    }

    class KombinlerViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView kisininresmi;
        private ImageView kombinresmi;
        private TextView kisininadi, kombintarzi, begenisayisi, aciklama, tarih, favorisayisi, yorumsayisi, magazasayisi;
        private ImageButton menu, begen, sil, favori, yorum, paylas;
        private AVLoadingIndicatorView yukleniyor;

        private KombinlerViewHolder(@NonNull View itemView) {

            super(itemView);

            kisininresmi = itemView.findViewById(R.id.single_kombin_kisi_resim);
            kombinresmi = itemView.findViewById(R.id.single_kombin_resmi);
            kisininadi = itemView.findViewById(R.id.single_kombin_isim);
            kombintarzi = itemView.findViewById(R.id.single_kombin_tarz);
            begenisayisi = itemView.findViewById(R.id.single_kombin_begeni_sayisi);
            aciklama = itemView.findViewById(R.id.single_kombin_aciklama);
            menu = itemView.findViewById(R.id.single_kombin_menu);
            begen = itemView.findViewById(R.id.single_kombin_begen);
            sil = itemView.findViewById(R.id.single_kombin_sil);
            tarih = itemView.findViewById(R.id.single_kombin_tarih);
            favori = itemView.findViewById(R.id.single_kombin_favori);
            favorisayisi = itemView.findViewById(R.id.single_kombin_favori_sayisi);
            yorum = itemView.findViewById(R.id.single_kombin_yorum);
            yorumsayisi = itemView.findViewById(R.id.single_kombin_yorum_sayisi);
            magazasayisi = itemView.findViewById(R.id.single_kombin_magaza_sayisi);
            yukleniyor = itemView.findViewById(R.id.single_kombin_yukleniyor);
            paylas = itemView.findViewById(R.id.single_kombin_paylas);
        }
    }

}

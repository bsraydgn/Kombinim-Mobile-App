package com.example.kombinim.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfilDuzenlemeActivity extends AppCompatActivity {

    private EditText kullaniciadi, durum, hakkimda;
    private Button kaydet;
    private CircleImageView profilresmi;
    private Switch profilgorunurlugu;
    private TextView acikkapali;
    private ProgressDialog progress, resimyukluyor;
    private static int PERMISSION_STORAGE_CODE = 87;

    private static int GALLERY_PICK = 1;

    private void init() {
        progress = new ProgressDialog(ProfilDuzenlemeActivity.this);
        progress.setTitle("Profil Yükleniyor");
        progress.setMessage("Profilinizin yüklenmesi zaman alabilir.");
        progress.setCanceledOnTouchOutside(false);
        progress.show();

        Toolbar actionbar = findViewById(R.id.profilduzenlemeactionbar);
        setSupportActionBar(actionbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Profil Ayarları");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        kullaniciadi = findViewById(R.id.profilduzenlemekullaniciadim);
        durum = findViewById(R.id.profilduzenlemedurum);
        hakkimda = findViewById(R.id.profilduzenlemehakkimda);
        profilresmi = findViewById(R.id.profilduzenlemeresmim);
        kaydet = findViewById(R.id.profilduzenlemekaydet);
        profilgorunurlugu = findViewById(R.id.profilduzenlemegizlihesapmi);
        acikkapali = findViewById(R.id.profilduzenlemegizliacikkapali);
    }

    private void outit() {
        resimyukluyor = new ProgressDialog(ProfilDuzenlemeActivity.this);
        resimyukluyor.setTitle("Resim Yükleniyor");
        resimyukluyor.setMessage("Profil resminizin yüklenmesi zaman alabilir.");
        resimyukluyor.setCanceledOnTouchOutside(false);
        resimyukluyor.show();
    }

    private void bilgileriCek() {
        BaslaActivity.vb.getDbref("Kullanicilar").child(BaslaActivity.vb.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String sKullaniciadi = Objects.requireNonNull(dataSnapshot.child("kullaniciadi").getValue()).toString();
                        String sDurum = Objects.requireNonNull(dataSnapshot.child("durum").getValue()).toString();
                        String sHakkimda = Objects.requireNonNull(dataSnapshot.child("hakkimda").getValue()).toString();
                        final String sProfilresmi = Objects.requireNonNull(dataSnapshot.child("profilresmi").getValue()).toString();
                        Boolean sProfilgorunurlugu = (Boolean) dataSnapshot.child("profilgorunurlugu").getValue();

                        kullaniciadi.setText(sKullaniciadi);
                        durum.setText(sDurum);
                        hakkimda.setText(sHakkimda);

                        if (!sProfilresmi.equals("default")) {
                            Picasso.get().load(sProfilresmi).networkPolicy(NetworkPolicy.OFFLINE)
                                    .resize(profilresmi.getWidth(), profilresmi.getHeight())
                                    .into(profilresmi, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get().load(sProfilresmi).resize(profilresmi.getWidth(), profilresmi.getHeight()).into(profilresmi);
                                        }
                                    });
                        }

                        if (Objects.requireNonNull(sProfilgorunurlugu)) {
                            profilgorunurlugu.setChecked(true);
                            String hrkacik = "Herkese Açık";
                            acikkapali.setText(hrkacik);
                            acikkapali.setTextColor(getResources().getColor(R.color.yesil));
                        } else {
                            String sdcark = "Sadece Arkadaşlar";
                            profilgorunurlugu.setChecked(false);
                            acikkapali.setText(sdcark);
                            acikkapali.setTextColor(getResources().getColor(R.color.beyaz));
                        }

                        if (progress != null && progress.isShowing()) {
                            progress.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        if (progress != null && progress.isShowing()) {
                            progress.dismiss();
                        }
                    }
                });
    }

    private void profilresmiYukle() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            //galeriden resim seçim activitysine yönlendirir
            Intent galeriIntent = new Intent();
            galeriIntent.setType("image/*");
            galeriIntent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(galeriIntent, "Bir fotoğraf seçin..."), GALLERY_PICK);
            //galeriden resim seçim activitysine yönlendirir
        } else {
            //dosya izni runtime permission
            requestStoragePermission();
        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("İzin gerekli !")
                    .setMessage("Bu izin cihazınızdaki resimleri seçmeniz için gereklidir.")
                    .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ProfilDuzenlemeActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
                        }
                    }).setNegativeButton("Kapat", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setIcon(R.drawable.ic_folder).create().show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            Uri imageURL = null;
            if (data != null) {
                imageURL = data.getData();
            }

            if (imageURL != null) {
                CropImage.activity(imageURL)
                        .setActivityTitle("Resmi Kırpın")
                        .setAspectRatio(1, 1)
                        .start(this);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                outit();

                Uri uri = null;
                if (result != null) {
                    uri = result.getUri();
                }

                if (uri != null) {
                    BaslaActivity.vb.getFbstorage().child("profil_resimleri/")
                            .child(BaslaActivity.vb.getUserID() + ".jpg")
                            .putFile(uri)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Objects.requireNonNull(task.getResult()).getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                String downloadURL = uri.toString();
                                                BaslaActivity.vb.getDbref("Kullanicilar")
                                                        .child(BaslaActivity.vb.getUserID())
                                                        .child("profilresmi")
                                                        .setValue(downloadURL).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            if (resimyukluyor != null && resimyukluyor.isShowing()) {
                                                                resimyukluyor.dismiss();
                                                            }
                                                            Toast.makeText(ProfilDuzenlemeActivity.this, "Profil Resmi Güncellendi", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        if (resimyukluyor != null && resimyukluyor.isShowing()) {
                                            resimyukluyor.dismiss();
                                        }
                                        Toast.makeText(ProfilDuzenlemeActivity.this, "Profil Resminiz Yüklenemedi", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                if (result != null) {
                    Exception error = result.getError();
                    Log.i("Hata: ", error.toString());
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bilgileriCek();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                this.onBackPressed();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_duzenleme);

        init();

        profilgorunurlugu.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String hrkacik = "Herkese Açık";
                    acikkapali.setText(hrkacik);
                    acikkapali.setTextColor(getResources().getColor(R.color.yesil));
                } else {
                    String sdcark = "Sadece Arkadaşlar";
                    acikkapali.setText(sdcark);
                    acikkapali.setTextColor(getResources().getColor(R.color.beyaz));
                }
            }
        });

        profilresmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profilresmiYukle();
            }
        });

        kaydet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUsername = kullaniciadi.getText().toString();
                String newUserStatus = durum.getText().toString();
                String newHakkimda = hakkimda.getText().toString();
                boolean newProfilgorunurluk;
                newProfilgorunurluk = profilgorunurlugu.isChecked();

                BaslaActivity.vb.getDbref("Kullanicilar")
                        .child(BaslaActivity.vb.getUserID())
                        .child("kullaniciadi")
                        .setValue(newUsername)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfilDuzenlemeActivity.this, "Kullanıcı adınız güncellenemedi !", Toast.LENGTH_LONG).show();
                            }
                        });
                BaslaActivity.vb.getDbref("Kullanicilar")
                        .child(BaslaActivity.vb.getUserID())
                        .child("durum")
                        .setValue(newUserStatus)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfilDuzenlemeActivity.this, "Durumunuz güncellenemedi !", Toast.LENGTH_LONG).show();
                            }
                        });
                BaslaActivity.vb.getDbref("Kullanicilar")
                        .child(BaslaActivity.vb.getUserID())
                        .child("hakkimda")
                        .setValue(newHakkimda)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfilDuzenlemeActivity.this, "Hakkımda güncellenemedi !", Toast.LENGTH_LONG).show();
                            }
                        });

                BaslaActivity.vb.getDbref("Kullanicilar")
                        .child(BaslaActivity.vb.getUserID())
                        .child("profilgorunurlugu")
                        .setValue(newProfilgorunurluk)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ProfilDuzenlemeActivity.this, "Profil görünürlüğü güncellenemedi !", Toast.LENGTH_LONG).show();
                            }
                        });

                if (progress != null && progress.isShowing()) {
                    progress.dismiss();
                }
                Intent goUserSetting = new Intent(ProfilDuzenlemeActivity.this, BaslaActivity.class);
                startActivity(goUserSetting);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                finish();
            }
        });

    }
}

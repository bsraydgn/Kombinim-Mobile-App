package com.example.kombinim.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Kombin;
import com.example.kombinim.VeriSiniflari.Magaza;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class OlusturFragment extends Fragment {

    private ImageView kombin_resmi;
    private EditText kombin_aciklamasi, magaza_ismi, urun_linki;
    private View rootView;
    private Uri resultUri;
    private Switch tarz_secimi, magaza_secimi;
    private RadioButton gece_hayati, gunluk_giyim, tatil_sahil, mezuniyet_toren;
    private ExpandableLayout tarz_secili_degilse_cikar, magaza_secili_degilse_cikar;
    private RelativeLayout kombin_paylas;
    private AVLoadingIndicatorView progress_paylas;
    private TextView text_paylas;
    private AdView kombinimreklamolustur;

    private static int GALLERY_PICK = 1;
    private static int PERMISSION_STORAGE_CODE = 2;

    private void ReklamlariCek() {
        AdRequest adRequest = new AdRequest.Builder().build();
        kombinimreklamolustur.loadAd(adRequest);
    }

    private void init() {
        kombin_resmi = rootView.findViewById(R.id.kombin_resmi);
        kombin_aciklamasi = rootView.findViewById(R.id.kombin_aciklama);
        kombin_paylas = rootView.findViewById(R.id.kombin_paylas);
        tarz_secimi = rootView.findViewById(R.id.tarz_secimi);
        magaza_secimi = rootView.findViewById(R.id.magaza_secimi);
        gece_hayati = rootView.findViewById(R.id.gece_hayati);
        gunluk_giyim = rootView.findViewById(R.id.gunluk_giyim);
        tatil_sahil = rootView.findViewById(R.id.tatil_sahil);
        mezuniyet_toren = rootView.findViewById(R.id.mezuniyet_toren);
        tarz_secili_degilse_cikar = rootView.findViewById(R.id.tarz_secili_degilse_cikar);
        magaza_secili_degilse_cikar = rootView.findViewById(R.id.magaza_secili_degilse_cikar);
        magaza_ismi = rootView.findViewById(R.id.magaza_ismi);
        urun_linki = rootView.findViewById(R.id.urun_linki);
        text_paylas = rootView.findViewById(R.id.text_paylas);
        progress_paylas = rootView.findViewById(R.id.progress_paylas);
        kombinimreklamolustur = rootView.findViewById(R.id.kombinimreklamolustur);
    }

    private boolean internetBaglantiKontrol() {
        ConnectivityManager baglantiYonetici = (ConnectivityManager) Objects.requireNonNull(getActivity()).getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = baglantiYonetici.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = baglantiYonetici.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (mWifi.isAvailable() && mWifi.isConnected()) || (mMobile.isAvailable() && mMobile.isConnected());
    }

    private void ProgressBaslat() {
        text_paylas.setText("");
        kombin_paylas.setEnabled(false);
        progress_paylas.setVisibility(View.VISIBLE);
        progress_paylas.show();
    }

    private void ProgressBitir() {
        String pyls = "Paylaş";
        text_paylas.setText(pyls);
        kombin_paylas.setEnabled(true);
        progress_paylas.hide();
        progress_paylas.setVisibility(View.GONE);
    }

    private void KontrolvePaylasim() {
        ProgressBaslat();
        final Kombin yeni = new Kombin();
        if (magaza_secimi.isChecked()) {
            if (!TextUtils.isEmpty(magaza_ismi.getText()) && !TextUtils.isEmpty(urun_linki.getText())) {
                Magaza magaza = new Magaza(magaza_ismi.getText().toString(), urun_linki.getText().toString());
                yeni.setMagaza(magaza);
                Paylas(yeni);
            } else {
                ProgressBitir();
                Toast.makeText(getActivity(), "Lütfen mağaza önerilerini eksiksiz doldurunuz !", Toast.LENGTH_SHORT).show();
            }
        } else {
            yeni.setMagaza(null);
            Paylas(yeni);
        }
    }

    private void Paylas(final Kombin yeni) {
        if (internetBaglantiKontrol()) {
            BaslaActivity.vb.getFbstorage().child("Kombinler")
                    .child(BaslaActivity.vb.getUserID())
                    .child(yeni.getId())
                    .putFile(resultUri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                Objects.requireNonNull(task.getResult()).getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String downloadURL = uri.toString();
                                        yeni.setAciklama(kombin_aciklamasi.getText().toString());
                                        yeni.setResim(downloadURL);
                                        if (tarz_secimi.isChecked()) {
                                            if (gunluk_giyim.isChecked()) {
                                                yeni.setTarz(gunluk_giyim.getText().toString());
                                            } else if (gece_hayati.isChecked()) {
                                                yeni.setTarz(gece_hayati.getText().toString());
                                            } else if (tatil_sahil.isChecked()) {
                                                yeni.setTarz(tatil_sahil.getText().toString());
                                            } else if (mezuniyet_toren.isChecked()) {
                                                yeni.setTarz(mezuniyet_toren.getText().toString());
                                            }
                                        } else {
                                            yeni.setTarz("Bir giyim tarzı seç...");
                                        }
                                        BaslaActivity.vb.getDbref("Kombinler")
                                                .child(BaslaActivity.vb.getUserID())
                                                .push()
                                                .setValue(yeni)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            ProgressBitir();
                                                            Toast.makeText(getActivity(), "Kombin Paylaşıldı", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    }
                                });
                            } else {
                                ProgressBitir();
                                Toast.makeText(getActivity(), "Kombin Paylaşılamadı !", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            ProgressBitir();
            Toast.makeText(getActivity(), "İnternet bağlantınızı kontrol edin !", Toast.LENGTH_LONG).show();
        }
    }

    private void ResimSec() {
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()),
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
        if (ActivityCompat.shouldShowRequestPermissionRationale(Objects.requireNonNull(getActivity()),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(getContext())
                    .setTitle("İzin gerekli !")
                    .setMessage("Bu izin cihazınızdaki resimleri seçmeniz için gereklidir.")
                    .setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
                        }
                    }).setNegativeButton("Kapat", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setIcon(R.drawable.ic_folder).create().show();
        } else {
            ActivityCompat.requestPermissions(Objects.requireNonNull(getActivity()),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_STORAGE_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {

            //Uri imageURL = data.getData();
            resultUri = data.getData();
            Picasso.get().load(resultUri).into(kombin_resmi);
            kombin_paylas.setVisibility(View.VISIBLE);

            /*CropImage.activity(imageURL)
                    .setAspectRatio(1, 1)
                    .start(getActivity());*/
        }

        /*if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }*/
    }

    @Override
    public void onStart() {
        super.onStart();
        ReklamlariCek();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_olustur, container, false);

        init();

        kombin_resmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ResimSec();
            }
        });

        kombin_paylas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KontrolvePaylasim();
            }
        });

        tarz_secimi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tarz_secili_degilse_cikar.expand();
                } else {
                    tarz_secili_degilse_cikar.collapse();
                }
            }
        });

        magaza_secimi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    magaza_secili_degilse_cikar.expand();
                } else {
                    magaza_secili_degilse_cikar.collapse();
                }
            }
        });

        gunluk_giyim.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tatil_sahil.setChecked(false);
                    gece_hayati.setChecked(false);
                    mezuniyet_toren.setChecked(false);
                }
            }
        });

        tatil_sahil.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    gunluk_giyim.setChecked(false);
                    gece_hayati.setChecked(false);
                    mezuniyet_toren.setChecked(false);
                }
            }
        });

        gece_hayati.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tatil_sahil.setChecked(false);
                    gunluk_giyim.setChecked(false);
                    mezuniyet_toren.setChecked(false);
                }
            }
        });

        mezuniyet_toren.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tatil_sahil.setChecked(false);
                    gece_hayati.setChecked(false);
                    gunluk_giyim.setChecked(false);
                }
            }
        });

        return rootView;
    }
}

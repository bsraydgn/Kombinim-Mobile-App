package com.example.kombinim.Auth;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Kullanici;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText kullaniciadi, sifre, sifretekrar, email;
    private Button kayitol;
    private ImageButton girisyapregistersayfasi;
    private Dialog progressDialog, sozlesme_dialog;
    private CheckBox kullanici_sozlesme;

    private void DialogBaslat() {
        progressDialog = new Dialog(RegisterActivity.this);
        progressDialog.setContentView(R.layout.indicator_progress);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        AVLoadingIndicatorView anim = progressDialog.findViewById(R.id.progress_login);
        TextView progress_text = progressDialog.findViewById(R.id.progress_text);
        progressDialog.setCanceledOnTouchOutside(false);
        String hspolst = "Hesap Oluşturuluyor...";
        progress_text.setText(hspolst);
        progressDialog.show();
        anim.show();
    }

    private void SozlesmeAc() {
        sozlesme_dialog = new Dialog(RegisterActivity.this);
        sozlesme_dialog.setContentView(R.layout.kullanici_sozlesmesi);
        sozlesme_dialog.setCanceledOnTouchOutside(false);

        Button sozlesme_kapat = sozlesme_dialog.findViewById(R.id.sozlesme_kapat);
        Button sozlesme_kabul = sozlesme_dialog.findViewById(R.id.sozlesme_kabul);

        sozlesme_kabul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kullanici_sozlesme.setChecked(true);
                if (sozlesme_dialog != null && sozlesme_dialog.isShowing()) {
                    sozlesme_dialog.dismiss();
                }
            }
        });

        sozlesme_kapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kullanici_sozlesme.setChecked(false);
                if (sozlesme_dialog != null && sozlesme_dialog.isShowing()) {
                    sozlesme_dialog.dismiss();
                }
            }
        });

        sozlesme_dialog.show();

    }

    public void init() {
        kullaniciadi = findViewById(R.id.txtbx_kullanıcıadi);
        sifre = findViewById(R.id.txtbx_sifre);
        sifretekrar = findViewById(R.id.txtbx_sifretekrar);
        email = findViewById(R.id.txtbx_email);
        kayitol = findViewById(R.id.bttn_kayitol);
        girisyapregistersayfasi = findViewById(R.id.bttn_girisyapregistersayfasi);
        kullanici_sozlesme = findViewById(R.id.kullanici_sozlesmesi);
    }

    private boolean internetBaglantiKontrol() {
        ConnectivityManager baglantiYonetici = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = baglantiYonetici.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = baglantiYonetici.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (mWifi.isAvailable() && mWifi.isConnected()) || (mMobile.isAvailable() && mMobile.isConnected());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        kayitol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yeniHesapOlustur();
            }
        });

        girisyapregistersayfasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kayitol.setEnabled(false);
                girisyapregistersayfasi.setEnabled(false);
                Intent goLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(goLogin);
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                finish();
            }
        });

        kullanici_sozlesme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    SozlesmeAc();
                }
            }
        });

    }

    private void yeniHesapOlustur() {
        String username = kullaniciadi.getText().toString();
        String password = sifre.getText().toString();
        String passwordretry = sifretekrar.getText().toString();
        String epost = email.getText().toString();
        final Kullanici userregister = new Kullanici(epost, username, password);

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Kullanıcı Adı Alanı Boş Bırakılamaz !", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Şifre Alanı Boş Bırakılamaz !", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(epost)) {
            Toast.makeText(this, "E-Mail Alanı Boş Bırakılamaz !", Toast.LENGTH_LONG).show();
        } else {
            if (TextUtils.equals(password, passwordretry)) {
                if (password.length() >= 6) {
                    if (kullanici_sozlesme.isChecked()) {
                        if (internetBaglantiKontrol()) {

                            DialogBaslat();

                            kayitol.setEnabled(false);
                            girisyapregistersayfasi.setEnabled(false);
                            BaslaActivity.vb.getAuth()
                                    .createUserWithEmailAndPassword(epost, password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                String userID = BaslaActivity.vb.getUserID();
                                                userregister.setUserID(userID);
                                                BaslaActivity.vb.getDbref("Kullanicilar/" + userID)
                                                        .setValue(userregister)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(RegisterActivity.this, "Hoşgeldin " + userregister.getKullaniciadi(), Toast.LENGTH_SHORT).show();
                                                                Intent loginIntent = new Intent(RegisterActivity.this, BaslaActivity.class);
                                                                startActivity(loginIntent);
                                                                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                                                finish();

                                                                if (progressDialog != null && progressDialog.isShowing()) {
                                                                    progressDialog.dismiss();
                                                                }

                                                            }
                                                        });
                                            } else {

                                                if (progressDialog != null && progressDialog.isShowing()) {
                                                    progressDialog.dismiss();
                                                }

                                                Toast.makeText(RegisterActivity.this, "Hesap Oluşturulamadı ! E-Mailinizi Kontrol Edin !", Toast.LENGTH_LONG).show();
                                                kayitol.setEnabled(true);
                                                girisyapregistersayfasi.setEnabled(true);
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterActivity.this, "İnternet bağlantını kontrol et !", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(this, "Kullanıcı sözleşmesini kabul etmelisiniz !", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, "Şifreniz 6 Haneden Kısa Olamaz !", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Şifrenizi Doğru Tekrar Ettiğinizden Emin Olunuz !", Toast.LENGTH_LONG).show();
            }
        }

    }

}

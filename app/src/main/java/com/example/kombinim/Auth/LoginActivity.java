package com.example.kombinim.Auth;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private EditText email2, sifre2;
    private Button girisyap;
    private Dialog progressDialog;
    private TextView kayitolloginsayfasi;
    private ImageButton gotoyoutube, gototwitter, gotoinstagram;

    private boolean internetBaglantiKontrol() {
        ConnectivityManager baglantiYonetici = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = baglantiYonetici.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = baglantiYonetici.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (mWifi.isAvailable() && mWifi.isConnected()) || (mMobile.isAvailable() && mMobile.isConnected());
    }

    private void DialogBaslat() {
        progressDialog = new Dialog(LoginActivity.this);
        progressDialog.setContentView(R.layout.indicator_progress);
        Objects.requireNonNull(progressDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        AVLoadingIndicatorView anim = progressDialog.findViewById(R.id.progress_login);
        TextView progress_text = progressDialog.findViewById(R.id.progress_text);
        progressDialog.setCanceledOnTouchOutside(false);
        String girisypl = "Giriş Yapılıyor...";
        progress_text.setText(girisypl);
        progressDialog.show();
        anim.show();
    }

    public void init() {
        email2 = findViewById(R.id.txtbx_email2);
        sifre2 = findViewById(R.id.txtbx_sifre2);
        girisyap =  findViewById(R.id.bttn_girisyap);
        kayitolloginsayfasi = findViewById(R.id.bttn_kayitolloginsayfasi);
        gotoyoutube = findViewById(R.id.gotoyoutube);
        gototwitter = findViewById(R.id.gototwitter);
        gotoinstagram = findViewById(R.id.gotoinstagram);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        girisyap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                girisYapFonk();
            }
        });

        gotoyoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://www.youtube.com/channel/UCb-GXd3B67b0OceHlnbFUMA/featured?view_as=subscriber";
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setPackage("com.google.android.youtube");
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            }
        });

        gototwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "https://twitter.com/brkdnc1";
                try {
                    Intent likeIng = new Intent(Intent.ACTION_VIEW);
                    likeIng.setPackage("com.twitter.android");
                    likeIng.setData(Uri.parse(uri));
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    Intent likeIng = new Intent(Intent.ACTION_VIEW);
                    likeIng.setData(Uri.parse(uri));
                    startActivity(likeIng);
                }
            }
        });

        gotoinstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "https://www.instagram.com/brkdnc.1/";
                try {
                    Intent likeIng = new Intent(Intent.ACTION_VIEW);
                    likeIng.setPackage("com.instagram.android");
                    likeIng.setData(Uri.parse(uri));
                    startActivity(likeIng);
                } catch (ActivityNotFoundException e) {
                    Intent likeIng = new Intent(Intent.ACTION_VIEW);
                    likeIng.setData(Uri.parse(uri));
                    startActivity(likeIng);
                }
            }
        });

        kayitolloginsayfasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                girisyap.setEnabled(false);
                kayitolloginsayfasi.setEnabled(false);
                Intent goRegister = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(goRegister);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                finish();
            }
        });
    }

    private void girisYapFonk() {
        String epost2 = email2.getText().toString();
        String password2 = sifre2.getText().toString();

        if (TextUtils.isEmpty(epost2)) {
            Toast.makeText(this, "E-Mail Alanı Boş Bırakılamaz !", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password2)) {
            Toast.makeText(this, "Şifre Alanı Boş Bırakılamaz !", Toast.LENGTH_LONG).show();
        } else {
            if (internetBaglantiKontrol()) {
                DialogBaslat();
                girisyap.setEnabled(false);
                kayitolloginsayfasi.setEnabled(false);
                BaslaActivity.vb.getAuth().signInWithEmailAndPassword(epost2, password2)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent baslaIntent = new Intent(LoginActivity.this, BaslaActivity.class);
                                    startActivity(baslaIntent);
                                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                    finish();

                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }

                                } else {
                                    if (progressDialog != null && progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }

                                    Toast.makeText(LoginActivity.this, "Kullanıcı Adını veya Şifreni Kontrol Et !", Toast.LENGTH_LONG).show();
                                    girisyap.setEnabled(true);
                                    kayitolloginsayfasi.setEnabled(true);
                                }
                            }
                        });
            } else {
                Toast.makeText(this, "İnternet bağlantınızı kontrol edin !", Toast.LENGTH_SHORT).show();
            }
        }
    }

}

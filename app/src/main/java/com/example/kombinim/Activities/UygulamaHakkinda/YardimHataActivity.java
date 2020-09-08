package com.example.kombinim.Activities.UygulamaHakkinda;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.GeriBildirim;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.Objects;

public class YardimHataActivity extends AppCompatActivity {

    private EditText bildirim, eposta;
    private RadioButton yardim, hata, oneri;
    private RelativeLayout gonder;
    private AVLoadingIndicatorView yardimindicator;
    private TextView yardimpaylastext;

    private void init() {
        Toolbar actionbaryardim = findViewById(R.id.actionbaryardim);
        bildirim = findViewById(R.id.yardimbildirim);
        eposta = findViewById(R.id.yardimeposta);
        yardim = findViewById(R.id.yardimradioyardim);
        hata = findViewById(R.id.yardimradiohata);
        oneri = findViewById(R.id.yardimradiooneri);
        gonder = findViewById(R.id.yardimgonder);
        yardimindicator = findViewById(R.id.progress_paylas_yardim);
        yardimpaylastext = findViewById(R.id.text_paylas_yardim);

        setSupportActionBar(actionbaryardim);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Geri Bildirim");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private boolean internetBaglantiKontrol() {
        ConnectivityManager baglantiYonetici = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = baglantiYonetici.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = baglantiYonetici.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return (mWifi.isAvailable() && mWifi.isConnected()) || (mMobile.isAvailable() && mMobile.isConnected());
    }

    private void ProgressBaslat(){
        yardimpaylastext.setText("");
        gonder.setEnabled(false);
        yardimindicator.setVisibility(View.VISIBLE);
        yardimindicator.show();
    }

    private void ProgressBitir(){
        String strgonder = "Gönder";
        yardimpaylastext.setText(strgonder);
        gonder.setEnabled(true);
        yardimindicator.hide();
        yardimindicator.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yardim_hata);

        init();

        gonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(bildirim.getText())
                        && !TextUtils.isEmpty(eposta.getText())
                        && (yardim.isChecked() || hata.isChecked() || oneri.isChecked())) {

                    if (internetBaglantiKontrol()) {
                        
                        ProgressBaslat();

                        String geribildirim = bildirim.getText().toString();
                        String epostayardim = eposta.getText().toString();
                        String yardimtur;

                        if (yardim.isChecked()) {
                            yardimtur = yardim.getText().toString();
                        } else if (hata.isChecked()) {
                            yardimtur = hata.getText().toString();
                        } else {
                            yardimtur = oneri.getText().toString();
                        }

                        GeriBildirim geriBildirim = new GeriBildirim(geribildirim, epostayardim, yardimtur);

                        BaslaActivity.vb.getDbref("GeriBildirim")
                                .child(BaslaActivity.vb.getUserID())
                                .push()
                                .setValue(geriBildirim)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        ProgressBitir();
                                        Toast.makeText(YardimHataActivity.this, "Geri bildiriminiz alındı. Teşekkürler.", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                ProgressBitir();
                                Toast.makeText(YardimHataActivity.this, "Geri bildiriminiz gönderilemiyor. Lütfen tekrar deneyiniz.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else{
                        Toast.makeText(YardimHataActivity.this, "İnternet bağlantınızı kontrol edin !", Toast.LENGTH_SHORT).show();    
                    }

                } else {
                    Toast.makeText(YardimHataActivity.this, "Lütfen tüm alanları doldurunuz !", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }
}

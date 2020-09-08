package com.example.kombinim.Activities.UygulamaHakkinda;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.kombinim.BuildConfig;
import com.example.kombinim.R;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.Objects;

public class UygulamaHakkindaActivity extends AppCompatActivity {

    private Button lisansbuton, guncellemebuton;
    private ExpandableLayout expandableLayoutlisans, expandableLayoutguncelleme;
    private TextView versiyon;
    private CardView twitter, facebook, youtube, instagram;

    private void init(){
        Toolbar uygulamahakkindatoolbar = findViewById(R.id.uygulamahakkindatoolbar);
        lisansbuton = findViewById(R.id.lisans_buton);
        expandableLayoutlisans=findViewById(R.id.expandable_layout_lisans);
        guncellemebuton=findViewById(R.id.guncelleme_buton);
        expandableLayoutguncelleme=findViewById(R.id.expandable_layout_guncelleme);
        versiyon=findViewById(R.id.versiyon);
        twitter=findViewById(R.id.hakkindatwittercard);
        facebook=findViewById(R.id.hakkindafacebookcard);
        youtube=findViewById(R.id.hakkindayoutubecard);
        instagram=findViewById(R.id.hakkindainstagramcard);

        setSupportActionBar(uygulamahakkindatoolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Uygulama Hakkında");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void VersiyonCek(){
        String versionName = BuildConfig.VERSION_NAME;
        versiyon.setText(versionName);
    }

    @Override
    protected void onStart() {
        super.onStart();
        VersiyonCek();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uygulama_hakkinda);

        init();

        lisansbuton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expandableLayoutlisans.isExpanded()){
                    expandableLayoutlisans.collapse();
                    lisansbuton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_asagi,0,0,0);
                }else{
                    expandableLayoutlisans.expand();
                    lisansbuton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_yukari,0,0,0);
                }
            }
        });

        guncellemebuton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expandableLayoutguncelleme.isExpanded()){
                    expandableLayoutguncelleme.collapse();
                    guncellemebuton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_asagi,0,0,0);
                }else{
                    expandableLayoutguncelleme.expand();
                    guncellemebuton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_yukari,0,0,0);
                }
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
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

        youtube.setOnClickListener(new View.OnClickListener() {
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

        instagram.setOnClickListener(new View.OnClickListener() {
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

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //facebook sayfasına yönlendirecek
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

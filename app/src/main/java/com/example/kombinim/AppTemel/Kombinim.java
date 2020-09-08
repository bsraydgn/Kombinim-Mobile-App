package com.example.kombinim.AppTemel;

import android.app.Application;

import com.example.kombinim.BaslaActivity;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class Kombinim extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        BaslaActivity.vb.getDb().setPersistenceEnabled(true);

        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built=builder.build();

        //indirmenin ağdanmı önbellektenmi çekildiğine dair renkler ile ipucu verir
        built.setIndicatorsEnabled(false);
        //indirmenin ağdanmı önbellektenmi çekildiğine dair renkler ile ipucu verir

        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}

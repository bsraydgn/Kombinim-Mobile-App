package com.example.kombinim;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;

import com.example.kombinim.AppTemel.MyFirebaseMessagingService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import com.example.kombinim.Activities.SohbetlerActivity;
import com.example.kombinim.Activities.UygulamaHakkinda.UygulamaHakkindaActivity;
import com.example.kombinim.Activities.UygulamaHakkinda.YardimHataActivity;
import com.example.kombinim.Auth.LoginActivity;
import com.example.kombinim.Fragments.AnaSayfaFragment;
import com.example.kombinim.Fragments.BildirimlerFragment;
import com.example.kombinim.Fragments.KesfetFragment;
import com.example.kombinim.Fragments.OlusturFragment;
import com.example.kombinim.Fragments.ProfilimFragment;
import com.example.kombinim.VeriTabani.VeriTabani;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Objects;

public class BaslaActivity extends AppCompatActivity {

    public static VeriTabani vb = VeriTabani.getVeritabani();
    private ImageButton mesajlarim, uygulama_hakkinda;
    private Fragment anasayfa, kesfet, olustur, bildirim, profilim;
    private PopupMenu a;
    private MyFirebaseMessagingService myFirebaseMessagingService;

    private void init(){
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mesajlarim=findViewById(R.id.mesajlarim);
        uygulama_hakkinda=findViewById(R.id.uygulama_hakkinda);

        OlusumTasarlayici();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,anasayfa).commit();
    }

    private void OlusumTasarlayici(){
        anasayfa=new AnaSayfaFragment();
        kesfet=new KesfetFragment();
        olustur=new OlusturFragment();
        bildirim=new BildirimlerFragment();
        profilim=new ProfilimFragment();
    }

    private void jetonKontrol(){
        myFirebaseMessagingService=new MyFirebaseMessagingService();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new Instance ID token
                        String token = Objects.requireNonNull(task.getResult()).getToken();
                        myFirebaseMessagingService.onNewToken(token);
                    }
                });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment=null;

            switch (item.getItemId()) {
                case R.id.navigation_anasayfa:{
                    if(anasayfa!=null){
                        selectedFragment=anasayfa;
                    }else{
                        selectedFragment=new AnaSayfaFragment();
                    }
                    break;
                }
                case R.id.navigation_kesfet:{
                    if(kesfet!=null){
                        selectedFragment=kesfet;
                    }else{
                        selectedFragment=new KesfetFragment();
                    }
                    break;
                }
                case R.id.navigation_arkadaslar:{
                    if(bildirim!=null){
                        selectedFragment=bildirim;
                    }else{
                        selectedFragment=new BildirimlerFragment();
                    }
                    break;
                }
                case R.id.navigation_profilim:{
                    if(profilim!=null){
                        selectedFragment=profilim;
                    }else{
                        selectedFragment=new ProfilimFragment();
                    }
                    break;
                }
                case R.id.navigation_olustur:{
                    if(olustur!=null){
                        selectedFragment=olustur;
                    }else{
                        selectedFragment=new OlusturFragment();
                    }
                    break;
                }
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, Objects.requireNonNull(selectedFragment))
                    .commit();
            return true;
        }
    };

    private void StateControle(){
        if(vb.getCurrentuser() == null){
            Intent goWelcome=new Intent(BaslaActivity.this, LoginActivity.class);
            startActivity(goWelcome);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basla);

        StateControle();

        init();

        MobileAds.initialize(this);

        //jetonKontrol();

        uygulama_hakkinda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                a=new PopupMenu(BaslaActivity.this,uygulama_hakkinda);
                a.inflate(R.menu.uygulama_hakkinda_menu);
                a.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.yardim_hata_bildir:{
                                Intent go=new Intent(BaslaActivity.this, YardimHataActivity.class);
                                startActivity(go);
                                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                                break;
                            }case R.id.uygulama_hakkinda_button:{
                                Intent go=new Intent(BaslaActivity.this, UygulamaHakkindaActivity.class);
                                startActivity(go);
                                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
                                break;
                            }
                        }
                        return false;
                    }
                });
                a.show();
            }
        });

        mesajlarim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go=new Intent(BaslaActivity.this, SohbetlerActivity.class);
                startActivity(go);
                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });
    }

}

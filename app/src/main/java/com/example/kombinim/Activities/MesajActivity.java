package com.example.kombinim.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kombinim.Adapters.MesajAdapter;
import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Mesaj;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.skhugh.simplepulltorefresh.PullToRefreshLayout;
import com.skhugh.simplepulltorefresh.PullToRefreshListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MesajActivity extends AppCompatActivity {

    private String chat_user_id;
    private CircleImageView chat_user_pp;
    private TextView chat_user_username;
    private ImageButton chat_menu, mesaj_gonder_btn;
    private PullToRefreshLayout mesaj_refresh;
    private RecyclerView mesajrecycview;
    private ImageButton mesaj_back;
    private LinearLayout mesajetkilesimleri;
    private EditText mesaj_yazma_edit;
    private ProgressDialog dialog;
    private static final int TOPLAM_GOSTERILECEK = 15;
    private int mevcutsayfa = 1;
    private PopupMenu popupMenu;
    private Query query;
    private ValueEventListener ab;

    private List<Mesaj> mesajlar = new ArrayList<>();
    private MesajAdapter adapter;

    private void ProgresBaslat() {
        dialog = new ProgressDialog(MesajActivity.this);
        dialog.setTitle("Mesajlar Yükleniyor");
        dialog.setMessage("Bu biraz zaman alabilir.");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void init() {
        ProgresBaslat();

        chat_user_pp = findViewById(R.id.mesajtoolbarresim);
        chat_user_username = findViewById(R.id.mesajtoolbarismi);
        chat_menu = findViewById(R.id.mesajmenu);
        mesajetkilesimleri = findViewById(R.id.mesajetkilesimleri);
        mesaj_gonder_btn = findViewById(R.id.mesajgonderımgbtn);
        mesaj_yazma_edit = findViewById(R.id.mesajyazmaedit);
        mesaj_refresh = findViewById(R.id.mesaj_refresh);
        mesajrecycview = findViewById(R.id.mesajrecycview);
        mesaj_back = findViewById(R.id.mesaj_back);

        popupMenu = new PopupMenu(this, chat_menu);
        popupMenu.inflate(R.menu.mesajmenu);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mesajrecycview.setHasFixedSize(true);

        chat_user_id = getIntent().getStringExtra("user_id");

        ArkadaslikKontrolEt();

        adapter = new MesajAdapter(mesajlar,this);
        if (mesajrecycview != null) {
            mesajrecycview.setAdapter(adapter);
            mesajrecycview.setLayoutManager(linearLayoutManager);
        }

        //goruldu yapmak için gereksinimler
        query = BaslaActivity.vb.getDbref("Mesajlar")
                .child(chat_user_id)
                .child(BaslaActivity.vb.getUserID());

        ab = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Mesaj mesaj = snapshot.getValue(Mesaj.class);
                        if (mesaj != null && mesaj.getKimden().equals(chat_user_id)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("gorulme", true);
                            snapshot.getRef().updateChildren(hashMap);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        //goruldu yapmak için gereksinimler
    }

    private void ArkadaslikKontrolEt() {
        BaslaActivity.vb.getDbref("Arkadaslar")
                .child(BaslaActivity.vb.getUserID())
                .child(chat_user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mesajetkilesimleri.setVisibility(View.VISIBLE);
                } else {
                    mesajetkilesimleri.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void MesajlariGetir() {
        BaslaActivity.vb.getDbref("Mesajlar")
                .child(BaslaActivity.vb.getUserID()).child(chat_user_id)
                .limitToLast(mevcutsayfa * TOPLAM_GOSTERILECEK)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mesajlar.clear();
                        if (dataSnapshot.exists()) {
                            mesajrecycview.setVisibility(View.VISIBLE);
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Mesaj m = snapshot.getValue(Mesaj.class);
                                mesajlar.add(m);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            adapter.notifyDataSetChanged();
                            mesajrecycview.getRecycledViewPool().clear();
                            mesajrecycview.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void BilgileriEkranaBas() {
        BaslaActivity.vb.getDbref("Kullanicilar")
                .child(chat_user_id)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String isimci = Objects.requireNonNull(dataSnapshot.child("kullaniciadi").getValue()).toString();
                        chat_user_username.setText(isimci);
                        String resimci = Objects.requireNonNull(dataSnapshot.child("profilresmi").getValue()).toString();
                        if (resimci.equals("default")) {
                            chat_user_pp.setImageResource(R.drawable.ic_person_black_24dp);
                        } else {
                            Picasso.get().load(resimci).into(chat_user_pp);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SendMessage() {
        if (!TextUtils.isEmpty(mesaj_yazma_edit.getText().toString())) {
            final String text = mesaj_yazma_edit.getText().toString();
            final Mesaj yenimesaj = new Mesaj(text,"mesaj");
            BaslaActivity.vb.getDbref("Mesajlar")
                    .child(BaslaActivity.vb.getUserID())
                    .child(chat_user_id)
                    .push()
                    .setValue(yenimesaj).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    BaslaActivity.vb.getDbref("Mesajlar")
                            .child(chat_user_id)
                            .child(BaslaActivity.vb.getUserID())
                            .push()
                            .setValue(yenimesaj).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            mesaj_gonder_btn.setEnabled(true);
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mesaj_yazma_edit.setText(null);
                            mesaj_gonder_btn.setEnabled(true);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mesaj_gonder_btn.setEnabled(true);
                }
            });
        } else {
            mesaj_gonder_btn.setEnabled(true);
        }

    }

    private void Sohbetitemizle() {
        BaslaActivity.vb.getDbref("Mesajlar")
                .child(BaslaActivity.vb.getUserID())
                .child(chat_user_id)
                .removeValue()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MesajActivity.this, "Sohbet temizlenemiyor. Tekrar dene.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void GorulduYap() {
        query.addValueEventListener(ab);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        query.removeEventListener(ab);
        this.finish();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    protected void onStart() {
        super.onStart();
        BilgileriEkranaBas();
        GorulduYap();
        MesajlariGetir();
        if (dialog.isShowing()) {
            dialog.cancel();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mesaj);

        init();

        mesaj_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mesaj_refresh.setPullToRefreshListener(new PullToRefreshListener() {
            @Override
            public void onStartRefresh(@Nullable View view) {
                mevcutsayfa++;
                MesajlariGetir();
                mesaj_refresh.refreshDone();
            }
        });

        mesaj_gonder_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mesaj_gonder_btn.setEnabled(false);
                SendMessage();
            }
        });

        chat_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.kisiyigoruntule: {
                        Intent go = new Intent(MesajActivity.this, UsersProfileActivity.class);
                        go.putExtra("user_id", chat_user_id);
                        startActivity(go);
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        break;
                    }
                    case R.id.sohbetitemizle: {
                        Sohbetitemizle();
                        break;
                    }
                }
                return false;
            }
        });

    }
}

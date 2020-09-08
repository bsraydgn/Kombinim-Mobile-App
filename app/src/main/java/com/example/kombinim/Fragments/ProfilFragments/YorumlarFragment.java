package com.example.kombinim.Fragments.ProfilFragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kombinim.Adapters.YorumAdapter;
import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Bildirim;
import com.example.kombinim.VeriSiniflari.Yorum;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class YorumlarFragment extends Fragment {

    public static String profil_sahibi;
    private EditText yorumyazmaedit;
    private ImageButton yorumgonder;
    private LinearLayout yorumetkilesimleri;
    private TextView henuzyorumyok;
    private YorumAdapter adapter;
    private List<Yorum> yorumlar = new ArrayList<>();
    private ProgressDialog progress;
    private View rootView;

    private void ArkadaslikKontrolu() {
        BaslaActivity.vb.getDbref("Arkadaslar")
                .child(BaslaActivity.vb.getUserID())
                .child(profil_sahibi)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            yorumetkilesimleri.setVisibility(View.VISIBLE);
                        } else {
                            yorumetkilesimleri.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SendYorum() {
        if (!TextUtils.isEmpty(yorumyazmaedit.getText().toString())) {
            final String text = yorumyazmaedit.getText().toString();
            final Yorum yeniyorum = new Yorum(text, BaslaActivity.vb.getUserID());
            BaslaActivity.vb.getDbref("Yorumlar")
                    .child("Profil_Yorumlari")
                    .child(profil_sahibi)
                    .push()
                    .setValue(yeniyorum)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            yorumgonder.setEnabled(true);
                            Toast.makeText(getActivity(), "Yorum gönderilemiyor !", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    yorumyazmaedit.setText(null);
                    yorumgonder.setEnabled(true);
                    Bildirim bildirim=new Bildirim(yeniyorum.getId(),"profile_yorum"," profiline bir yorum bıraktı.");
                    BaslaActivity.vb.getDbref("Bildirimler")
                            .child(profil_sahibi)
                            .push()
                            .setValue(bildirim);
                }
            });
        } else {
            yorumgonder.setEnabled(true);
        }
    }

    private void YorumlariCek() {
        BaslaActivity.vb.getDbref("Yorumlar")
                .child("Profil_Yorumlari")
                .child(profil_sahibi)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        yorumlar.clear();
                        if (dataSnapshot.exists()) {
                            henuzyorumyok.setVisibility(View.GONE);
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Yorum a = snapshot.getValue(Yorum.class);
                                yorumlar.add(a);
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            henuzyorumyok.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        if(progress!=null && progress.isShowing()){
            progress.dismiss();
        }
    }

    private void ProgressBaslat(){
        progress = new ProgressDialog(getActivity());
        progress.setTitle("Yorumlar Yükleniyor");
        progress.setMessage("Yorumlarınızın yüklenmesi zaman alabilir.");
        progress.setCanceledOnTouchOutside(false);
        progress.show();
    }

    private void init(){
        profil_sahibi = Objects.requireNonNull(getArguments()).getString("user_id");

        RecyclerView yorumrecycview = rootView.findViewById(R.id.yorumrecycview);
        yorumyazmaedit = rootView.findViewById(R.id.yorumyazmaedit);
        yorumgonder = rootView.findViewById(R.id.yorumgonderımgbtn);
        yorumetkilesimleri = rootView.findViewById(R.id.yorumetkilesimleri);
        henuzyorumyok = rootView.findViewById(R.id.henuzyorumyok);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        yorumrecycview.setHasFixedSize(true);
        yorumrecycview.setLayoutManager(linearLayoutManager);

        adapter = new YorumAdapter(yorumlar, getContext());
        yorumrecycview.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        ArkadaslikKontrolu();
        YorumlariCek();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_yorumlar, container, false);

        ProgressBaslat();

        init();

        yorumgonder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                yorumgonder.setEnabled(false);
                SendYorum();
            }
        });

        return rootView;
    }
}

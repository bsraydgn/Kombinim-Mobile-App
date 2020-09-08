package com.example.kombinim.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kombinim.Activities.MesajActivity;
import com.example.kombinim.Activities.UsersProfileActivity;
import com.example.kombinim.AppTemel.GetTimeAgo;
import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Kullanici;
import com.example.kombinim.VeriSiniflari.Mesaj;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SohbetlerAdapter extends RecyclerView.Adapter<SohbetlerAdapter.SohbetlerViewHolder> {

    private List<Kullanici> mesajlar;
    private Context context;
    private Activity activity;

    public SohbetlerAdapter(List<Kullanici> mesajlar, Context context, Activity activity) {
        this.mesajlar = mesajlar;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public SohbetlerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_sohbetler_layout, parent, false);
        return new SohbetlerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final SohbetlerViewHolder holder, final int position) {

        final Kullanici e = mesajlar.get(position);

        BaslaActivity.vb.getDbref("Mesajlar")
                .child(BaslaActivity.vb.getUserID())
                .child(e.getUserID())
                .limitToLast(1)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String atama = Objects.requireNonNull(dataSnapshot.child("mesaj").getValue()).toString();
                        long tarih = Long.parseLong(Objects.requireNonNull(dataSnapshot.child("tarih").getValue()).toString());
                        GetTimeAgo getTimeAgo = new GetTimeAgo();
                        String duzenleme = getTimeAgo.getTimeAgo(tarih);
                        String kimdense = Objects.requireNonNull(dataSnapshot.child("kimden").getValue()).toString();
                        String type = Objects.requireNonNull(dataSnapshot.child("type").getValue()).toString();

                        holder.tarihi.setText(duzenleme);

                        if (kimdense.equals(BaslaActivity.vb.getUserID())) {
                            if(type.equals("mesaj")){
                                String atm = "Siz: " + atama;
                                holder.sonmesaj.setText(atm);
                                holder.sonmesaj.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                            }else if(type.equals("kombin")){
                                String atm="Bir kombin gönderdiniz.";
                                holder.sonmesaj.setText(atm);
                                holder.sonmesaj.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_kombin_paylasilmis,0,0,0);
                            }
                        } else {
                            if(type.equals("mesaj")){
                                holder.sonmesaj.setText(atama);
                                holder.sonmesaj.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                            }else if(type.equals("kombin")){
                                String atm="Bir kombin gönderdi.";
                                holder.sonmesaj.setText(atm);
                                holder.sonmesaj.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_kombin_paylasilmis,0,0,0);
                            }
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        BaslaActivity.vb.getDbref("Mesajlar")
                .child(e.getUserID())
                .child(BaslaActivity.vb.getUserID())
                .orderByChild("kimden").equalTo(e.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        long sayi = 0;
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Mesaj mesaj = snapshot.getValue(Mesaj.class);
                                if (mesaj != null && !mesaj.getGorulme()) {
                                    sayi++;
                                }
                            }
                            if (sayi == 0) {
                                holder.gorulmeyen.setVisibility(View.GONE);
                            } else {
                                holder.gorulmeyen.setVisibility(View.VISIBLE);
                                String sayisi = String.valueOf(sayi);
                                holder.gorulmeyen.setText(sayisi);
                            }
                        } else {
                            holder.gorulmeyen.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        holder.kullanicadi.setText(e.getKullaniciadi());

        if (e.getProfilresmi().equals("default")) {
            holder.profilresmi.setImageResource(R.drawable.ic_person_black_24dp);
        } else {
            Picasso.get().load(e.getProfilresmi()).into(holder.profilresmi);
        }

        holder.profilresmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FotoDialog(e.getProfilresmi());
            }
        });

        holder.geridonexpanded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.expandableLayoutSohbet.isExpanded()) {
                    holder.expandableLayoutSohbet.collapse();
                }
            }
        });

        holder.profilegitexpanded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(activity, UsersProfileActivity.class);
                go.putExtra("user_id", e.getUserID());
                activity.startActivity(go);
                activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        holder.sohbettemizleexpanded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaslaActivity.vb.getDbref("Mesajlar")
                        .child(BaslaActivity.vb.getUserID())
                        .child(e.getUserID())
                        .removeValue()
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity, "Sohbet temizlenemiyor. Tekrar dene.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        holder.sohbetlercarview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go=new Intent(activity, MesajActivity.class);
                go.putExtra("user_id",e.getUserID());
                activity.startActivity(go);
                activity.overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });

        holder.sohbetlercarview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!holder.expandableLayoutSohbet.isExpanded()) {
                    holder.expandableLayoutSohbet.expand();
                }
                return true;
            }
        });

    }

    private void FotoDialog(String url) {
        final Dialog MyDialog = new Dialog(context);
        MyDialog.setContentView(R.layout.profil_foto_dialog);
        Objects.requireNonNull(MyDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        MyDialog.setCanceledOnTouchOutside(false);
        MyDialog.show();

        Button profilfotokapat = MyDialog.findViewById(R.id.profil_foto_kapat);
        ImageView profilfoto = MyDialog.findViewById(R.id.profil_foto_view);

        if (url.equals("default")) {
            profilfoto.setImageResource(R.drawable.ic_person_black_24dp);
        } else {
            Picasso.get().load(url).into(profilfoto);
        }

        profilfotokapat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyDialog.isShowing()) {
                    MyDialog.cancel();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mesajlar.size();
    }

    class SohbetlerViewHolder extends RecyclerView.ViewHolder  {
        CircleImageView profilresmi;
        TextView kullanicadi, sonmesaj, tarihi, gorulmeyen;
        ExpandableLayout expandableLayoutSohbet;
        Button sohbettemizleexpanded, profilegitexpanded, geridonexpanded;
        CardView sohbetlercarview ;

        SohbetlerViewHolder(@NonNull View itemView) {
            super(itemView);
            profilresmi = itemView.findViewById(R.id.msjcycimage);
            kullanicadi = itemView.findViewById(R.id.msjcycusername);
            sonmesaj = itemView.findViewById(R.id.msjcycsonmesaj);
            tarihi = itemView.findViewById(R.id.msjcyctarih);
            gorulmeyen = itemView.findViewById(R.id.msjcycgorulmeyen);
            expandableLayoutSohbet = itemView.findViewById(R.id.expandable_sohbetler);
            sohbettemizleexpanded = itemView.findViewById(R.id.expanded_sohbet_sohbetitemizle);
            profilegitexpanded = itemView.findViewById(R.id.expanded_sohbet_profilegit);
            geridonexpanded = itemView.findViewById(R.id.expanded_sohbet_geridon);
            sohbetlercarview=itemView.findViewById(R.id.sohbetlercardview);

        }
    }
}

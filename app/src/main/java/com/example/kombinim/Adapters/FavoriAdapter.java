package com.example.kombinim.Adapters;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kombinim.Activities.KombinActivity;
import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Bildirim;
import com.example.kombinim.VeriSiniflari.Favori;
import com.example.kombinim.VeriSiniflari.Kombin;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class FavoriAdapter extends RecyclerView.Adapter<FavoriAdapter.FavorilerViewHolder> {

    private List<Kombin> kombinler;
    private Activity activity;

    public FavoriAdapter(List<Kombin> kombinler, Activity activity) {
        this.kombinler = kombinler;
        this.activity = activity;
    }

    @NonNull
    @Override
    public FavorilerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_favoriler_layout, parent, false);
        return new FavorilerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final FavorilerViewHolder holder, int position) {
        final Kombin a = kombinler.get(position);

        //aktif kullanici o kombini favorilemişmi kontrolü
        BaslaActivity.vb.getDbref("Favoriler")
                .child(a.getId())
                .orderByChild("favorileyen")
                .equalTo(BaslaActivity.vb.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            holder.favori.setImageResource(R.drawable.ic_bookmark_okay);
                            holder.favori.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        snapshot.getRef().removeValue();
                                    }
                                    BaslaActivity.vb.getDbref("FavoriListesi")
                                            .child(BaslaActivity.vb.getUserID())
                                            .child(a.getId()).removeValue();
                                }
                            });
                        } else {
                            holder.favori.setImageResource(R.drawable.ic_bookmark);
                            holder.favori.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
                                    Favori yenifavori = new Favori(BaslaActivity.vb.getUserID(), currentDate);
                                    //Kombinin Favorilenmesi
                                    BaslaActivity.vb.getDbref("Favoriler")
                                            .child(a.getId())
                                            .push()
                                            .setValue(yenifavori);
                                    //Kombinin Favorilenmesi
                                    if (!a.getKimden().equals(BaslaActivity.vb.getUserID())) { //kendine bildirim gönderilmesin diye
                                        //Bildirim Gönderilmesi
                                        Bildirim bildirim = new Bildirim(a.getId(), "kombin_favori", " senin bir paylaşımını favorilere ekledi.");
                                        BaslaActivity.vb.getDbref("Bildirimler")
                                                .child(a.getKimden())
                                                .push()
                                                .setValue(bildirim);
                                        //Bildirim Gönderilmesi
                                    }
                                    //Veritabanında kisinin listesine eklenmesi
                                    BaslaActivity.vb.getDbref("FavoriListesi")
                                            .child(BaslaActivity.vb.getUserID())
                                            .child(a.getId())
                                            .setValue(a.getKimden());
                                    //Veritabanında kisinin listesine eklenmesi
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        //aktif kullanici o kombini favorilemişmi kontrolü

        BaslaActivity.vb.getDbref("Kullanicilar")
                .child(a.getKimden())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String kullaniciadi = Objects.requireNonNull(dataSnapshot.child("kullaniciadi").getValue()).toString();
                        holder.kisininadi.setText(kullaniciadi);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        Picasso.get().load(a.getResim()).resize(holder.kombinresmi.getWidth(),250).centerCrop().into(holder.kombinresmi, new Callback() {
            @Override
            public void onSuccess() {
                holder.yukleniyor.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {

            }
        });

        holder.git.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent go = new Intent(activity,KombinActivity.class);
                go.putExtra("kombin_kimden", a.getKimden());
                go.putExtra("kombin_id", a.getId());
                activity.startActivity(go);
                activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return kombinler.size();
    }

    class FavorilerViewHolder extends RecyclerView.ViewHolder {

        private ImageView kombinresmi;
        private TextView kisininadi;
        private ImageButton git, favori;
        private AVLoadingIndicatorView yukleniyor;

        private FavorilerViewHolder(@NonNull View itemView) {

            super(itemView);

            git = itemView.findViewById(R.id.single_favori_git);
            kombinresmi = itemView.findViewById(R.id.single_favori_resim);
            kisininadi = itemView.findViewById(R.id.single_favori_kisiadi);
            favori = itemView.findViewById(R.id.single_favori_favori);
            yukleniyor=itemView.findViewById(R.id.single_favori_yukleniyor);
        }
    }

}

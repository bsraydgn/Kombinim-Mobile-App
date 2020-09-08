package com.example.kombinim.Adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kombinim.AppTemel.GetTimeAgo;
import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Mesaj;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MesajAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Mesaj> mesajlist;
    private static int GIDEN_MESAJ = 1;
    private static int GIDEN_KOMBIN = 2;
    private static int GIDEN_RESIM = 3;
    private static int GELEN_MESAJ = 4;
    private static int GELEN_KOMBIN = 5;
    private static int GELEN_RESIM = 6;
    private Context context;

    public MesajAdapter(List<Mesaj> mesajlist, Context context) {
        this.mesajlist = mesajlist;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view;
        if (viewType == GIDEN_MESAJ) {
            view = LayoutInflater.from(context).inflate(R.layout.single_giden_mesaj_layout, viewGroup, false);
            return new GidenMesajViewHolder(view);
        } else if (viewType == GELEN_MESAJ) {
            view = LayoutInflater.from(context).inflate(R.layout.single_mesaj_layout, viewGroup, false);
            return new GelenMesajViewHolder(view);
        } else if (viewType == GIDEN_KOMBIN) {
            view = LayoutInflater.from(context).inflate(R.layout.single_mesaj_giden_kombin_layout, viewGroup, false);
            return new GidenKombinViewHolder(view);
        } else if (viewType == GELEN_KOMBIN) {
            view = LayoutInflater.from(context).inflate(R.layout.single_mesaj_gelen_kombin_layout, viewGroup, false);
            return new GelenKombinViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == GIDEN_MESAJ) {
            ((GidenMesajViewHolder) holder).setGidenMesajAyarla(mesajlist.get(position));
        } else if (getItemViewType(position) == GELEN_MESAJ) {
            ((GelenMesajViewHolder) holder).setGelenMesajAyarla(mesajlist.get(position));
        } else if (getItemViewType(position) == GIDEN_KOMBIN) {
            ((GidenKombinViewHolder) holder).setGidenKombinAyarla(mesajlist.get(position));
        } else if (getItemViewType(position) == GELEN_KOMBIN) {
            ((GelenKombinViewHolder) holder).setGelenKombinAyarla(mesajlist.get(position));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mesajlist.get(position).getKimden().equals(BaslaActivity.vb.getUserID())
                && mesajlist.get(position).getType().equals("mesaj")) {
            return GIDEN_MESAJ;
        } else if (mesajlist.get(position).getKimden().equals(BaslaActivity.vb.getUserID())
                && mesajlist.get(position).getType().equals("kombin")) {
            return GIDEN_KOMBIN;
        } else if (mesajlist.get(position).getKimden().equals(BaslaActivity.vb.getUserID())
                && mesajlist.get(position).getType().equals("resim")) {
            return GIDEN_RESIM;
        } else if (!mesajlist.get(position).getKimden().equals(BaslaActivity.vb.getUserID())
                && mesajlist.get(position).getType().equals("mesaj")) {
            return GELEN_MESAJ;
        } else if (!mesajlist.get(position).getKimden().equals(BaslaActivity.vb.getUserID())
                && mesajlist.get(position).getType().equals("kombin")) {
            return GELEN_KOMBIN;
        } else if (!mesajlist.get(position).getKimden().equals(BaslaActivity.vb.getUserID())
                && mesajlist.get(position).getType().equals("resim")) {
            return GELEN_RESIM;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return mesajlist.size();
    }

    private class GelenMesajViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView msjimage;
        private TextView msjtext, msjtarih;

        private GelenMesajViewHolder(@NonNull View itemView) {
            super(itemView);
            msjimage = itemView.findViewById(R.id.msjimage);
            msjtext = itemView.findViewById(R.id.msjtext);
            msjtarih = itemView.findViewById(R.id.msjtarih);
        }

        private void setGelenMesajAyarla(Mesaj mesaj) {
            msjtext.setText(mesaj.getMesaj());
            GetTimeAgo getTimeAgo = new GetTimeAgo();
            msjtarih.setText(getTimeAgo.getTimeAgo(mesaj.getTarih()));
            BaslaActivity.vb.getDbref("Kullanicilar")
                    .child(mesaj.getKimden())
                    .child("profilresmi")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String profresmi = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                            if (profresmi.equals("default")) {
                                msjimage.setImageResource(R.drawable.ic_person_black_24dp);
                            } else {
                                Picasso.get().load(profresmi).into(msjimage);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    private class GidenMesajViewHolder extends RecyclerView.ViewHolder {
        private TextView msjtext, msjtarih;

        private GidenMesajViewHolder(@NonNull View itemView) {
            super(itemView);
            msjtext = itemView.findViewById(R.id.msjtext1);
            msjtarih = itemView.findViewById(R.id.msjtarih1);
        }

        private void setGidenMesajAyarla(Mesaj mesaj) {
            msjtext.setText(mesaj.getMesaj());
            GetTimeAgo getTimeAgo = new GetTimeAgo();
            msjtarih.setText(getTimeAgo.getTimeAgo(mesaj.getTarih()));
            if (mesaj.getGorulme()) {
                msjtext.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_okunma_mavi, 0);
            } else {
                msjtext.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_okunma, 0);
            }
        }
    }

    private class GelenKombinViewHolder extends RecyclerView.ViewHolder {
        private ImageView kombinresmi;
        private TextView msjtarih, kombinaciklama, kombinsahibininismi;
        private CircleImageView kombinsahibiresmi, gelenkombingndrnimage;
        private RelativeLayout gizliliksaglayicisi;
        private boolean erisebilirmi;

        private GelenKombinViewHolder(@NonNull View itemView) {
            super(itemView);
            kombinresmi = itemView.findViewById(R.id.gelen_kombin_paylasim);
            msjtarih = itemView.findViewById(R.id.msjtarih4);
            kombinaciklama = itemView.findViewById(R.id.gelenkombinaciklama);
            kombinsahibininismi = itemView.findViewById(R.id.gelenkombinsahibiismi);
            kombinsahibiresmi = itemView.findViewById(R.id.gelenkombinsahibiresmi);
            gizliliksaglayicisi = itemView.findViewById(R.id.gizliliksaglayicisi4);
            gelenkombingndrnimage=itemView.findViewById(R.id.gelen_kombin_gndrnimage);
        }

        private boolean arkadasVeAcikmi(final String kombinsahibiid) {
            if (kombinsahibiid.equals(BaslaActivity.vb.getUserID())) {
                erisebilirmi = true;
            } else {
                BaslaActivity.vb.getDbref("Kullanicilar")
                        .child(kombinsahibiid)
                        .child("profilgorunurlugu")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshotz) {
                                if (Objects.equals(dataSnapshotz.getValue(), true)) {
                                    erisebilirmi = true;
                                } else {
                                    BaslaActivity.vb
                                            .getDbref("Arkadaslar")
                                            .child(BaslaActivity.vb.getUserID())
                                            .child(kombinsahibiid)
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    erisebilirmi = dataSnapshot.exists();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
            return erisebilirmi;
        }

        private void setGelenKombinAyarla(Mesaj mesaj) {
            GetTimeAgo getTimeAgo = new GetTimeAgo();
            msjtarih.setText(getTimeAgo.getTimeAgo(mesaj.getTarih()));

            final String[] ayarlama = mesaj.getMesaj().split(" "); // 0. indeks userid, 1. indeks kombin id
            BaslaActivity.vb.getDbref("Kombinler")
                    .child(ayarlama[0])
                    .orderByChild("id")
                    .equalTo(ayarlama[1])
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (arkadasVeAcikmi(ayarlama[0])) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        gizliliksaglayicisi.setVisibility(View.VISIBLE);
                                        String kmbnaciklama = Objects.requireNonNull(snapshot.child("aciklama").getValue()).toString();
                                        String kmbnresmi = Objects.requireNonNull(snapshot.child("resim").getValue()).toString();
                                        kombinaciklama.setText(kmbnaciklama);
                                        Picasso.get().load(kmbnresmi).into(kombinresmi);
                                    }
                                } else {
                                    gizliliksaglayicisi.setVisibility(View.GONE);
                                    kombinaciklama.setText("İçerik Görüntülenemiyor !\nBu gönderinin sahibi gizli bir hesaba sahip. Gönderilerini görmek için arkadaş eklemelisin.");
                                }
                            } else {
                                gizliliksaglayicisi.setVisibility(View.GONE);
                                kombinaciklama.setText("Gönderiye Ulaşılamıyor !\nSilindiği için bu gönderiye ulaşılamıyor.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            BaslaActivity.vb.getDbref("Kullanicilar")
                    .child(ayarlama[0])
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String isim = Objects.requireNonNull(dataSnapshot.child("kullaniciadi").getValue()).toString();
                            String profilresmi = Objects.requireNonNull(dataSnapshot.child("profilresmi").getValue()).toString();
                            kombinsahibininismi.setText(isim);
                            if (profilresmi.equals("default")) {
                                kombinsahibiresmi.setImageResource(R.drawable.ic_person_black_24dp);
                            } else {
                                Picasso.get().load(profilresmi).into(kombinsahibiresmi);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            BaslaActivity.vb.getDbref("Kullanicilar")
                    .child(mesaj.getKimden())
                    .child("profilresmi")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String profresmi = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                            if (profresmi.equals("default")) {
                                gelenkombingndrnimage.setImageResource(R.drawable.ic_person_black_24dp);
                            } else {
                                Picasso.get().load(profresmi).into(gelenkombingndrnimage);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

    private class GidenKombinViewHolder extends RecyclerView.ViewHolder {
        private ImageView kombinresmi;
        private TextView msjtarih, kombinaciklama, kombinsahibininismi;
        private CircleImageView kombinsahibiresmi;
        private RelativeLayout gizliliksaglayicisi;
        private boolean erisebilirmi;

        private GidenKombinViewHolder(@NonNull View itemView) {
            super(itemView);
            kombinresmi = itemView.findViewById(R.id.giden_kombin_paylasim);
            msjtarih = itemView.findViewById(R.id.msjtarih3);
            kombinaciklama = itemView.findViewById(R.id.gidenkombinaciklama);
            kombinsahibininismi = itemView.findViewById(R.id.gidenkombinsahibiismi);
            kombinsahibiresmi = itemView.findViewById(R.id.gidenkombinsahibiresmi);
            gizliliksaglayicisi = itemView.findViewById(R.id.gizliliksaglayicisi);
        }

        private boolean arkadasVeAcikmi(final String kombinsahibiid) {
            if (kombinsahibiid.equals(BaslaActivity.vb.getUserID())) {
                erisebilirmi = true;
            } else {
                BaslaActivity.vb.getDbref("Kullanicilar")
                        .child(kombinsahibiid)
                        .child("profilgorunurlugu")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshotz) {
                                if (Objects.equals(dataSnapshotz.getValue(), true)) {
                                    erisebilirmi = true;
                                } else {
                                    BaslaActivity.vb
                                            .getDbref("Arkadaslar")
                                            .child(BaslaActivity.vb.getUserID())
                                            .child(kombinsahibiid)
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    erisebilirmi = dataSnapshot.exists();
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
            return erisebilirmi;
        }

        private void setGidenKombinAyarla(Mesaj mesaj) {
            GetTimeAgo getTimeAgo = new GetTimeAgo();
            msjtarih.setText(getTimeAgo.getTimeAgo(mesaj.getTarih()));

            if (mesaj.getGorulme()) {
                msjtarih.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_okunma_mavi, 0);
            } else {
                msjtarih.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_okunma, 0);
            }

            final String[] ayarlama = mesaj.getMesaj().split(" "); // 0. indeks userid, 1. indeks kombin id
            BaslaActivity.vb.getDbref("Kombinler")
                    .child(ayarlama[0])
                    .orderByChild("id")
                    .equalTo(ayarlama[1])
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                if (arkadasVeAcikmi(ayarlama[0])) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        gizliliksaglayicisi.setVisibility(View.VISIBLE);
                                        String kmbnaciklama = Objects.requireNonNull(snapshot.child("aciklama").getValue()).toString();
                                        String kmbnresmi = Objects.requireNonNull(snapshot.child("resim").getValue()).toString();
                                        kombinaciklama.setText(kmbnaciklama);
                                        Picasso.get().load(kmbnresmi).into(kombinresmi);
                                    }
                                } else {
                                    gizliliksaglayicisi.setVisibility(View.GONE);
                                    kombinaciklama.setText("İçerik Görüntülenemiyor !\nBu gönderinin sahibi gizli bir hesaba sahip. Gönderilerini görmek için arkadaş eklemelisin.");
                                }
                            } else {
                                gizliliksaglayicisi.setVisibility(View.GONE);
                                kombinaciklama.setText("Gönderiye Ulaşılamıyor !\nSilindiği için bu gönderiye ulaşılamıyor.");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

            BaslaActivity.vb.getDbref("Kullanicilar")
                    .child(ayarlama[0])
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String isim = Objects.requireNonNull(dataSnapshot.child("kullaniciadi").getValue()).toString();
                            String profilresmi = Objects.requireNonNull(dataSnapshot.child("profilresmi").getValue()).toString();
                            kombinsahibininismi.setText(isim);
                            if (profilresmi.equals("default")) {
                                kombinsahibiresmi.setImageResource(R.drawable.ic_person_black_24dp);
                            } else {
                                Picasso.get().load(profilresmi).into(kombinsahibiresmi);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
        }
    }

}

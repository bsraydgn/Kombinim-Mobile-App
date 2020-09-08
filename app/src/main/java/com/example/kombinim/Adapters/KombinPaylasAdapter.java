package com.example.kombinim.Adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Kombin;
import com.example.kombinim.VeriSiniflari.Kullanici;
import com.example.kombinim.VeriSiniflari.Mesaj;
import com.google.android.gms.tasks.OnSuccessListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class KombinPaylasAdapter extends RecyclerView.Adapter<KombinPaylasAdapter.KombinPaylasViewHolder> {

    private List<Kullanici> arkadaslar;
    private Kombin gonderilecekKombin;

    public KombinPaylasAdapter(List<Kullanici> arkadaslar, Kombin gonderilecekKombin) {
        this.arkadaslar = arkadaslar;
        this.gonderilecekKombin = gonderilecekKombin;
    }

    @NonNull
    @Override
    public KombinPaylasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.kmbn_pyls_kisiler_single, parent, false);
        return new KombinPaylasViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final KombinPaylasViewHolder holder, int position) {

        final Kullanici a = arkadaslar.get(position);

        holder.kp_username.setText(a.getKullaniciadi());

        if (a.getProfilresmi().equals("default")) {
            holder.kp_image.setImageResource(R.drawable.ic_person_black_24dp);
        } else {
            Picasso.get().load(a.getProfilresmi()).into(holder.kp_image);
        }

        holder.kp_gonder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String text = gonderilecekKombin.getKimden() + " " + gonderilecekKombin.getId();
                    final Mesaj yenimesaj = new Mesaj(text, "kombin");
                    BaslaActivity.vb.getDbref("Mesajlar")
                            .child(BaslaActivity.vb.getUserID())
                            .child(a.getUserID())
                            .push()
                            .setValue(yenimesaj).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            BaslaActivity.vb.getDbref("Mesajlar")
                                    .child(a.getUserID())
                                    .child(BaslaActivity.vb.getUserID())
                                    .push()
                                    .setValue(yenimesaj)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            holder.kp_gonder.setEnabled(false);
                                        }
                                    });
                        }
                    });


                }
            }
        });

        //devam edilecek
    }

    @Override
    public int getItemCount() {
        return arkadaslar.size();
    }

    class KombinPaylasViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView kp_image;
        private TextView kp_username;
        private ToggleButton kp_gonder;

        private KombinPaylasViewHolder(@NonNull View itemView) {
            super(itemView);
            kp_gonder = itemView.findViewById(R.id.kp_gonder);
            kp_image = itemView.findViewById(R.id.kp_image);
            kp_username = itemView.findViewById(R.id.kp_username);
        }
    }
}

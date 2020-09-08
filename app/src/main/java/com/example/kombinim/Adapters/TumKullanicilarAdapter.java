package com.example.kombinim.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kombinim.Activities.UsersProfileActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Kullanici;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class TumKullanicilarAdapter extends RecyclerView.Adapter<TumKullanicilarAdapter.TumKullanicilarViewHolder> {

    private List<Kullanici> kullanicilar;
    private Context tiklama_icin;
    private Activity activity;

    private void FotoDialog(String url, Context context) {
        final Dialog MyDialog = new Dialog(context);
        MyDialog.setContentView(R.layout.profil_foto_dialog);
        MyDialog.setCanceledOnTouchOutside(false);
        Objects.requireNonNull(MyDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
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

    public TumKullanicilarAdapter(List<Kullanici> kullanicilar, Context tiklama_icin, Activity activity) {
        this.kullanicilar = kullanicilar;
        this.tiklama_icin = tiklama_icin;
        this.activity=activity;
    }

    @NonNull
    @Override
    public TumKullanicilarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);
        return new TumKullanicilarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TumKullanicilarViewHolder holder, final int position) {
        final Kullanici a = kullanicilar.get(position);
        holder.kullanicadi.setText(a.getKullaniciadi());
        holder.durumu.setText(a.getDurum());
        if (a.getProfilresmi().equals("default")) {
            holder.profilresmi.setImageResource(R.drawable.ic_person_black_24dp);
        } else {
            Picasso.get().load(a.getProfilresmi()).into(holder.profilresmi);
        }

        holder.profilresmi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FotoDialog(a.getProfilresmi(), tiklama_icin);
            }
        });

        holder.tumkullanicilarview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goprofile=new Intent(activity, UsersProfileActivity.class);
                goprofile.putExtra("user_id",a.getUserID());
                activity.startActivity(goprofile);
                activity.overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return kullanicilar.size();
    }

    class TumKullanicilarViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profilresmi;
        private TextView kullanicadi, durumu;
        private CardView tumkullanicilarview;

        private TumKullanicilarViewHolder(@NonNull View itemView) {
            super(itemView);
            profilresmi = itemView.findViewById(R.id.cycimage);
            kullanicadi = itemView.findViewById(R.id.cycusername);
            durumu = itemView.findViewById(R.id.cycstatus);
            tumkullanicilarview=itemView.findViewById(R.id.tumkullanicilarcardview);
        }
    }

}

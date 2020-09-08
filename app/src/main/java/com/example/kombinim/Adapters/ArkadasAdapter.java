package com.example.kombinim.Adapters;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.kombinim.Activities.MesajActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Kullanici;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ArkadasAdapter extends RecyclerView.Adapter<ArkadasAdapter.ArkadaslarViewHolder> {

    private List<Kullanici> arkadaslar;
    private Activity activity;

    public ArkadasAdapter(List<Kullanici> arkadaslar, Activity activity) {
        this.arkadaslar = arkadaslar;
        this.activity=activity;
    }

    @NonNull
    @Override
    public ArkadaslarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_arkadas_layout,parent,false);
        return new ArkadaslarViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ArkadaslarViewHolder holder, final int position) {
        final Kullanici a=arkadaslar.get(position);

        String resmi=a.getProfilresmi();
        String ismi=a.getKullaniciadi();

        holder.kullanicadi.setText(ismi);

        if(resmi.equals("default")){
            holder.profilresmi.setImageResource(R.drawable.ic_person_black_24dp);
        }else{
            Picasso.get().load(resmi).into(holder.profilresmi);
        }

        holder.arkadaslarcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go=new Intent(activity, MesajActivity.class);
                go.putExtra("user_id",a.getUserID());
                activity.startActivity(go);
                activity.overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arkadaslar.size();
    }

    class ArkadaslarViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profilresmi;
        private TextView kullanicadi;
        private CardView arkadaslarcardview;

        private ArkadaslarViewHolder(@NonNull View itemView) {
            super(itemView);
            profilresmi=itemView.findViewById(R.id.single_arkadas_resim);
            kullanicadi=itemView.findViewById(R.id.single_arkadas_isim);
            arkadaslarcardview=itemView.findViewById(R.id.arkadaslarcardview);

        }
    }

}

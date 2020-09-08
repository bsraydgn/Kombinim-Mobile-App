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

import com.example.kombinim.Activities.UsersProfileActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Kullanici;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BegenenAdapter extends RecyclerView.Adapter<BegenenAdapter.BegenenViewHolder> {

    private List<Kullanici> begenenler;
    private Activity activity;

    public BegenenAdapter(List<Kullanici> begenenler, Activity activity) {
        this.begenenler = begenenler;
        this.activity = activity;
    }

    @NonNull
    @Override
    public BegenenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_begenen_layout, parent, false);
        return new BegenenViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull BegenenViewHolder holder, int position) {
        final Kullanici a = begenenler.get(position);

        String resmi = a.getProfilresmi();
        String ismi = a.getKullaniciadi();

        holder.kullanicadi.setText(ismi);

        if (resmi.equals("default")) {
            holder.profilresmi.setImageResource(R.drawable.ic_person_black_24dp);
        } else {
            Picasso.get().load(resmi).into(holder.profilresmi);
        }

        holder.begenencardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(activity, UsersProfileActivity.class);
                go.putExtra("user_id", a.getUserID());
                activity.startActivity(go);
                activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return begenenler.size();
    }

    class BegenenViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profilresmi;
        private TextView kullanicadi;
        private CardView begenencardview;

        private BegenenViewHolder(@NonNull View itemView) {
            super(itemView);
            profilresmi = itemView.findViewById(R.id.begenen_resmi);
            kullanicadi = itemView.findViewById(R.id.begenen_ismi);
            begenencardview = itemView.findViewById(R.id.begenencardview);

        }
    }

}

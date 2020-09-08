package com.example.kombinim.Adapters;

import android.app.Activity;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kombinim.Activities.KombinActivity;
import com.example.kombinim.Activities.UsersProfileActivity;
import com.example.kombinim.AppTemel.GetTimeAgo;
import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Bildirim;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class BildirimAdapter extends RecyclerView.Adapter<BildirimAdapter.BildirimlerViewHolder> {

    private List<Bildirim> bildirimler;
    private Activity activity;

    public BildirimAdapter(List<Bildirim> bildirimler, Activity activity) {
        this.bildirimler = bildirimler;
        this.activity=activity;
    }

    @NonNull
    @Override
    public BildirimlerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_bildirim_layout, parent, false);
        return new BildirimlerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final BildirimlerViewHolder holder, int position) {
        final Bildirim a = bildirimler.get(position);

        BaslaActivity.vb.getDbref("Kullanicilar")
                .child(a.getKimden())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String adi= Objects.requireNonNull(dataSnapshot.child("kullaniciadi").getValue()).toString();
                        String birlestirme=adi+a.getIcerik();
                        String profresmi= Objects.requireNonNull(dataSnapshot.child("profilresmi").getValue()).toString();
                        holder.bicerik.setText(birlestirme);
                        if(profresmi.equals("default")){
                            holder.bildirimkimdenresmi.setImageResource(R.drawable.ic_person_black_24dp);
                        }else{
                            Picasso.get().load(profresmi).into(holder.bildirimkimdenresmi);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        GetTimeAgo getTimeAgo = new GetTimeAgo();
        String trh = getTimeAgo.getTimeAgo(a.getTarih());
        holder.btarih.setText(trh);

        switch (a.getTur()){
            case "istek":{
                holder.bsimge.setImageResource(R.drawable.ic_arkadaslikistegi);
                break;
            }
            case "kombin_begeni":{
                holder.bsimge.setImageResource(R.drawable.ic_like_12dp);
                break;
            }
            case "yorum_begeni":{
                holder.bsimge.setImageResource(R.drawable.ic_like_12dp);
                break;
            }
            case "kombine_yorum":{
                holder.bsimge.setImageResource(R.drawable.ic_yorumlar_12dp);
                break;
            }
            case "profile_yorum":{
                holder.bsimge.setImageResource(R.drawable.ic_yorumlar_12dp);
                break;
            }
            case "kombin_favori":{
                holder.bsimge.setImageResource(R.drawable.ic_bookmark_12dp);
            }
        }

        holder.bildirimcardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tr = a.getTur();
                String lnk = a.getLink();
                switch (tr) {
                    case "istek": {
                        Intent go_profile = new Intent(activity, UsersProfileActivity.class);
                        go_profile.putExtra("user_id", lnk);
                        activity.startActivity(go_profile);
                        activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        break;
                    }
                    case "kombin_begeni": {
                        Intent go_kombin = new Intent(activity, KombinActivity.class);
                        go_kombin.putExtra("kombin_id", lnk);
                        go_kombin.putExtra("kombin_kimden",BaslaActivity.vb.getUserID());
                        activity.startActivity(go_kombin);
                        activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        break;
                    }
                    case "yorum_begeni": {
                        //sadece yorumun görüntüleneceği bir activityye yönlendirilecek
                    }
                    case "profile_yorum": {
                        //sadece yorumun görüntüleneceği bir activityye yönlendirilecek
                    }
                    case "kombine_yorum":{
                        Intent go_kombin = new Intent(activity, KombinActivity.class);
                        go_kombin.putExtra("kombin_id", lnk);
                        go_kombin.putExtra("kombin_kimden",BaslaActivity.vb.getUserID());
                        activity.startActivity(go_kombin);
                        activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        break;
                    }
                    case "kombin_favori": {
                        Intent go_kombin = new Intent(activity, KombinActivity.class);
                        go_kombin.putExtra("kombin_id", lnk);
                        go_kombin.putExtra("kombin_kimden",BaslaActivity.vb.getUserID());
                        activity.startActivity(go_kombin);
                        activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        break;
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return bildirimler.size();
    }

    class BildirimlerViewHolder extends RecyclerView.ViewHolder {

        private TextView bicerik, btarih;
        private ImageView bsimge;
        private CircleImageView bildirimkimdenresmi;
        private CardView bildirimcardview;

        private BildirimlerViewHolder(@NonNull View itemView ) {
            super(itemView);

            bicerik = itemView.findViewById(R.id.bildirimicerik);
            btarih = itemView.findViewById(R.id.bildirimtarih);
            bsimge=itemView.findViewById(R.id.bildirimsimge);
            bildirimkimdenresmi=itemView.findViewById(R.id.bildirimkimdenresmi);
            bildirimcardview=itemView.findViewById(R.id.bildirimlercardview);

        }


    }

}

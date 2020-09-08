package com.example.kombinim.Adapters;

import android.app.Dialog;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kombinim.BaslaActivity;
import com.example.kombinim.Fragments.ProfilFragments.YorumlarFragment;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Begeni;
import com.example.kombinim.VeriSiniflari.Bildirim;
import com.example.kombinim.VeriSiniflari.Yorum;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class YorumAdapter extends RecyclerView.Adapter<YorumAdapter.YorumViewHolder> {

    private List<Yorum> yorumlar;
    private Button yorumsilsil, yorumsilkapat;
    private Context context;

    public YorumAdapter(List<Yorum> yorumList, Context context) {
        this.yorumlar = yorumList;
        this.context=context;
    }

    @NonNull
    @Override
    public YorumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_yorum_layout,parent,false);
        return new YorumViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final YorumViewHolder holder, final int position) {
       final Yorum a=yorumlar.get(position);

       //kullanıcıya göre nelere erişebileceği kontrolü
       if(BaslaActivity.vb.getUserID().equals(YorumlarFragment.profil_sahibi)){
           holder.yorumsil.setVisibility(View.VISIBLE);
       }else{
           if(BaslaActivity.vb.getUserID().equals(a.getKimden())){
               holder.yorumsil.setVisibility(View.VISIBLE);
           }else{
               holder.yorumsil.setVisibility(View.GONE);
           }
       }
       //kullanıcıya göre nelere erişebileceği kontrolü

        //aktif kullanici o yorumu begenmismi kontrolü
        BaslaActivity.vb.getDbref("Begeniler")
                .child("Yorum_Beğenileri")
                .child(a.getId())
                .orderByChild("begenen")
                .equalTo(BaslaActivity.vb.getUserID())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            holder.yorumbegen.setImageResource(R.drawable.ic_like_okay);
                            holder.yorumbegen.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                        snapshot.getRef().removeValue();
                                    }
                                }
                            });
                        }else{
                            holder.yorumbegen.setImageResource(R.drawable.ic_like);
                            holder.yorumbegen.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    final String currentDate= DateFormat.getDateTimeInstance().format(new Date());
                                    Begeni yenibegeni=new Begeni(BaslaActivity.vb.getUserID(),currentDate);
                                    BaslaActivity.vb.getDbref("Begeniler")
                                            .child("Yorum_Beğenileri")
                                            .child(a.getId())
                                            .push()
                                            .setValue(yenibegeni);
                                    if(!a.getKimden().equals(BaslaActivity.vb.getUserID())){
                                        Bildirim bildirim=new Bildirim(a.getId(),"yorum_begeni"," senin bir yorumunu beğendi.");
                                        BaslaActivity.vb.getDbref("Bildirimler")
                                                .child(a.getKimden())
                                                .push()
                                                .setValue(bildirim);
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        //aktif kullanici o yorumu begenmismi kontrolü

        //begenisayisi
        BaslaActivity.vb.getDbref("Begeniler")
                .child("Yorum_Beğenileri")
                .child(a.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Long sayi=dataSnapshot.getChildrenCount();
                        String begenisayisi=sayi.toString()+" Beğeni";
                        if(begenisayisi.equals("0 Beğeni")){
                            holder.yorumbegenisayisi.setText("");
                        }else{
                            holder.yorumbegenisayisi.setText(begenisayisi);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        //begenisayisi

       holder.yorumtext.setText(a.getYorum_icerigi());
       holder.yorumtarih.setText(a.getYorum_tarihi());

       //yorumu atanın bilgilerinin çekilmesi
       BaslaActivity.vb.getDbref("Kullanicilar")
               .child(a.getKimden())
               .addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       String yorumcuadi= Objects.requireNonNull(dataSnapshot.child("kullaniciadi").getValue()).toString();
                       String profilresmi= Objects.requireNonNull(dataSnapshot.child("profilresmi").getValue()).toString();

                       holder.yorumsahibi.setText(yorumcuadi);
                       if(profilresmi.equals("default")){
                           holder.yorumimage.setImageResource(R.drawable.ic_person_black_24dp);
                       }else{
                           Picasso.get().load(profilresmi).into(holder.yorumimage);
                       }
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError databaseError) {

                   }
               });
        //yorumu atanın bilgilerinin çekilmesi

        //yorumun silinmesi
       holder.yorumsil.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               final Dialog MyDialog=new Dialog(context);
               MyDialog.setContentView(R.layout.yorum_silme_dialog);
               MyDialog.setCanceledOnTouchOutside(false);
               Objects.requireNonNull(MyDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
               MyDialog.show();

               yorumsilsil=MyDialog.findViewById(R.id.yorumsilsil);
               yorumsilkapat=MyDialog.findViewById(R.id.yorumsilkapat);

               yorumsilkapat.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       if(MyDialog.isShowing()){
                           MyDialog.cancel();
                       }
                   }
               });

               yorumsilsil.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       BaslaActivity.vb.getDbref("Begeniler")
                               .child("Yorum_Beğenileri")
                               .child(a.getId())
                               .removeValue();

                       BaslaActivity.vb.getDbref("Yorumlar")
                               .child("Profil_Yorumlari")
                               .child(YorumlarFragment.profil_sahibi)
                               .orderByChild("id").equalTo(a.getId())
                               .addListenerForSingleValueEvent(new ValueEventListener() {
                                   @Override
                                   public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                       for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                           snapshot.getRef().removeValue();
                                       }
                                   }

                                   @Override
                                   public void onCancelled(@NonNull DatabaseError databaseError) {

                                   }
                               });

                       if(MyDialog.isShowing()){
                           MyDialog.cancel();
                       }
                   }
               });
           }
       });
        //yorumun silinmesi

    }

    @Override
    public int getItemCount() {
        return yorumlar.size();
    }

    class YorumViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView yorumimage;
        private TextView yorumsahibi,yorumtext,yorumtarih,yorumbegenisayisi;
        private ImageButton yorumsil,yorumbegen;

        private YorumViewHolder(@NonNull View itemView) {
            super(itemView);
            yorumimage=itemView.findViewById(R.id.tekliyorumimage);
            yorumsahibi=itemView.findViewById(R.id.tekliyorumusername);
            yorumtext=itemView.findViewById(R.id.tekliyorumstatus);
            yorumtarih=itemView.findViewById(R.id.tekliyorumtarih);
            yorumsil=itemView.findViewById(R.id.tekliyorumsil);
            yorumbegen=itemView.findViewById(R.id.yorumbegen);
            yorumbegenisayisi=itemView.findViewById(R.id.yorumbegenisayisi);
        }
    }

}

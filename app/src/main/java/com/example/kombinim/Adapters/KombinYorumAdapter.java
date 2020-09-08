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

import com.example.kombinim.Activities.KombinActivity;
import com.example.kombinim.BaslaActivity;
import com.example.kombinim.R;
import com.example.kombinim.VeriSiniflari.Yorum;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class KombinYorumAdapter extends RecyclerView.Adapter<KombinYorumAdapter.KombinYorumViewHolder> {

    private List<Yorum> yorumlar;
    private Button yorumsilsil, yorumsilkapat;
    private Context context;

    public KombinYorumAdapter(List<Yorum> yorumList, Context context) {
        this.yorumlar = yorumList;
        this.context=context;
    }

    @NonNull
    @Override
    public KombinYorumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_kombin_yorum_layout,parent,false);
        return new KombinYorumViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final KombinYorumViewHolder holder, final int position) {
       final Yorum a=yorumlar.get(position);

       //kullanıcıya göre nelere erişebileceği kontrolü
       if(BaslaActivity.vb.getUserID().equals(KombinActivity.kombin_kimden)){
           holder.yorumsil.setVisibility(View.VISIBLE);
       }else{
           if(BaslaActivity.vb.getUserID().equals(a.getKimden())){
               holder.yorumsil.setVisibility(View.VISIBLE);
           }else{
               holder.yorumsil.setVisibility(View.GONE);
           }
       }
       //kullanıcıya göre nelere erişebileceği kontrolü

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
               Objects.requireNonNull(MyDialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
               MyDialog.setCanceledOnTouchOutside(false);
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
                       BaslaActivity.vb.getDbref("Yorumlar")
                               .child("Kombin_Yorumlari")
                               .child(KombinActivity.kombin_id)
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

    class KombinYorumViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView yorumimage;
        private TextView yorumsahibi,yorumtext,yorumtarih;
        private ImageButton yorumsil;

        private KombinYorumViewHolder(@NonNull View itemView) {
            super(itemView);
            yorumimage=itemView.findViewById(R.id.single_kombin_yorum_resmi);
            yorumsahibi=itemView.findViewById(R.id.single_kombin_yorum_ismi);
            yorumtext=itemView.findViewById(R.id.single_kombin_yorum_yorum);
            yorumtarih=itemView.findViewById(R.id.single_kombin_yorum_tarih);
            yorumsil=itemView.findViewById(R.id.single_kombin_yorum_sil);
        }
    }

}

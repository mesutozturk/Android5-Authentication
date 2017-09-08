package com.wissen.mesut.j6_5authentication.tool;

import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wissen.mesut.j6_5authentication.R;
import com.wissen.mesut.j6_5authentication.model.Kisi;
import com.wissen.mesut.j6_5authentication.model.Yapilacak;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Mesut on 8.09.2017.
 */

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.ViewHolder> {


    FirebaseDatabase database;
    DatabaseReference myRef;
    private ArrayList<Yapilacak> yapilacakList;
    private CustomItemClickListener itemClickListener;
    private CustomItemLongClickListener itemLongClickListener;

    public MyRecyclerAdapter(ArrayList<Yapilacak> yapilacakList, CustomItemClickListener itemClickListener, CustomItemLongClickListener itemLongClickListener) {
        this.yapilacakList = yapilacakList;
        this.itemClickListener = itemClickListener;
        this.itemLongClickListener = itemLongClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.todolist_card, parent, false);
        final ViewHolder view_holder = new ViewHolder(v);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClick(v, view_holder.getPosition());
            }
        });
        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                itemLongClickListener.onItemLongClick(view, view_holder.getPosition());
                return false;
            }
        });
        return view_holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("uyeler");
        final Query query = myRef.child(yapilacakList.get(position).getEkleyen());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Kisi kisi = dataSnapshot.getValue(Kisi.class);
                if (kisi.getAd() != null && kisi.getSoyad() != null)
                    holder.txtKullaniciAdi.setText(kisi.getAd() + " " + kisi.getSoyad());
                if (kisi.getFotograf() != null)
                    holder.img_profil.setImageBitmap(AppTool.stringToBitmap(kisi.getFotograf()));
                query.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        holder.txtIcerik.setText(yapilacakList.get(position).getIcerik());
        final int pos = position;
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    holder.txtIcerik.setPaintFlags(holder.txtIcerik.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    if (yapilacakList.get(pos).getYapilmaZamani() != null)
                        return;
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference().child("yapilacaklar");
                    Yapilacak yapilacak = yapilacakList.get(pos);
                    yapilacak.setYapilmaZamani(new Date().toString());
                    myRef.child(yapilacak.getId()).setValue(yapilacak);

                } else {
                    holder.txtIcerik.setPaintFlags(holder.txtIcerik.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference().child("yapilacaklar");
                    Yapilacak yapilacak = yapilacakList.get(pos);
                    yapilacak.setYapilmaZamani(null);
                    myRef.child(yapilacak.getId()).setValue(yapilacak);
                }
            }
        });
        if (yapilacakList.get(position).getYapilmaZamani() == null)
            holder.checkBox.setChecked(false);
        else
            holder.checkBox.setChecked(true);
    }

    @Override
    public int getItemCount() {
        return yapilacakList.size();
    }

    private void kullaniciBilgisiniGetir(String ekleyen, final CircleImageView imageView, final TextView textView) {

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CheckBox checkBox;
        public TextView txtKullaniciAdi, txtIcerik;
        public CircleImageView img_profil;
        public CardView card_view;


        public ViewHolder(View view) {
            super(view);

            card_view = view.findViewById(R.id.todo_cardView);
            checkBox = view.findViewById(R.id.card_checkBox);
            txtKullaniciAdi = view.findViewById(R.id.card_txtKullaniciAdi);
            txtIcerik = view.findViewById(R.id.card_txtIcerik);
            img_profil = view.findViewById(R.id.card_imgprofil);
        }
    }
}

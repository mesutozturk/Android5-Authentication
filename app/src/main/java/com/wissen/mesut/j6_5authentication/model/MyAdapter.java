package com.wissen.mesut.j6_5authentication.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wissen.mesut.j6_5authentication.R;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Mesut on 6.09.2017.
 */

public class MyAdapter extends ArrayAdapter<Yapilacak> {
    private final Context context;
    private final ArrayList<Yapilacak> values;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    public MyAdapter(Context context, ArrayList<Yapilacak> values) {
        super(context, R.layout.todolist_item, values);
        this.context = context;
        this.values = values;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.todolist_item, parent, false);
        CheckBox checkBox = rowView.findViewById(R.id.lst_checkBox);
        final TextView txtIcerik = rowView.findViewById(R.id.lst_txtIcerik);
        txtIcerik.setText(values.get(position).getIcerik());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    txtIcerik.setPaintFlags(txtIcerik.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    if (values.get(position).getYapilmaZamani() != null)
                        return;
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference().child("yapilacaklar");
                    Yapilacak yapilacak = values.get(position);
                    yapilacak.setYapilmaZamani(new Date().toString());
                    myRef.child(yapilacak.getId()).setValue(yapilacak);

                } else {
                    txtIcerik.setPaintFlags(txtIcerik.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    database = FirebaseDatabase.getInstance();
                    myRef = database.getReference().child("yapilacaklar");
                    Yapilacak yapilacak = values.get(position);
                    yapilacak.setYapilmaZamani(null);
                    myRef.child(yapilacak.getId()).setValue(yapilacak);
                }

            }
        });
        if (values.get(position).getYapilmaZamani() == null)
            checkBox.setChecked(false);
        else
            checkBox.setChecked(true);
        txtIcerik.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                Yapilacak silinecek = values.get(position);
                                database = FirebaseDatabase.getInstance();
                                myRef = database.getReference().child("yapilacaklar");
                                myRef.child(silinecek.getId()).removeValue();
                                Toast.makeText(context, "Silindi", Toast.LENGTH_SHORT).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                Toast.makeText(context, "İptal edildi", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Emin misiniz?").setPositiveButton("Evet", dialogClickListener)
                        .setNegativeButton("Bilemiyorum", dialogClickListener).show();
                return false;

            }
        });
        return rowView;
    }

}

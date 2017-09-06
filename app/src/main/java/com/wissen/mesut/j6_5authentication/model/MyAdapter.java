package com.wissen.mesut.j6_5authentication.model;

import android.content.Context;
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

import com.wissen.mesut.j6_5authentication.R;

import java.util.ArrayList;

/**
 * Created by Mesut on 6.09.2017.
 */

public class MyAdapter extends ArrayAdapter<Yapilacak> {
    private final Context context;
    private final ArrayList<Yapilacak> values;

    public MyAdapter(Context context, ArrayList<Yapilacak> values) {
        super(context, R.layout.todolist_item, values);
        this.context = context;
        this.values = values;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.todolist_item, parent, false);
        CheckBox checkBox = rowView.findViewById(R.id.lst_checkBox);
        final TextView txtIcerik = rowView.findViewById(R.id.lst_txtIcerik);
        txtIcerik.setText(values.get(position).getIcerik());
        if (values.get(position).getYapilmaZamani() == null)
            checkBox.setChecked(false);
        else
            checkBox.setChecked(true);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b)
                    txtIcerik.setPaintFlags(txtIcerik.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                else
                    txtIcerik.setPaintFlags(txtIcerik.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }
        });
        return rowView;
    }
}

package com.wissen.mesut.j6_5authentication;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.wissen.mesut.j6_5authentication.base.BaseActivity;
import com.wissen.mesut.j6_5authentication.model.Yapilacak;

import java.util.Date;

public class AddTodo extends BaseActivity {
    Button btnEkle;
    EditText txtYapilacak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);
        btnEkle = (Button) findViewById(R.id.add_btnEkle);
        txtYapilacak = (EditText) findViewById(R.id.add_txtYapilacak);
        btnEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(txtYapilacak.getText())) {
                    Toast.makeText(AddTodo.this, "Lütfen birşeyler yazın", Toast.LENGTH_SHORT).show();
                    return;
                }
                showProgressDialog("Veritabanına bağlanıyor", "Lütfen bekleyiniz");
                Yapilacak yapilacak = new Yapilacak();
                yapilacak.setIcerik(txtYapilacak.getText().toString());
                yapilacak.setEklenmeZamani(new Date().toString());
                mAuth = FirebaseAuth.getInstance();
                user = mAuth.getCurrentUser();
                yapilacak.setEkleyen(user.getUid());
                database = FirebaseDatabase.getInstance();
                myRef = database.getReference().child("yapilacaklar");
                myRef.child(yapilacak.getId()).setValue(yapilacak);
                hideProgressDialog();
                finish();
            }
        });
    }
}

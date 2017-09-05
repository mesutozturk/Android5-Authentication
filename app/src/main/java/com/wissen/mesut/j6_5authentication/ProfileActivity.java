package com.wissen.mesut.j6_5authentication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wissen.mesut.j6_5authentication.model.Kisi;

import java.time.chrono.IsoEra;

public class ProfileActivity extends BaseActivity {
    EditText txtAd, txtSoyad, txtEmail;
    Button btnGuncelle;
    FirebaseUser user;
    FirebaseDatabase database;
    DatabaseReference myRef;
    Kisi kullanici;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        txtAd = (EditText) findViewById(R.id.profil_duzenle_txtad);
        txtSoyad = (EditText) findViewById(R.id.profil_duzenle_txtsoyad);
        txtEmail = (EditText) findViewById(R.id.profil_duzenle_txtemail);
        btnGuncelle = (Button) findViewById(R.id.profil_duzenle_btnguncelle);
        showProgressDialog("Lütfen Bekleyiniz", "Profil bilginize erişiliyor");

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user == null) finish();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("uyeler");
        Query query = myRef.child(user.getUid());

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                kullanici = dataSnapshot.getValue(Kisi.class);
                txtAd.setText(kullanici.getAd());
                txtSoyad.setText(kullanici.getSoyad());
                txtEmail.setText(kullanici.getEmail());
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnGuncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean emailDegistiMi = false;
                Kisi guncellenecekKisi = new Kisi();
                emailDegistiMi = !kullanici.getEmail().equals(txtEmail.getText().toString());
                guncellenecekKisi.setId(user.getUid());
                guncellenecekKisi.setEmail(txtEmail.getText().toString());
                guncellenecekKisi.setAd(txtAd.getText().toString());
                guncellenecekKisi.setSoyad(txtSoyad.getText().toString());
                database = FirebaseDatabase.getInstance();
                myRef = database.getReference().child("uyeler");
                myRef.child(user.getUid()).setValue(guncellenecekKisi);
                if(emailDegistiMi){
                    mAuth = FirebaseAuth.getInstance();
                    user = mAuth.getCurrentUser();
                    user.updateEmail(txtEmail.getText().toString());
                    user.sendEmailVerification();
                    mAuth.signOut();
                }
                Toast.makeText(ProfileActivity.this, "Güncelleme işlemi başarılı", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}

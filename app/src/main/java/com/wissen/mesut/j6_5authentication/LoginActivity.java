package com.wissen.mesut.j6_5authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wissen.mesut.j6_5authentication.model.Kisi;

public class LoginActivity extends BaseActivity {
    Button btnLogin, btnRegister;
    EditText txtEmail, txtPassword;
    FirebaseDatabase database;
    DatabaseReference myRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin = (Button) findViewById(R.id.btnGirisYap_login);
        btnRegister = (Button) findViewById(R.id.btnKayitOl_login);
        txtEmail = (EditText) findViewById(R.id.txtEmail_login);
        txtPassword = (EditText) findViewById(R.id.txtPassword_login);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                girisYap(txtEmail.getText().toString(), txtPassword.getText().toString());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        showProgressDialog("Giriş","Lütfen Bekleyin");
        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.isEmailVerified()) {
                database= FirebaseDatabase.getInstance();
                myRef=database.getReference().child("uyeler");
                Query query=myRef.child(user.getUid());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()){
                            Kisi yeniKisi= new Kisi();
                            yeniKisi.setEmail(user.getEmail());
                            yeniKisi.setId(user.getUid());
                            myRef.child(user.getUid()).setValue(yeniKisi);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                Toast.makeText(this, "Hoşgeldiniz", Toast.LENGTH_SHORT).show();
                hideProgressDialog();
                startActivity(new Intent(this, MainActivity.class));
            }
        }
    }

    private void girisYap(String email, String pass) {
        if (!validateForm(txtEmail, txtPassword)) return;
        showProgressDialog("Giriş", "Sisteme giriş yapılıyor");
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user.isEmailVerified()) {
                        Toast.makeText(LoginActivity.this, "Hoşgeldin sahip", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    } else {
                        Toast.makeText(LoginActivity.this, "Email adresinizi doğrulayın!", Toast.LENGTH_SHORT).show();
                        kullaniciDogrula();
                    }
                } else if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Kullanıcı adı veya şifre hatalı", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Login başarısız", Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }
        });
    }

    private void kullaniciDogrula() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(LoginActivity.this, "Posta kutunuzu kontrol edin", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
    }
}

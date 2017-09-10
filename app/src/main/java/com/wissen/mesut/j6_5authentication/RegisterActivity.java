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
import com.wissen.mesut.j6_5authentication.base.BaseActivity;

public class RegisterActivity extends BaseActivity {
    Button btnLogin, btnRegister;
    EditText txtEmail, txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        btnLogin = (Button) findViewById(R.id.btnGirisYap_register);
        btnRegister = (Button) findViewById(R.id.btnKayitOl_register);
        txtEmail = (EditText) findViewById(R.id.txtEmail_register);
        txtPassword = (EditText) findViewById(R.id.txtPassword_register);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                kullaniciOlustur(txtEmail.getText().toString(), txtPassword.getText().toString());
            }
        });
    }

    private void kullaniciOlustur(String email, String pass) {
        if (!validateForm(txtEmail, txtPassword))
            return;
        showProgressDialog("Kayıt İşlemi", "Lütfen bekleyin");

        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful())
                    Toast.makeText(RegisterActivity.this, "Kayıt işleminde bir hata oluştu", Toast.LENGTH_SHORT).show();
                hideProgressDialog();
            }
        });
    }
}

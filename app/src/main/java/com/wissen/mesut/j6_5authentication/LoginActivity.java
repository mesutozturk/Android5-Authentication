package com.wissen.mesut.j6_5authentication;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wissen.mesut.j6_5authentication.base.BaseActivity;
import com.wissen.mesut.j6_5authentication.model.Kisi;

public class LoginActivity extends BaseActivity {
    private static final int RC_SIGN_IN = 9001;
    private Button btnLogin, btnRegister, btnSifremiUnuttum;
    private EditText txtEmail, txtPassword;
    private SignInButton btnGoogleGirisYap;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnSifremiUnuttum = (Button) findViewById(R.id.btnSifremiUnuttum_login);
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
        btnSifremiUnuttum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                if (TextUtils.isEmpty(txtEmail.getText()))
                    Toast.makeText(LoginActivity.this, "Mail Adresini yazınız", Toast.LENGTH_SHORT).show();
                else
                    mAuth.sendPasswordResetEmail(txtEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(LoginActivity.this, txtEmail.getText().toString() + " adresine parola sıfırlama linki gönderildi", Toast.LENGTH_LONG).show();
                        }
                    });
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        btnGoogleGirisYap = (SignInButton) findViewById(R.id.btnGoogleGirisYap_login);
        btnGoogleGirisYap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog("Giriş", "Lütfen Bekleyin");
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
        kullaniciKontrol();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            kullaniciKontrol();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void girisYap(String email, String pass) {
        if (!validateForm(txtEmail, txtPassword)) return;
        showProgressDialog("Giriş", "Sisteme giriş yapılıyor");
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    kullaniciKontrol();
                } else if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Kullanıcı adı veya şifre hatalı", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Login başarısız", Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }
        });
    }

    private void kullaniciKontrol() {
        showProgressDialog("Giriş", "Lütfen Bekleyin");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.isEmailVerified()) {
                database = FirebaseDatabase.getInstance();
                myRef = database.getReference().child("uyeler");
                final Query query = myRef.child(user.getUid());
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            Kisi yeniKisi = new Kisi();
                            yeniKisi.setEmail(user.getEmail());
                            yeniKisi.setId(user.getUid());
                            myRef.child(user.getUid()).setValue(yeniKisi);
                        }
                        query.removeEventListener(this);
                        Toast.makeText(LoginActivity.this, "Hoşgeldiniz", Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                Toast.makeText(LoginActivity.this, "Email adresinizi doğrulayın!", Toast.LENGTH_SHORT).show();
                kullaniciDogrula();
            }
        }
        hideProgressDialog();
    }

    private void kullaniciDogrula() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(LoginActivity.this, "Posta kutunuzu kontrol edin", Toast.LENGTH_SHORT).show();
                    //finish();
                }
            });
        }
    }
}

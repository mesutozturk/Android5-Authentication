package com.wissen.mesut.j6_5authentication.base;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Mesut on 28.08.2017.
 */

public class BaseActivity extends AppCompatActivity {
    public ProgressDialog mProgressDialog;
    public FirebaseAuth mAuth;
    public FirebaseUser user;
    public FirebaseDatabase database;
    public DatabaseReference myRef;

    public boolean validateForm(EditText txtEmail, EditText txtPass) {
        boolean valid = true;
        String email = txtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError("Gerekli!");
            valid = false;
        } else {
            txtEmail.setError(null);
        }
        String password = txtPass.getText().toString();
        if (TextUtils.isEmpty(password)) {
            txtPass.setError("Gerekli.");
            valid = false;
        } else {
            txtPass.setError(null);
        }
        if (txtPass.getText().length() < 6) {
            txtPass.setError("Şifre en az 6 karakter olmalı.");
            valid = false;
        } else {
            txtPass.setError(null);
        }
        return valid;
    }

    public void showProgressDialog(String title, String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}

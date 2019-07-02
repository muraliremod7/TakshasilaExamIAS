package com.takshasila.exam;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.takshasila.exam.commonclass.User;
import com.takshasila.exam.helpers.PrefManager;
import com.takshasila.exam.helpers.ProgressDailog;
import com.takshasila.exam.takshasilaexam.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText editTextEmail,editTextPassword;
    private Button login;
    private FirebaseAuth auth;
    private ProgressDailog progressDailog;
    private PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = findViewById(R.id.email_login);
        editTextPassword = findViewById(R.id.password_login);

        login = findViewById(R.id.login);
        login.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
        progressDailog = new ProgressDailog(this);
        prefManager = new PrefManager(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.login:
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                if (email.isEmpty()) {
                    editTextEmail.setError(getString(R.string.input_error_email));
                    editTextEmail.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError(getString(R.string.input_error_email_invalid));
                    editTextEmail.requestFocus();
                    return;
                }

                if (password.isEmpty()) {
                    editTextPassword.setError(getString(R.string.input_error_password));
                    editTextPassword.requestFocus();
                    return;
                }

                if (password.length() < 6) {
                    editTextPassword.setError(getString(R.string.input_error_password_length));
                    editTextPassword.requestFocus();
                    return;
                }
                login(email,password);
                break;
        }
    }

    private void login(String email, final String password) {
        progressDailog.showDailog();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                editTextPassword.setError("Password Should be minimum 6 characters");
                            } else {
                            }
                        } else {
                            User user = new User();
                            String name = user.getName();
                            String email = user.getEmail();
                            String phone = user.getPhone();
                            prefManager.createLogin(phone,email,name);
                            prefManager.setEmail(email);
                            prefManager.setMobileNumber(phone);
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }
}

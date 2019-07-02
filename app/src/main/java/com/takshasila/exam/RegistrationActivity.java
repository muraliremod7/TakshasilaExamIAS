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
import com.google.firebase.database.FirebaseDatabase;
import com.takshasila.exam.commonclass.User;
import com.takshasila.exam.helpers.PrefManager;
import com.takshasila.exam.takshasilaexam.R;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText editTextName, editTextEmail, editTextPassword,editTextconPassword, editTextPhone;
    private Button register;
    private FirebaseAuth mAuth;
    private PrefManager prefManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editTextName = findViewById(R.id.name_register);
        editTextEmail = findViewById(R.id.mailid_register);
        editTextPhone = findViewById(R.id.mobile_register);
        editTextPassword = findViewById(R.id.password_register);
        editTextconPassword = findViewById(R.id.checkpassword_register);

        register = findViewById(R.id.registration);
        register.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        prefManager = new PrefManager(this);

    }
    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null) {
            //handle the already login user
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.registration:
                String name = editTextName.getText().toString();
                String email = editTextEmail.getText().toString();
                String phone = editTextPhone.getText().toString();
                String password = editTextPassword.getText().toString();
                String conpassword = editTextconPassword.getText().toString();

                if (name.isEmpty()) {
                    editTextName.setError(getString(R.string.input_error_name));
                    editTextName.requestFocus();
                    return;
                }

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

                if (phone.isEmpty()) {
                    editTextPhone.setError(getString(R.string.input_error_phone));
                    editTextPhone.requestFocus();
                    return;
                }

                if (phone.length() != 10) {
                    editTextPhone.setError(getString(R.string.input_error_phone_invalid));
                    editTextPhone.requestFocus();
                    return;
                }

                if(password.equals(conpassword)){
                    register(email,password,name,phone);
                }
                break;
            case android.R.id.home:
                this.finish();
                break;
        }
    }

    private void register(final String email, String password, final String name, final String phone) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            User user = new User(
                                    name,
                                    email,
                                    phone
                            );

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        //display a failure message
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}

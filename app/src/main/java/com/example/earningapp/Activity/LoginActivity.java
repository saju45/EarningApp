package com.example.earningapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.earningapp.R;
import com.example.earningapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth=FirebaseAuth.getInstance();

        binding.signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=binding.email.getText().toString();
                String password=binding.password.getText().toString();

                if (email.isEmpty())
                {
                    binding.email.setError("Please Enter your email");
                    binding.email.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    binding.email.setError("Enter valid Email");
                    binding.email.requestFocus();
                }else if (password.isEmpty())
                {
                    binding.password.setError("Please enter your password");
                    binding.password.requestFocus();
                }else if (password.length()<6)
                {
                    binding.password.setError("Password length should be 6");
                    binding.password.requestFocus();
                }else {

                    binding.progressBar.setVisibility(View.VISIBLE);
                    auth.signInWithEmailAndPassword(email,password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful())
                                    {
                                        binding.progressBar.setVisibility(View.GONE);
                                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                        finish();
                                        Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
                                    }else {
                                        binding.progressBar.setVisibility(View.GONE);
                                        Toast.makeText(LoginActivity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
        }

    }
}
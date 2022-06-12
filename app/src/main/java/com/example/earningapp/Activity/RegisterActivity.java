package com.example.earningapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.earningapp.R;
import com.example.earningapp.databinding.ActivityRegisterBinding;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {


    ActivityRegisterBinding binding;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        auth=FirebaseAuth.getInstance();


        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name=binding.EdName.getText().toString();
                String email=binding.EtEmail.getText().toString();
                String password=binding.EtPassword.getText().toString();
                String ctPassword=binding.EtConfirmPass.getText().toString();


                if (name.isEmpty())
                {
                    binding.EdName.setError("Please enter your name");
                    binding.EdName.requestFocus();
                }else if (email.isEmpty())
                {
                    binding.EtEmail.setError("please enter your email");
                    binding.EtEmail.requestFocus();
                }else if (password.isEmpty())
                {
                    binding.EtPassword.setError("Please enter your password");
                    binding.EtPassword.requestFocus();
                }else if(ctPassword.isEmpty())
                {
                    binding.EtConfirmPass.setError("please enter your password");
                    binding.EtConfirmPass.requestFocus();
                }else {

                    binding.progressBar.setVisibility(View.VISIBLE);

                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {
                                binding.progressBar.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, "Registration is successfully", Toast.LENGTH_SHORT).show();
                                FirebaseUser user=auth.getCurrentUser();
                                saveUser(user,email);

                            }else {
                                binding.progressBar.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }

            }
        });
        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                finish();
            }
        });
    }

    public void saveUser(FirebaseUser user,String email)
    {

        String refer=email.substring(0,email.lastIndexOf("@"));
        String refercode=refer.replace(".","");

        HashMap<String,Object> map=new HashMap<>();
        map.put("name",binding.EdName.getText().toString());
        map.put("email",email);
        map.put("profileImg"," ");
        map.put("uId",user.getUid());
        map.put("coins",0);
        map.put("referCode",refercode);
        map.put("spins",2);

        Date date= Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyy", Locale.ENGLISH);

        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH,-1); // to get yesterday date

        Date previousDate=calendar.getTime();
        String dateString=simpleDateFormat.format(previousDate);

        FirebaseDatabase.getInstance().getReference()
                .child("dailyCheck")
                .child(user.getUid())
                .child("date")
                .setValue(dateString);

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();

        databaseReference.child("users").child(user.getUid()).setValue(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful())
                        {
                            startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                            finish();

                        }else {
                            Toast.makeText(RegisterActivity.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                });

    }
}
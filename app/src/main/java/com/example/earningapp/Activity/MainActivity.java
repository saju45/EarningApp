package com.example.earningapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.earningapp.Fragments.FragmentReplacerActivity;
import com.example.earningapp.Model.Internet;
import com.example.earningapp.Model.ProfileModel;
import com.example.earningapp.R;
import com.example.earningapp.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {


    ActivityMainBinding binding;

    DatabaseReference reference;
    FirebaseUser firebaseUser;
    FirebaseAuth firebaseAuth;

    private Dialog dialog;
    Internet internet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
       // checkInternetConnection();
        internet=new Internet(MainActivity.this);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser= firebaseAuth.getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference().child("users");


        dialog=new Dialog(this);
        dialog.setContentView(R.layout.loading_dialog);
        if (dialog.getWindow()!=null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        getDataFromDatabase();

        binding.toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "ToolBar", Toast.LENGTH_SHORT).show();
            }
        });


        binding.luckySpin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, FragmentReplacerActivity.class);
                intent.putExtra("position",2);
                startActivity(intent);
            }
        });

        binding.dailyCheckCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dailyCheck();
            }
        });

        binding.referCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,InviteActivity.class));
                finish();
            }
        });

        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ProfileActivity.class));

            }
        });

        binding.redeemCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(MainActivity.this,RedeemActivity2.class));
                finish();
            }
        });

    }

    public void getDataFromDatabase()
    {
        dialog.show();

        reference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                if(snapshot.exists())
                {
                    ProfileModel profileModel=snapshot.getValue(ProfileModel.class);
                    binding.name.setText(profileModel.getName());
                    binding.email.setText(profileModel.getEmail());
                    binding.coins.setText(profileModel.getCoins()+"");
                  // Picasso.get().load(profileModel.getProfileImg()).placeholder(R.drawable.profile).into(binding.profile);

                    Glide.with(getApplicationContext())
                            .load(profileModel.getProfileImg())
                            .placeholder(R.drawable.profile)
                            .into(binding.profile);

                    dialog.dismiss();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(MainActivity.this, "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
                dialog.dismiss();
            }
        });
    }


    public void checkInternetConnection(){

        if (internet.isConnected())
        {
            new isInternetActive().execute();
        }else {
            Toast.makeText(this, "Please Check your Internet ", Toast.LENGTH_SHORT).show();
        }
    }

    public void dailyCheck() {

        if (internet.isConnected()) {

            final SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            sweetAlertDialog.setTitleText("Please wait...");
            sweetAlertDialog.setCancelable(false);
            sweetAlertDialog.show();

            final Date currentDate = Calendar.getInstance().getTime();
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            //final String date=simpleDateFormat.format(currentDate);

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            databaseReference.child("dailyCheck").child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            if (snapshot.exists()) {
                                String dbDateString = snapshot.child("date").getValue(String.class);

                                try {

                                    assert dbDateString != null;
                                    Date dbDate = simpleDateFormat.parse(dbDateString);
                                    String xDate = simpleDateFormat.format(currentDate);
                                    Date date = simpleDateFormat.parse(xDate);

                                    if (date.after(dbDate) && date.compareTo(dbDate) != 0) {
                                        //reward available

                                        databaseReference.child("users").child(firebaseUser.getUid())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                        ProfileModel model = snapshot.getValue(ProfileModel.class);

                                                        int currentCoins = model.getCoins();
                                                        int update = currentCoins + 10;
                                                        int spinC = model.getSpins();
                                                        int updateSpin = spinC + 2;

                                                        HashMap<String, Object> hashMap = new HashMap<>();
                                                        hashMap.put("coins", update);
                                                        hashMap.put("spins", updateSpin);

                                                        databaseReference.child("users").child(firebaseUser.getUid()).updateChildren(hashMap);
                                                        Date newDate = Calendar.getInstance().getTime();
                                                        String newDateString = simpleDateFormat.format(newDate);

                                                        HashMap<String, Object> dateMap = new HashMap<>();
                                                        dateMap.put("date", newDateString);
                                                        databaseReference.child("dailyCheck").child(firebaseUser.getUid()).setValue(dateMap)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                        sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                                                        sweetAlertDialog.setTitleText("success");
                                                                        sweetAlertDialog.setContentText("Coins added to your account Successfully");
                                                                        sweetAlertDialog.setConfirmButton("Dismiss", new SweetAlertDialog.OnSweetClickListener() {
                                                                            @Override
                                                                            public void onClick(SweetAlertDialog sweetAlertDialog) {

                                                                                sweetAlertDialog.dismissWithAnimation();
                                                                            }
                                                                        }).show();
                                                                    }
                                                                });
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Toast.makeText(MainActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });

                                    } else {
                                        sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                        sweetAlertDialog.setTitleText("Failed");
                                        sweetAlertDialog.setContentText("you have already rewarded, come back tomorrow ");
                                        sweetAlertDialog.setConfirmButton("Ok", null);
                                        sweetAlertDialog.show();
                                    }

                                } catch (ParseException e) {
                                    e.printStackTrace();
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            } else {
                                sweetAlertDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                                sweetAlertDialog.setTitleText("System Busy");
                                sweetAlertDialog.setContentText("System is busy, please try again later");
                                sweetAlertDialog.setConfirmButton("Dismiss", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                });
                                sweetAlertDialog.show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Toast.makeText(MainActivity.this, "Error : " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });

        }else {
            Toast.makeText(this, "Please Check your Internet ", Toast.LENGTH_SHORT).show();
        }
    }

    class isInternetActive extends AsyncTask<Void,Void,String>{

        @Override
        protected String doInBackground(Void... voids) {

            InputStream inputStream=null;
            String json="";


            try {

                String strUrl="https://icons.iconarchive.com/icons/martz90/circle/256/android-icon.png";
                URL url=new URL(strUrl);

                URLConnection urlConnection=url.openConnection();
                urlConnection.setDoOutput(true);

                inputStream=urlConnection.getInputStream();
                json="success";

            }catch (Exception e)
            {
              e.printStackTrace();
              json="failed";
            }
            return json;
        }

        @Override
        protected void onPostExecute(String s) {
         //   super.onPostExecute(s);

            if(s!=null)
            {

                if (s.equals("success"))
                {
                    Toast.makeText(MainActivity.this, "Internet Connected", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "No Internet", Toast.LENGTH_SHORT).show();
                }
                
            }else {
                Toast.makeText(MainActivity.this, "No internet", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(MainActivity.this, "Validating internet", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }
    }
}
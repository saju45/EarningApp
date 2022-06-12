package com.example.earningapp.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.earningapp.Model.ProfileModel;

import com.example.earningapp.databinding.ActivityInviteBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class InviteActivity extends AppCompatActivity {

    ActivityInviteBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private String oppositeUid;

    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityInviteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firebaseAuth=FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();

        reference= FirebaseDatabase.getInstance().getReference().child("users");

        loadData();
       redeemAvaility();

        binding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String referCode=binding.referCode.getText().toString();
                String shareBody="Hey, i'm using the best earning app . Join using my invite code to instantly get 100 "+

                        "coins . My invite code is "+referCode+"\n"+
                        "Download from play store \n"+
                        "https://play.google.com/store/apps/details?id="+
                        getPackageName();

                Intent intent=new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(intent);
            }
        });

        binding.redeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText=new EditText(InviteActivity.this);
                editText.setHint("abc12");

                LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                editText.setLayoutParams(layoutParams);

                AlertDialog.Builder builder=new AlertDialog.Builder(InviteActivity.this);

                builder.setTitle("Redeem code");
                builder.setView(editText);

                builder.setPositiveButton("Redeem", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String inputCode=editText.getText().toString();

                        if (TextUtils.isEmpty(inputCode))
                        {
                            Toast.makeText(InviteActivity.this, "Input valid code", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (inputCode.equals(binding.referCode.getText().toString()))
                        {
                            Toast.makeText(InviteActivity.this, "you can not input your own code", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        redeemQuery(inputCode, builder);

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                });
                builder.show();
            }
        });

    }

    public void redeemAvaility()
    {

        reference.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists() && snapshot.hasChild("redeemed")) {

                            boolean isAvailabe = snapshot.child("redeemed").getValue(Boolean.class);

                            if (isAvailabe) {
                                binding.redeemBtn.setVisibility(View.GONE);
                                binding.redeemBtn.setEnabled(false);
                            } else {
                                binding.redeemBtn.setEnabled(true);
                                binding.redeemBtn.setVisibility(View.VISIBLE);
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void loadData()
    {

        reference.child(user.getUid())
               .addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {

                       String referCode=snapshot.child("referCode").getValue(String.class);
                       binding.referCode.setText(referCode);

                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                       Toast.makeText(InviteActivity.this, "Error"+error.getMessage(),
                               Toast.LENGTH_SHORT).show();
                       finish();
                   }
               });


    }

    public void redeemQuery(String inputCode, final AlertDialog.Builder dialog)
    {

        Query query=reference.orderByChild("referCode").equalTo(inputCode);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    oppositeUid=dataSnapshot.getKey();

                    reference
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                  ProfileModel model=snapshot.child(oppositeUid).getValue(ProfileModel.class);
                                   // ProfileModel model=snapshot.getValue(ProfileModel.class);
                                    ProfileModel myModel=snapshot.child(user.getUid()).getValue(ProfileModel.class);

                                    int coins= model.getCoins();
                                    int updateCoins=coins+100;

                                    int myCoins=myModel.getCoins();
                                    int myUpdate=myCoins+100;

                                    HashMap<String,Object> map=new HashMap<>();
                                    map.put("coins",updateCoins);

                                     HashMap<String,Object> myMap=new HashMap<>();
                                    myMap.put("coins",myUpdate);
                                    myMap.put("redeemed",true);


                                  //  reference.child(oppositeUid).updateChildren(map);
                                    reference.child(oppositeUid).updateChildren(map);
                                    reference.child(user.getUid()).updateChildren(myMap)

                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                  //  dialog.dismiss();
                                                    Toast.makeText(InviteActivity.this, "congrats", Toast.LENGTH_SHORT).show();
                                                }
                                            });


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(InviteActivity.this, "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }
}
package com.example.earningapp.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.earningapp.Model.ProfileModel;
import com.example.earningapp.Model.WheelView;
import com.example.earningapp.R;
import com.example.earningapp.Spin.SpinItem;
import com.example.earningapp.databinding.FragmentLuckySpinBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class LuckySpin extends Fragment {

    FragmentLuckySpinBinding binding;
    ArrayList<SpinItem> spinItemList;
    private FirebaseUser firebaseUser;
    DatabaseReference reference;
    int currentSpins;
    public LuckySpin() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLuckySpinBinding.inflate(inflater, container, false);

        spinItemList=new ArrayList<>();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference().child("users");
        spinList();
        loadData();

        reference.child(firebaseUser.getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {


                                if (snapshot.exists())
                                {
                                    ProfileModel profileModel=snapshot.getValue(ProfileModel.class);
                                    binding.coins.setText(profileModel.getCoins()+"");
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



        binding.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int index = getRandomIndex();


                if (currentSpins>=-1 && currentSpins<3){
                    Toast.makeText(getContext(), "Watch video to get more spin", Toast.LENGTH_SHORT).show();
                }

                if (currentSpins<-1){
                    binding.playBtn.setEnabled(false);
                    binding.playBtn.setAlpha(.4f);
                    Toast.makeText(getContext(), "Watch video to get more spin", Toast.LENGTH_SHORT).show();
                }else {

                    binding.playBtn.setEnabled(false);
                    binding.playBtn.setAlpha(.4f);

                    binding.wheelView.startWheelWithTargetIndex(index);

                }


            }
        });
        return  binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }




    public void spinList(){

        SpinItem item1=new SpinItem();
        item1.text="0";
        item1.color=0xffFFF3E0;
        spinItemList.add(item1);

        SpinItem item2=new SpinItem();
        item2.text="5";
        item2.color=0xffFFE0B2;
        spinItemList.add(item2);

        SpinItem item3=new SpinItem();
        item3.text="3";
        item3.color=0xffFFCC80;
        spinItemList.add(item3);

        SpinItem item4=new SpinItem();
        item4.text="8";
        item4.color=0xffFFE002;
        spinItemList.add(item4);

        SpinItem item5=new SpinItem();
        item5.text="7";
        item5.color=0xffFFF3E0;
        spinItemList.add(item5);

        SpinItem item6=new SpinItem();
        item6.text="15";
        item6.color=0xffFFCC80;
        spinItemList.add(item6);

        SpinItem item7=new SpinItem();
        item7.text="10";
        item7.color=0xffFFF3E0;
        spinItemList.add(item7);

       SpinItem item8=new SpinItem();
        item8.text="7";
        item8.color=0xffFFE0B2;
        spinItemList.add(item8);

        SpinItem item9=new SpinItem();
        item9.text="9";
        item9.color=0xffFFCC80;
        spinItemList.add(item9);

        SpinItem item10=new SpinItem();
        item10.text="5";
        item10.color=0xffFFCC80;
        spinItemList.add(item10);

        SpinItem item11=new SpinItem();
        item11.text="11";
        item11.color=0xffFFE0B2;
        spinItemList.add(item11);

        SpinItem item12=new SpinItem();
        item12.text="20";
        item12.color=0xffFFCC80;
        spinItemList.add(item12);

        binding.wheelView.setData(spinItemList);
        binding.wheelView.setRound(getRandCircleRound());

        binding.wheelView.LuckyRoundItemSelectedListener(new WheelView.LuckyRoundItemSelectedListener() {
            @Override
            public void LuckyRoundItemSelectedListener(int index) {

                binding.playBtn.setEnabled(true);
                binding.playBtn.setAlpha(1f);

                String value=spinItemList.get(index-1).text;

                updateDataFirebase(Integer.parseInt(value));
            }
        });

    }


    private int getRandomIndex(){
        int [] index=new int[]{1,1,1,1,2,2,2,2,2,2,3,3,3,3,3,3,4,4,4,4,5,5,5,6,6,7,7,9,9,10,11,12};

        int random=new Random().nextInt(index.length);

        return index[random];
    }
    private int getRandCircleRound(){

        Random random=new Random();
        return random.nextInt(10)+15;
    }


    public void loadData(){

        reference.child(firebaseUser.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        ProfileModel profileModel=snapshot.getValue(ProfileModel.class);
                        currentSpins=profileModel.getSpins();
                        String currentSpin="Spin The Wheel "+currentSpins+"";
                        binding.playBtn.setText(currentSpin);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error : "+error.getMessage(), Toast.LENGTH_SHORT).show();

                        if (getActivity()!=null)
                        getActivity().finish();
                    }
                });
    }

    public void updateDataFirebase(int reward){

        int currentCoins= Integer.parseInt(binding.coins.getText().toString());
        int updateCoins=currentCoins+reward;
        int updateSpin=currentSpins-1;

        HashMap<String,Object> map=new HashMap<>();
        map.put("coins",updateCoins);
        map.put("spins",updateSpin);

        reference.child(firebaseUser.getUid())
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        
                        if (task.isSuccessful())
                        {
                            Toast.makeText(getContext(), "coins added", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(getContext(), "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        
                    }
                });

    }
}
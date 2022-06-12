package com.example.earningapp.Fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.example.earningapp.R;
import com.example.earningapp.databinding.ActivityFragmentReplacerBinding;

public class FragmentReplacerActivity extends AppCompatActivity {

    ActivityFragmentReplacerBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityFragmentReplacerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);



       int position=getIntent().getIntExtra("position",0);

       if (position==1)
       {
           fragmentReplacer(new AmazonFragment());
       }


    }

    public void fragmentReplacer(Fragment fragment)
    {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        fragmentTransaction.replace(binding.frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
}
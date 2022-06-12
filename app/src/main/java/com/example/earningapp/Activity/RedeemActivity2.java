package com.example.earningapp.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.earningapp.Fragments.AmazonFragment;
import com.example.earningapp.Fragments.FragmentReplacerActivity;
import com.example.earningapp.R;
import com.example.earningapp.databinding.ActivityRedeem2Binding;

public class RedeemActivity2 extends AppCompatActivity {

    ActivityRedeem2Binding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRedeem2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        loadImage();
        binding.amazonGiftCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(RedeemActivity2.this, FragmentReplacerActivity.class);
                intent.putExtra("position",1);
                startActivity(intent);
            }
        });
    }

    private void loadImage()
    {

        String amazonGiftImageUrl="https://www.google.com/imgres?imgurl=https%3A%2F%2Fpng.pngitem.com%2Fpimgs%2Fs%2F186-1864356_amazon-500-gift-card-get-a-250-amazon.png&imgrefurl=https%3A%2F%2Fwww.pngitem.com%2Fso%2Famazon-gift-card%2F&tbnid=awVokjJYS-Z7KM&vet=12ahUKEwi-usPXsZ74AhWe_jgGHVEMCJUQMyhEegQIARBF..i&docid=_-MiJzNOa4CECM&w=489&h=280&q=amazon%20gift%20card&ved=2ahUKEwi-usPXsZ74AhWe_jgGHVEMCJUQMyhEegQIARBF";

        Glide.with(RedeemActivity2.this)
                .load(amazonGiftImageUrl)
                .into(binding.amazonGiftImage);
    }
}
package com.example.earningapp.Model;

public class ProfileModel {

   private String name,email,profileImg;

    private int coins, spins;


    public ProfileModel() {
    }

    public ProfileModel(String name, String email,String profileImg, int coins,int spins) {
       this.profileImg=profileImg;
        this.name = name;
        this.email = email;
        this.coins = coins;
        this.spins =spins;
    }

    public String getName() {
        return name;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getSpins() {
        return spins;
    }

    public void setSpins(int spins) {
        this.spins = spins;
    }
}

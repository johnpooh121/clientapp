package com.example.clientapp;

public class RankingItem {
    String name;
    String ranking;
    int image;

    public RankingItem(int image, String name, String ranking) {
        this.name = name;
        this.ranking= ranking;
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public String getRanking() {
        return ranking;
    }

    public String getName() {
        return name;
    }

    public void setRank(String ranking) {
        this.ranking = ranking;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(int image) {
        this.image = image;
    }
}

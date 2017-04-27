package com.getfriendlistwarframetest;

/**
 * Created by Chocobouc on 19/04/2017.
 */

public class Player {
    int cipherSolved = 0;
    int totalIncome = 0;
    int itemCrafted = 0;
    int revivedBuddy = 0;
    int soldItems = 0;
    int upgradesReceived = 0;
    int playTime = 0;
    String name="";
    String urlImage="";

    public void setUrlImage(String urlImage) {
        this.urlImage = urlImage;
    }

    public String getUrlImage() {
        return urlImage;
    }

    public int getCipherSolved() {
        return cipherSolved;
    }

    public void setCipherSolved(int cipherSolved) {
        this.cipherSolved = cipherSolved;
    }

    public int getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(int totalIncome) {
        this.totalIncome = totalIncome;
    }

    public int getItemCrafted() {
        return itemCrafted;
    }

    public void setItemCrafted(int itemCrafted) {
        this.itemCrafted = itemCrafted;
    }

    public int getRevivedBuddy() {
        return revivedBuddy;
    }

    public void setRevivedBuddy(int revivedBuddy) {
        this.revivedBuddy = revivedBuddy;
    }

    public int getSoldItems() {
        return soldItems;
    }

    public void setSoldItems(int soldItems) {
        this.soldItems = soldItems;
    }

    public int getUpgradesReceived() {
        return upgradesReceived;
    }

    public void setUpgradesReceived(int upgradesReceived) {
        this.upgradesReceived = upgradesReceived;
    }

    public int getPlayTime() {
        return playTime;
    }

    public void setPlayTime(int playTime) {
        this.playTime = playTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString()
    {
        return "[Player:"+name+" | CIPHER_SOLVED:"+cipherSolved+" | INCOME:"+totalIncome+" | ITEM_CRAFTED:"+itemCrafted+" | REVIVE_BUDDY:"+revivedBuddy+" | SELL_ITEM:"+soldItems+" | UPGRADE_RECEIVED:"+upgradesReceived+" | PLAY_TIME:"+playTime+"]";
    }
}

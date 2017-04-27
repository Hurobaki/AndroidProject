package com.getfriendlistwarframetest;

import java.util.concurrent.TimeUnit;

/**
 * Created by Chocobouc on 25/04/2017.
 */

public class AlerteWarframe {
    private long activation;
    private long expiry;
    private String timeLeft;
    private String missionType;
    private String faction;
    private int credits;
    private String item;
    private int quantity_item=1;

    public String getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(String timeLeft) {
        this.timeLeft = timeLeft;
    }

    @Override
    public String toString() {

        return "AlerteWarframe{timeLeft=" + timeLeft +
                ", missionType='" + missionType + '\'' +
                ", faction='" + faction + '\'' +
                ", credits=" + credits +
                ", item='" + item + '\'' +
                ", quantity_item=" + quantity_item +
                '}';

    }

    public void calculateTimeLeft()
    {
        timeLeft = String.format("%d min %d sec",
                TimeUnit.MILLISECONDS.toMinutes(expiry-System.currentTimeMillis()),
                TimeUnit.MILLISECONDS.toSeconds(expiry-System.currentTimeMillis())-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(expiry-System.currentTimeMillis()))
        );
    }

    //Because the names are ids, we split each of them into tokens and only keep what is interesting for the user
    public void splitInfosToGetRealName()
    {
        faction=faction.split("_")[1];
        missionType=missionType.split("_")[1];
        if (item!=null) {
            String[] tokens = item.split("/");
            item=tokens[tokens.length-1];
        }

    }


    public long getActivation() {
        return activation;
    }

    public void setActivation(long activation) {
        this.activation = activation;
    }

    public long getExpiry() {
        return expiry;
    }

    public void setExpiry(long expiry) {
        this.expiry = expiry;
    }

    public String getMissionType() {
        return missionType;
    }

    public void setMissionType(String missionType) {
        this.missionType = missionType;
    }

    public String getFaction() {
        return faction;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public int getQuantity_item() {
        return quantity_item;
    }

    public void setQuantity_item(int quantity_item) {
        this.quantity_item = quantity_item;
    }
}

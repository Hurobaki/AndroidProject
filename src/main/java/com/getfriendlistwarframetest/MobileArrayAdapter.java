package com.getfriendlistwarframetest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by Chocobouc on 23/04/2017.
 */

public class MobileArrayAdapter extends BaseAdapter {
    private HashMap<String,Player> players;
    private String[] steamIds;
    private String currentPlayer;
    private Context mContext;

    public MobileArrayAdapter(Context mContext, HashMap<String, Player> players, String currentPlayer) {
        this.players = players;
        this.steamIds = players.keySet().toArray(new String [players.size()]);
        this.currentPlayer = currentPlayer;
        this.mContext=mContext;
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Object getItem(int i) {
        return players.get(steamIds[i]);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String key = steamIds[i];
        String value = getItem(i).toString();
        final View result;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null) {
            result = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_friend, viewGroup, false);
        } else {
            result = view;
        }

        // seconds to hours
        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = ((Player)getItem(i)).getPlayTime() % SECONDS_IN_A_MINUTE;
        int totalMinutes = ((Player)getItem(i)).getPlayTime() / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;


        if (currentPlayer.equals(((Player)getItem(i)).getName()))
            ((TextView) result.findViewById(R.id.label)).setText(((Player)getItem(i)).getName()+ " (Searched)");
        else
            ((TextView) result.findViewById(R.id.label)).setText(((Player)getItem(i)).getName());

        ((TextView) result.findViewById(R.id.cipher)).setText("Cipher resolved : "+((Player)getItem(i)).getCipherSolved());
        ((TextView) result.findViewById(R.id.income)).setText("Money earned : "+((Player)getItem(i)).getTotalIncome());
        ((TextView) result.findViewById(R.id.crafted)).setText("Item crafted : "+((Player)getItem(i)).getItemCrafted());
        ((TextView) result.findViewById(R.id.revived)).setText("Revived buddies : "+((Player)getItem(i)).getRevivedBuddy());
        ((TextView) result.findViewById(R.id.sold)).setText("Worth of sold items : "+((Player)getItem(i)).getSoldItems());
        ((TextView) result.findViewById(R.id.upgrades)).setText("Upgrades received : "+((Player)getItem(i)).getUpgradesReceived());
        ((TextView) result.findViewById(R.id.playtime)).setText("Total playtime : "+hours+"h"+minutes+"min");

        new DownloadImageTask((ImageView) result.findViewById(R.id.logo)).execute(((Player)getItem(i)).getUrlImage());

        return result;
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
                mIcon11 = Bitmap.createScaledBitmap(mIcon11, 400, 400, true);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
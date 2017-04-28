package com.getfriendlistwarframetest;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class FriendList extends ListActivity {

    private final String API_KEY = "73D6112D6AF7221BA277C1CD617A8C6C";
    //Warframe ID in steam database
    private final String GAME_ID = "230410";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);
        Bundle extras = getIntent().getExtras();
        String steamId = (String) extras.get("steamId");
        new GetSteamFriends(steamId).execute(steamId, API_KEY, GAME_ID);
    }

    public void listFriends(HashMap<String, Player> hm, String currentPlayer)
    {
        setListAdapter(new MobileArrayAdapter(this, hm, currentPlayer));
    }

    class GetSteamFriends extends AsyncTask<String, Void, Void> {

        private boolean flagSuccess=false;
        private HashMap<String, Player> friends;
        private String mainSteamId;

        private LinearLayout layout;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layout = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
            layout.setVisibility(View.VISIBLE);
        }

        public GetSteamFriends(String str)
        {
            mainSteamId=str;
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {

                // First call to get the friend list of the steam id entered
                URL url = new URL("http://api.steampowered.com/ISteamUser/GetFriendList/v0001/?key=" + strings[1] + "&steamid=" + strings[0] + "&relationship=friend");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // If the steam id is correct, carry on by setting the flag on true
                if (urlConnection.getResponseCode()==200) {
                    flagSuccess=true;
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();

                    // We create a map of player to enter the data received
                    friends = new HashMap<String, Player>();
                    JSONObject json = new JSONObject(stringBuilder.toString());
                    JSONArray jsonArray = new JSONArray(json.getJSONObject("friendslist").getJSONArray("friends").toString());
                    JSONObject researchId = new JSONObject();
                    researchId.put("steamid", strings[0]);
                    jsonArray.put(researchId);
                    String chainedIdsForGetPseudo = "";

                    // For each players, we want their warframe game stat, we access it with the game id and the steam id of the friends
                    for (int i = 0; i < jsonArray.length(); ++i) {
                        String friendId = jsonArray.getJSONObject(i).getString("steamid").toString();
                        try {
                            url = new URL("http://api.steampowered.com/ISteamUserStats/GetUserStatsForGame/v0002/?appid=" + strings[2] + "&key=" + strings[1] + "&steamid=" + friendId);
                            urlConnection = (HttpURLConnection) url.openConnection();
                            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            stringBuilder = new StringBuilder();
                            while ((line = bufferedReader.readLine()) != null) {
                                stringBuilder.append(line).append("\n");
                            }
                            bufferedReader.close();
                            json = new JSONObject(stringBuilder.toString());

                            // The stats can either be totally void or an empty json object : {}
                            if (json.getJSONObject("playerstats").has("stats")) {
                                JSONArray stats = json.getJSONObject("playerstats").getJSONArray("stats");
                                Player p = new Player();

                                // For each players, we retreive all the datas from the json
                                for (int j = 0; j < stats.length(); ++j) {
                                    switch (stats.getJSONObject(j).getString("name")) {
                                        case "CIPHER_SOLVED":
                                            p.setCipherSolved(stats.getJSONObject(j).getInt("value"));
                                            break;
                                        case "INCOME":
                                            p.setTotalIncome(stats.getJSONObject(j).getInt("value"));
                                            break;
                                        case "ITEM_CRAFTED":
                                            p.setItemCrafted(stats.getJSONObject(j).getInt("value"));
                                            break;
                                        case "REVIVE_BUDDY":
                                            p.setRevivedBuddy(stats.getJSONObject(j).getInt("value"));
                                            break;
                                        case "SELL_ITEM":
                                            p.setSoldItems(stats.getJSONObject(j).getInt("value"));
                                            break;
                                        case "UPGRADE_RECEIVED":
                                            p.setUpgradesReceived(stats.getJSONObject(j).getInt("value"));
                                            break;
                                        case "PLAY_TIME":
                                            p.setPlayTime(stats.getJSONObject(j).getInt("value"));
                                            break;
                                    }
                                }
                                friends.put(friendId, p);
                                chainedIdsForGetPseudo += friendId + ",";
                            }
                        } catch (FileNotFoundException e) {
                            Log.v("ErrorMsg", "Friend " + friendId + " not playing warframe");
                        }

                    }
                    // We chain steam id with comma to get the nicknames and the images of the friends
                    if (chainedIdsForGetPseudo != "") {
                        chainedIdsForGetPseudo = chainedIdsForGetPseudo.substring(0, chainedIdsForGetPseudo.length() - 1);

                        //We can chain the steam ids to get them all at once
                        url = new URL("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=73D6112D6AF7221BA277C1CD617A8C6C&steamids=" + chainedIdsForGetPseudo);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        stringBuilder = new StringBuilder();
                        while ((line = bufferedReader.readLine()) != null) {
                            stringBuilder.append(line).append("\n");
                        }
                        bufferedReader.close();
                        json = new JSONObject(stringBuilder.toString());
                        JSONArray friendsArray = new JSONArray(json.getJSONObject("response").getJSONArray("players").toString());

                        // For each friends, we put the datas received in the player object
                        for (int k = 0; k < friendsArray.length(); ++k) {
                            String steamId = friendsArray.getJSONObject(k).getString("steamid");
                            String pseudo = friendsArray.getJSONObject(k).getString("personaname");
                            String urlImage = friendsArray.getJSONObject(k).getString("avatarfull");
                            friends.get(steamId).setName(pseudo);
                            friends.get(steamId).setUrlImage(urlImage);
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // if the steam id is wrong, toast error and launch research activity
            if(!flagSuccess)
            {
                Toast.makeText(getApplicationContext(), "STEAMID not found", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
            // else, list the friends
            else
            {
                if (friends.get(mainSteamId)!=null)
                    listFriends(friends, friends.get(mainSteamId).getName());
                else
                    listFriends(friends, "");
            }

            layout.setVisibility(View.GONE);
        }
    }
}


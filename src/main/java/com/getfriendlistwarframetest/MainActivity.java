package com.getfriendlistwarframetest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import org.json.JSONArray;
import org.json.JSONObject;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<AlerteWarframe> alerts = new ArrayList<AlerteWarframe>();
    private SharedPreferences sharedPref;
    private RecyclerView recyclerView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void onGoToAlerts(MenuItem mi) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
    public void onGoToFriends(MenuItem mi) {
        startActivity(new Intent(getApplicationContext(), LookForFriends.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        new getAlerts().execute("");
    }
    class getAlerts extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {

                // First call to get the friend list of the steam id entered
                URL url = new URL("http://content.warframe.com/dynamic/worldState.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                JSONObject json = new JSONObject(stringBuilder.toString());
                JSONArray jsonArray = new JSONArray(json.getJSONArray("Alerts").toString());
                Log.v("array",jsonArray.toString());
                for (int i = 0; i < jsonArray.length(); ++i)
                {
                    JSONObject currentObject = (JSONObject) jsonArray.get(i);
                    AlerteWarframe a = new AlerteWarframe();
                    a.setActivation(currentObject.getJSONObject("Activation").getJSONObject("$date").getLong("$numberLong"));
                    a.setExpiry(currentObject.getJSONObject("Expiry").getJSONObject("$date").getLong("$numberLong"));
                    a.setMissionType(currentObject.getJSONObject("MissionInfo").getString("missionType"));
                    a.setFaction(currentObject.getJSONObject("MissionInfo").getString("faction"));
                    a.setCredits(currentObject.getJSONObject("MissionInfo").getJSONObject("missionReward").getInt("credits"));
                    if (currentObject.getJSONObject("MissionInfo").getJSONObject("missionReward").has("items"))
                    {
                        switch (currentObject.getJSONObject("MissionInfo").getJSONObject("missionReward").getJSONArray("items").getString(0)) {
                            case "/Lotus/StoreItems/Upgrades/Mods/FusionBundles/AlertFusionBundleSmall":
                                a.setItem("endo");
                                a.setQuantity_item(50);
                                break;
                            case "/Lotus/StoreItems/Upgrades/Mods/FusionBundles/AlertFusionBundleMedium":
                                a.setItem("endo");
                                a.setQuantity_item(100);
                                break;
                            case "/Lotus/StoreItems/Upgrades/Mods/FusionBundles/AlertFusionBundleLarge":
                                a.setItem("endo");
                                a.setQuantity_item(150);
                                break;
                            default:
                                a.setItem(currentObject.getJSONObject("MissionInfo").getJSONObject("missionReward").getJSONArray("items").getString(0));
                                break;
                        }
                    }
                    else
                    {
                        if (currentObject.getJSONObject("MissionInfo").getJSONObject("missionReward").has("countedItems")) {
                            a.setItem(currentObject.getJSONObject("MissionInfo").getJSONObject("missionReward").getJSONArray("countedItems").getJSONObject(0).getString("ItemType"));
                            a.setQuantity_item(currentObject.getJSONObject("MissionInfo").getJSONObject("missionReward").getJSONArray("countedItems").getJSONObject(0).getInt("ItemCount"));
                        }
                    }
                    a.calculateTimeLeft();
                    a.splitInfosToGetRealName();
                    alerts.add(a);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

            AlertAdapter mAdapter = new AlertAdapter(alerts);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);

        }
    }
}

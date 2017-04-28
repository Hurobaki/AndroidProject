package com.getfriendlistwarframetest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
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
    private RecyclerView recyclerView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // menu icons onclick
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
        new getAlerts().execute();

        //handler to check if any new missions was issued or expired
        handler.post(runnableCode);
    }

    private Handler handler = new Handler();
    private int nbrMission=0;

    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
            if (nbrMission<alerts.size())
            {
                Notification n  = new Notification.Builder(getApplicationContext())
                        .setContentTitle("Warframe OP")
                        .setContentText("New mission issued")
                        .setSmallIcon(R.drawable.ic_alerts)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true).build();
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, n);
            }
            if (nbrMission>alerts.size())
            {
                Notification n  = new Notification.Builder(getApplicationContext())
                        .setContentTitle("Warframe OP")
                        .setContentText("Mission timed out")
                        .setSmallIcon(R.drawable.ic_alerts)
                        .setContentIntent(pIntent)
                        .setAutoCancel(true).build();
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, n);
            }
            handler.postDelayed(runnableCode, 2000);
        }
    };

    class getAlerts extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                // We make the call to the warframe API
                URL url = new URL("http://content.warframe.com/dynamic/worldState.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();

                // This json object contains a lot of informations
                JSONObject json = new JSONObject(stringBuilder.toString());
                JSONArray jsonArray = new JSONArray(json.getJSONArray("Alerts").toString());

                // for each object in the alerts array we create an AlertWarframe that we put in the arrayList
                for (int i = 0; i < jsonArray.length(); ++i)
                {
                    JSONObject currentObject = (JSONObject) jsonArray.get(i);
                    AlerteWarframe a = new AlerteWarframe();
                    a.setActivation(currentObject.getJSONObject("Activation").getJSONObject("$date").getLong("$numberLong"));
                    a.setExpiry(currentObject.getJSONObject("Expiry").getJSONObject("$date").getLong("$numberLong"));
                    a.setMissionType(currentObject.getJSONObject("MissionInfo").getString("missionType"));
                    a.setFaction(currentObject.getJSONObject("MissionInfo").getString("faction"));
                    a.setCredits(currentObject.getJSONObject("MissionInfo").getJSONObject("missionReward").getInt("credits"));

                    // most of the items names are ids so we looked on internet to find which id is which object
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
                    // As said before, most of the items are ids (ex:"Lotus/StoreItems/Upgrades/Mods/FusionBundles/AlertFusionBundleLarge", so we only keep the last leaf of the id path
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

            // We inflate the recycle view with the informations received
            recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
            AlertAdapter mAdapter = new AlertAdapter(alerts);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(mAdapter);
            // Update of the number to check if there is new mission or expired ones
            nbrMission=alerts.size();
        }
    }
}

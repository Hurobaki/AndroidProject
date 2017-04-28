package com.getfriendlistwarframetest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LookForFriends extends AppCompatActivity {
    private SharedPreferences sharedPref;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu icons
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
        // menu icons on click
    public void onGoToAlerts(MenuItem mi) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }
    public void onGoToFriends(MenuItem mi) {
        startActivity(new Intent(getApplicationContext(), LookForFriends.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_for_friends);
        // We store the last entered steam id in the preferences
        final EditText steamId = (EditText) findViewById(R.id.steamId);
        sharedPref = getSharedPreferences("myPref", Context.MODE_PRIVATE);
        steamId.setText(sharedPref.getString("last_search",""), TextView.BufferType.EDITABLE);
        Button findFriends = (Button) findViewById(R.id.buttonFriend);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        findFriends.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor;
                sharedPref= getSharedPreferences("myPref", Context.MODE_PRIVATE);
                editor=sharedPref.edit();
                editor.putString("last_search", steamId.getText().toString());
                editor.commit();
                Intent i = new Intent(getApplicationContext(), FriendList.class);
                i.putExtra("steamId",steamId.getText().toString());
                startActivity(i);
            }
        });
    }

}

package com.getfriendlistwarframetest;

import android.os.CountDownTimer;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created by Chocobouc on 27/04/2017.
 */

public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.MyViewHolder>{
    private ArrayList<AlerteWarframe> alerts;

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        AlerteWarframe alert = alerts.get(position);
        holder.credits.setText("Credits:"+alert.getCredits());
        holder.faction.setText("Faction:"+alert.getFaction());
        holder.timeLeft.setText("Time left:"+alert.getTimeLeft());

        new CountDownTimer(alert.getTimeLeft(), 1000) {

            public void onTick(long millisUntilFinished) {
                holder.timeLeft.setText(""+String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }

            public void onFinish() {
                holder.timeLeft.setText("done!");
            }
        }.start();

        holder.missionType.setText("Mission:"+alert.getMissionType());
        if (alert.getItem()!=null)
            holder.reward.setText("Reward: "+alert.getItem()+" x "+alert.getQuantity_item());
        else
            holder.reward.setText("No reward");
    }

    @Override
    public int getItemCount() {
        return alerts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView timeLeft, faction, missionType, credits, reward;

        public MyViewHolder(View view) {
            super(view);
            timeLeft = (TextView) view.findViewById(R.id.timeLeft);
            missionType = (TextView) view.findViewById(R.id.missionType);
            faction = (TextView) view.findViewById(R.id.faction);
            credits = (TextView) view.findViewById(R.id.credits);
            reward = (TextView) view.findViewById(R.id.reward);
        }

    }
    public AlertAdapter(ArrayList<AlerteWarframe> alerts)
    {
        this.alerts = alerts;
    }
}

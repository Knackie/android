package com.example.projetamio.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projetamio.R;
import com.example.projetamio.datamanagement.DonneesLampe;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class LampAdapter extends RecyclerView.Adapter<LampAdapter.LampViewHolder> {
    private List<DonneesLampe> mData;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    LampAdapter(Context context, List<DonneesLampe> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public LampViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_view_item, parent, false);
        return new LampViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(LampViewHolder holder, int position) {
        // Manage the view of the recyclerView
        DonneesLampe donneesLampe = mData.get(position);
        holder.myTextView.setText(donneesLampe.getNom());
        holder.mTemperature.setText("Température = " + donneesLampe.getLastTemperature().toString());
        if (donneesLampe.isAllume()) {
            // Check the state of lamp, and show image with state
            holder.mLampeStatus.setImageResource(R.drawable.light_on);
        } else {
            holder.mLampeStatus.setImageResource(R.drawable.light_off);
        }
        holder.mHumidite.setText("Humidité = " + donneesLampe.getLastHumidity() + "");
        holder.mBattery.setText("Batterie = " + donneesLampe.getLastBattery() + "");
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class LampViewHolder extends RecyclerView.ViewHolder {
        // Here is define some text and image for the recyclerView
        TextView myTextView;
        TextView mTemperature;
        TextView mHumidite;
        TextView mBattery;
        ImageView mLampeStatus;

        LampViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvCapteur);
            mTemperature = itemView.findViewById(R.id.tvTemperature);
            mLampeStatus = itemView.findViewById(R.id.tvLamp);
            mHumidite = itemView.findViewById(R.id.tvHumidity);
            mBattery = itemView.findViewById(R.id.tvBattery);
        }
    }
}

package com.example.projetamio.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.projetamio.R;
import com.example.projetamio.datamanagement.DonneesLampe;

import java.util.List;

public class LampAdapter extends RecyclerView.Adapter<LampAdapter.LampViewHolder> {
    private List<DonneesLampe> mData;
    private LayoutInflater mInflater;

    /**
     * Constructeur de la classe LampAdapter
     * @param context Context d'appel des données
     * @param data Les données à afficher
     */
    LampAdapter(Context context, List<DonneesLampe> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed

    /**
     * Fonction permettant d'intialiser la vue avec le bon fichier xmk
     * @param parent
     * @param viewType
     * @return La liste des données sous forme LampViewHolder
     */
    @Override
    public LampViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_view_item, parent, false);
        return new LampViewHolder(view);
    }

    // binds the data to the TextView in each row

    /**
     * Fonction permettant d'associer les données au différents emplacements prévu dans la vue
     * @param holder Liste des données présentes
     * @param position Numéro indiquant la position de la ligne en cours d'édition
     */
    @Override
    public void onBindViewHolder(LampViewHolder holder, int position) {
        // Manage the view of the recyclerView
        DonneesLampe donneesLampe = mData.get(position);
        holder.myTextView.setText(donneesLampe.getEmplaceemnt());
        String labelTemperature = mInflater.getContext().getString(R.string.temperature);
        holder.mTemperature.setText(String.format("%s = %s °C", labelTemperature, donneesLampe.getLastTemperature()));
        if (donneesLampe.isAllume()) {
            // Check the state of lamp, and show image with state
            holder.mLampeStatus.setImageResource(R.drawable.light_on);
        } else {
            holder.mLampeStatus.setImageResource(R.drawable.light_off);
        }
        String labelHumidite = mInflater.getContext().getString(R.string.humidite);
        holder.mHumidite.setText(String.format("%s = %s %%", labelHumidite, donneesLampe.getLastHumidity()));
        String labelBatterie = mInflater.getContext().getString(R.string.batterylabel);
        holder.mBattery.setText(String.format("%s = %s V", labelBatterie, donneesLampe.getLastBattery()));
    }

    /**
     * Fonction retournant le nombre de lignes
     * @return le nombre de lignes
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off scree

    /**
     * Permet de stocker les différents emplacements
     */
    public class LampViewHolder extends RecyclerView.ViewHolder {
        TextView myTextView;
        TextView mTemperature;
        TextView mHumidite;
        TextView mBattery;
        ImageView mLampeStatus;

        /**
         * Le constructeur permet de récupérer l'ensemble des éléments
         * @param itemView La vue contenant les items à modifier
         */
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

package com.example.projetamio.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.example.projetamio.datamanagement.ListLampe;
import com.example.projetamio.R;

import java.util.ArrayList;

public class ListMoteActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    /**
     * Fonction qui est appelée à la création de la vue
     * @param savedInstanceState Permet de récupérer l'état de la précédente vue
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_mote_layout);
        recyclerView = (RecyclerView) findViewById(R.id.allmoterecyclerview);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        ListLampe lamp = ListLampe.getInstance();

        mAdapter = new LampAdapter(this, lamp.getDonneesLampList());
        recyclerView.setAdapter(mAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * Permet d'effectuer une action lorsque l'on clique sur un item
     * @param item item sur lequel on a appuyé
     * @return Retourne Vrai si l'évènement a été utilisé
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        onBackPressed();
        return true;
    }
}
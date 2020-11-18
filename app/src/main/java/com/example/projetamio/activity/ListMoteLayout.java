package com.example.projetamio.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.projetamio.datamanagement.ListLampe;
import com.example.projetamio.R;

import java.util.ArrayList;

public class ListMoteLayout extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

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

        // specify an adapter (see also next example)
        ListLampe lamp = ListLampe.getInstance();
        ArrayList<String> mdata = new ArrayList<String>(lamp.getLampeName());

        Log.d(this.getClass().getName(), mdata.toString());

        mAdapter = new LampAdapter(this, mdata);
        recyclerView.setAdapter(mAdapter);
    }
    public void onStart(){
        super.onStart();
        Button retour = findViewById(R.id.buttonBack);
        retour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // doStuff
                Intent intentApp = new Intent(ListMoteLayout.this,
                        MainActivity.class);

                ListMoteLayout.this.startActivity(intentApp);

            }
        });
    }
}
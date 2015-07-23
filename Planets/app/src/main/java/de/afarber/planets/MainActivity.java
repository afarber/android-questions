package de.afarber.planets;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private final String[] mPlanets = new String[] {
            "Mercury",
            "Venus",
            "Earth",
            "Mars",
            "Jupiter",
            "Saturn",
            "Uranus",
            "Neptune"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.Adapter adapter = new PlanetAdapter(mPlanets, this);
        mRecyclerView.setAdapter(adapter);
/*
            mRecyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent,
                                    View view,
                                    int position,
                                    long id) {
                Toast.makeText(getApplicationContext(),
                        "You have clicked " + mPlanets[position],
                        Toast.LENGTH_LONG).show();
            }
        });
*/

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
    }
}

class PlanetAdapter extends RecyclerView.Adapter<PlanetAdapter.PlanetViewHolder> {

    ArrayList<String> planetList = new ArrayList<>();

    public PlanetAdapter(String[] planetList, Context context) {
        Collections.addAll(this.planetList, planetList);
    }

    @Override
    public PlanetAdapter.PlanetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        PlanetViewHolder viewHolder = new PlanetViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PlanetAdapter.PlanetViewHolder holder, int position) {
        holder.text1.setText(planetList.get(position).toString());

        holder.text1.setCompoundDrawablePadding(24);
        holder.text1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stars_black_24dp, 0, 0, 0);

    }

    @Override
    public int getItemCount() {
        return planetList.size();
    }

    class PlanetViewHolder extends RecyclerView.ViewHolder {

        protected TextView text1;

        public PlanetViewHolder(View itemView) {
            super(itemView);
            text1 = (TextView) itemView.findViewById(android.R.id.text1);
        }
    }
}

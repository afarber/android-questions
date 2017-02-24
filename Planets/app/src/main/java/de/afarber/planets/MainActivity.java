package de.afarber.planets;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


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

    private class PlanetViewHolder
            extends RecyclerView.ViewHolder {

        public PlanetViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new RecyclerView.Adapter<PlanetViewHolder>() {

            @Override
            public PlanetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                        android.R.layout.simple_list_item_1,
                        parent,
                        false);
                PlanetViewHolder vh = new PlanetViewHolder(v);
                return vh;
            }

            @Override
            public void onBindViewHolder(PlanetViewHolder vh, int position) {
                TextView tv = (TextView) vh.itemView;
                tv.setText(mPlanets[position]);
                tv.setCompoundDrawablePadding(24);
                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stars_black_24dp, 0, 0, 0);
                vh.itemView.setOnClickListener(new View.OnClickListener() {
                  @Override
                   public void onClick(View view) {
                         Toast.makeText(getApplicationContext(),
                          "You have clicked " + mPlanets[position],
                          Toast.LENGTH_LONG).show();
                    }
                });
                    
                   
            }

            @Override
            public int getItemCount() {
                return mPlanets.length;
            }
        });
    }
}

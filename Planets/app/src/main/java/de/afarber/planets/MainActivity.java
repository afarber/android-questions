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
    private class PlanetViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        protected TextView text1;

        public PlanetViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            text1 = (TextView) itemView.findViewById(android.R.id.text1);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(),
                    "You have clicked " + ((TextView) v).getText(),
                    Toast.LENGTH_LONG).show();
        }
    }

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
        mRecyclerView.setAdapter(new RecyclerView.Adapter<PlanetViewHolder>() {

            @Override
            public PlanetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
                PlanetViewHolder viewHolder = new PlanetViewHolder(v);
                return viewHolder;
            }

            @Override
            public void onBindViewHolder(PlanetViewHolder holder, int position) {
                holder.text1.setText(mPlanets[position]);
                holder.text1.setCompoundDrawablePadding(24);
                holder.text1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_stars_black_24dp, 0, 0, 0);
            }

            @Override
            public int getItemCount() {
                return mPlanets.length;
            }
        });
    }
}

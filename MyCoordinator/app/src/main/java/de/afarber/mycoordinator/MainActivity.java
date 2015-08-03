package de.afarber.mycoordinator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements Runnable {
    private static final String[] sItems = new String[] {
            "Item 001",
            "Item 002",
            "Item 003",
            "Item 004",
            "Item 005",
            "Item 006",
            "Item 007",
            "Item 008",
            "Item 009",
            "Item 010",
            "Item 011",
            "Item 012",
            "Item 013",
            "Item 014",
            "Item 015",
            "Item 016",
            "Item 017",
            "Item 018",
            "Item 019",
            "Item 020"
    };

    private int mMutedColor = R.attr.colorPrimary;
    private Handler mHandler = new Handler();
    private CollapsingToolbarLayout mCollapsingToolbar;
    private RecyclerView mRecyclerView;
    private FloatingActionButton mFab;
    private Animation mInAnimation;
    private Animation mOutAnimation;

    private class MyViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public MyViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(),
                    "You have clicked " + ((TextView) v).getText(),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        mCollapsingToolbar.setTitle("Flexible space");

        ImageView header = (ImageView) findViewById(R.id.header_image_view);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.header);
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                mMutedColor = palette.getMutedColor(R.attr.colorPrimary);
                mCollapsingToolbar.setContentScrimColor(mMutedColor);
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new RecyclerView.Adapter<MyViewHolder>() {

            @Override
            public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(
                        android.R.layout.simple_list_item_1,
                        parent,
                        false);
                MyViewHolder vh = new MyViewHolder(v);
                return vh;
            }

            @Override
            public void onBindViewHolder(MyViewHolder vh, int position) {
                TextView tv = (TextView) vh.itemView;
                tv.setText(sItems[position]);
            }

            @Override
            public int getItemCount() {
                return sItems.length;
            }
        });

        mFab = (FloatingActionButton) findViewById(R.id.fab);

        mInAnimation = AnimationUtils.makeInAnimation(this, false);
        mInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
                mFab.setVisibility(View.VISIBLE);
            }
        });

        mOutAnimation = AnimationUtils.makeOutAnimation(this, true);
        mOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationEnd(Animation animation) {
                mFab.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationStart(Animation animation) {
            }
        });

        run();
    }

    @Override
    public void run() {
        Log.d("MyCoordinator", "Toggle animation");

        mFab.startAnimation(mFab.isShown() ? mOutAnimation : mInAnimation);

        mHandler.postDelayed(this, 10000);

    }

    public void showToast(View v) {
        Toast.makeText(getApplicationContext(),
                "FAB clicked",
                Toast.LENGTH_LONG).show();
    }
}


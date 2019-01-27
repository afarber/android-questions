package de.afarber.fasi;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import androidx.annotation.NonNull;

public class MyItem extends AbstractItem<MyItem, MyItem.ViewHolder> {
    public final static String WON = "won";
    public final static String LOST = "lost";

    public boolean details;
    public long stamp;
    public int length;
    public int gid;
    public int score1;
    public int score2;
    public int elo1;
    public int elo2;
    public String state1;
    public String finished;
    public String word;
    public String given1;
    public String given2;
    public String photo1;
    public String photo2;

    public MyItem() { }

    @Override
    public long getIdentifier() {
        return gid;
    }

    @Override
    public int getType() {
        return R.id.my_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.my_item_layout;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MyItem) {
            MyItem other = (MyItem) obj;

            return gid == other.gid &&
                    stamp == other.stamp &&
                    score1 == other.score1 &&
                    score2 == other.score2 &&
                    elo1 == other.elo1 &&
                    elo2 == other.elo2;
        }

        return false;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends FastAdapter.ViewHolder<MyItem> {
        private ImageView mDetails;
        private TextView mGid;
        private TextView mInfo;
        private TextView mScore1;
        private TextView mScore2;
        private TextView mGiven1;
        private TextView mGiven2;
        private TextView mElo1;
        private TextView mElo2;
        private ImageView mPhoto1;
        private ImageView mPhoto2;
        private ImageView mBoard;

        public ViewHolder(View view) {
            super(view);
            mDetails = view.findViewById(R.id.details);
            mGid = view.findViewById(R.id.gid);
            mInfo = view.findViewById(R.id.info);
            mScore1 = view.findViewById(R.id.score1);
            mScore2 = view.findViewById(R.id.score2);
            mGiven1 = view.findViewById(R.id.given1);
            mGiven2 = view.findViewById(R.id.given2);
            mElo1 = view.findViewById(R.id.elo1);
            mElo2 = view.findViewById(R.id.elo2);
            mPhoto1 = view.findViewById(R.id.photo1);
            mPhoto2 = view.findViewById(R.id.photo2);
            mBoard = view.findViewById(R.id.board);
        }

        @Override
        public void bindView(@NonNull MyItem item, @NonNull List<Object> payloads) {
            Context ctx = mDetails.getContext();
            Resources res = mDetails.getResources();

            mDetails.setImageResource(item.details ? R.drawable.minus_circle_gray : R.drawable.plus_circle_gray);
            mBoard.setVisibility(item.details ? View.VISIBLE : View.GONE);

            if (payloads.size() > 0) {
                // when called by onClick, do not update the other views, to prevent flickering
                return;
            }

            // the item is used to display either finished games or the longest words

            if (TextUtils.isEmpty(item.word)) {
                int result = (
                        WON.equals(item.state1) ?
                                R.string.won :
                                (
                                        LOST.equals(item.state1) ?
                                                R.string.lost :
                                                R.string.draw
                                )
                );
                mInfo.setTypeface(Typeface.DEFAULT);
                mInfo.setText(ctx.getString(result, item.finished));
            } else {
                mInfo.setTypeface(Typeface.DEFAULT_BOLD);
                mInfo.setText(item.word);
            }

            mGid.setText(res.getString(R.string.str_game, item.gid));
            mScore1.setText(res.getString(R.string.str_score, item.score1));
            mScore2.setText(res.getString(R.string.str_score, item.score2));
            mGiven1.setText(item.given1);
            mGiven2.setText(item.given2);
            mElo1.setText(String.valueOf(item.elo1));
            mElo2.setText(String.valueOf(item.elo2));
            mPhoto1.setImageResource(R.drawable.account_gray);
            mPhoto2.setImageResource(R.drawable.account_gray);
            mBoard.setImageResource(R.drawable.checkerboard_gray);
        }

        @Override
        public void unbindView(@NonNull MyItem item) {
            mDetails.setImageDrawable(null);
            mGid.setText(null);
            mInfo.setText(null);
            mScore1.setText(null);
            mScore2.setText(null);
            mGiven1.setText(null);
            mGiven2.setText(null);
            mElo1.setText(null);
            mElo2.setText(null);
            mPhoto1.setImageDrawable(null);
            mPhoto2.setImageDrawable(null);
            mBoard.setImageDrawable(null);
        }
    }
}

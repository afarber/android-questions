package de.afarber.topplayers;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class TopItem extends AbstractItem<TopItem, TopItem.ViewHolder> {

    // {"uid":4264,"elo":2467,"avg_time":"01:08","avg_score":16.7,"given":"Андрей",
    // "photo":"https://avt-1.foto.mail.ru/mail/andrej.kudinov.97/_avatarbig?1363079237"},

    public int uid;
    public int elo;
    public String given;
    public String photo;
    public String avg_time;
    public float avg_score;

    public TopItem(TopEntity top) {
        this.uid = top.uid;
        this.elo = top.elo;
        this.given = top.given;
        this.photo = top.photo;
        this.avg_time = top.avg_time;
        this.avg_score = top.avg_score;
    }

    @Override
    public int getType() {
        return R.id.top_item_id;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_top;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends FastAdapter.ViewHolder<TopItem> {
        private TextView mElo;
        private TextView mGiven;
        private TextView mAvgScore;
        private TextView mAvgTime;
        private ImageView mPhoto;

        public ViewHolder(View view) {
            super(view);
            mElo = view.findViewById(R.id.elo);
            mGiven = view.findViewById(R.id.given);
            mAvgScore = view.findViewById(R.id.avg_score);
            mAvgTime = view.findViewById(R.id.avg_time);
            mPhoto = view.findViewById(R.id.photo);
        }

        @Override
        public void bindView(@NonNull TopItem item, @NonNull List<Object> payloads) {
            Resources res = mElo.getResources();
            
            mGiven.setText(item.given);
            mElo.setText(String.valueOf(item.elo));
            mAvgScore.setText(res.getString(R.string.avg_score, item.avg_score));
            mAvgTime.setText(res.getString(R.string.avg_time, item.avg_time));

            if (URLUtil.isHttpsUrl(item.photo)) {
                MainActivity.getPicasso()
                        .load(item.photo)
                        .placeholder(R.drawable.account_gray)
                        .fit()
                        .centerInside()
                        .noFade()
                        .into(mPhoto);
            } else {
                mPhoto.setImageResource(R.drawable.account_gray);
            }
        }

        @Override
        public void unbindView(@NonNull TopItem item) {
            MainActivity.getPicasso().cancelRequest(mPhoto);

            mElo.setText(null);
            mGiven.setText(null);
            mAvgScore.setText(null);
            mAvgTime.setText(null);
            mPhoto.setImageDrawable(null);
        }
    }
}

package de.afarber.topplayers;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

public class DiffCallback extends DiffUtil.Callback {
    private final List<TopItem> mOldList;
    private final List<TopItem> mNewList;

    public DiffCallback(List<TopItem> oldStudentList, List<TopItem> newStudentList) {
        this.mOldList = oldStudentList;
        this.mNewList = newStudentList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        TopItem oldItem = mOldList.get(oldItemPosition);
        TopItem newItem = mNewList.get(newItemPosition);

        return oldItem.uid == newItem.uid;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        TopItem oldItem = mOldList.get(oldItemPosition);
        TopItem newItem = mNewList.get(newItemPosition);

        return oldItem.elo == newItem.elo &&
                oldItem.given.equals(newItem.given) &&
                //oldItem.photo != null && oldItem.photo.equals(newItem.photo) &&
                oldItem.avg_time != null && oldItem.avg_time.equals(newItem.avg_time) &&
                oldItem.avg_score == newItem.avg_score;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}

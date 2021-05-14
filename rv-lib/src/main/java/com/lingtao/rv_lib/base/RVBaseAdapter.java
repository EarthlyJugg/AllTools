package com.lingtao.rv_lib.base;


import android.util.Log;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;


/**
 * Created by lingtao on 17/1/19.
 */

public class RVBaseAdapter<C extends Cell> extends RecyclerView.Adapter<RVBaseViewHolder> {
    public static final String TAG = "RVBaseAdapter";
    protected List<C> mData;

    private OnItemClickListener<C> listener;


    public RVBaseAdapter() {
        mData = new LinkedList<>();
    }

    @Override
    public RVBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        for (int i = 0; i < getItemCount(); i++) {
            if (viewType == mData.get(i).getItemType()) {
                C c = mData.get(i);
                RVBaseViewHolder holder = c.onCreateViewHolder(parent, viewType);
                return holder;
            }
        }

        throw new RuntimeException("wrong viewType");
    }

    @Override
    public void onBindViewHolder(RVBaseViewHolder holder, int position) {
        C t = mData.get(position);
        if (holder.itemView != null) {
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(t, holder, position);
                }
            });
        }
        t.onBindViewHolder(holder, position);
        onViewHolderBound(t, holder, position);

    }

    @Override
    public void onViewDetachedFromWindow(RVBaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        Log.e(TAG, "onViewDetachedFromWindow invoke...");
        //释放资源
        int position = holder.getAdapterPosition();
        //越界检查
        if (position < 0 || position >= mData.size()) {
            return;
        }
        mData.get(position).releaseResource();
    }


    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getItemType();
    }

    public void addData(List<C> data) {
        addAll(data);
        notifyDataSetChanged();
    }

    public void refreshData(List<C> data) {
        if (mData == null) {
            return;
        }
        mData.clear();
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public List<C> getData() {
        return mData;
    }

    /**
     * add one cell
     *
     * @param cell
     */
    public void add(C cell) {
        mData.add(cell);
        int index = mData.indexOf(cell);
        notifyItemChanged(index);
    }

    public void add(int index, C cell) {
        mData.add(index, cell);
        notifyItemChanged(index);
    }

    /**
     * remove a cell
     *
     * @param cell
     */
    public void remove(C cell) {
        int indexOfCell = mData.indexOf(cell);
        remove(indexOfCell);
    }

    public void remove(int index) {
        mData.remove(index);
        notifyItemRemoved(index);
    }

    /**
     * @param start
     * @param count
     */
    public void remove(int start, int count) {
        if ((start + count) > mData.size()) {
            return;
        }

        mData.subList(start, start + count).clear();

        notifyItemRangeRemoved(start, count);
    }


    /**
     * add a cell list
     *
     * @param cells
     */
    public void addAll(List<C> cells) {
        if (cells == null || cells.size() == 0) {
            return;
        }
        Log.e(TAG, "addAll cell size:" + cells.size());
        mData.addAll(cells);
        notifyItemRangeChanged(mData.size() - cells.size(), mData.size());
    }

    public void addAll(int index, List<C> cells) {
        if (cells == null || cells.size() == 0) {
            return;
        }
        mData.addAll(index, cells);
        notifyItemRangeChanged(index, index + cells.size());
    }

    public void clear() {
        mData.clear();
        notifyDataSetChanged();
    }

    public void onViewHolderBound(C bean, RVBaseViewHolder holder, int position) {

    }


    /**
     * 需要实现item 点击效果，需要在item 跟布局加上id =item
     *
     * @param listener
     */
    public void setListener(OnItemClickListener<C> listener) {
        this.listener = listener;
    }


    public interface OnItemClickListener<T extends Cell> {
        void onItemClick(T bean, RVBaseViewHolder holder, int position);
    }


}

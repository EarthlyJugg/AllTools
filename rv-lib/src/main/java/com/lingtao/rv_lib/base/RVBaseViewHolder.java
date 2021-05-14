package com.lingtao.rv_lib.base;


import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by lingtao
 */

public class RVBaseViewHolder extends RecyclerView.ViewHolder {
    private SparseArray<View> views;
    private View mItemView;
    private Context context;

    public RVBaseViewHolder(int layoutRes, ViewGroup parent) {
        this(layoutRes, parent, false);
    }

    public RVBaseViewHolder(int layoutRes, ViewGroup parent, boolean attachToRoot) {
        this(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, attachToRoot));
    }


    public RVBaseViewHolder(View itemView) {
        super(itemView);
        views = new SparseArray<>();
        mItemView = itemView;
        context = itemView.getContext();
    }

    public Context getContext() {
        return context;
    }

    /**
     * 获取ItemView
     *
     * @return
     */
    public View getItemView() {
        return mItemView;
    }

    public View getView(int resId) {
        return retrieveView(resId);
    }

    public TextView getTextView(int resId) {
        return retrieveView(resId);
    }


    public ImageView getImageView(int resId) {
        return retrieveView(resId);
    }

    public Button getButton(int resId) {
        return retrieveView(resId);
    }

    @SuppressWarnings("unchecked")
    protected <V extends View> V retrieveView(int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = mItemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (V) view;
    }

    public void setText(int resId, CharSequence text) {
        if (TextUtils.isEmpty(text)) {
            getTextView(resId).setText("");
        } else {
            getTextView(resId).setText(text);
        }
    }

    public void setImage(int resId, int imgRes) {
        getImageView(resId).setImageResource(imgRes);
    }

    public void setText(int resId, int strId) {
        getTextView(resId).setText(strId);
    }

}

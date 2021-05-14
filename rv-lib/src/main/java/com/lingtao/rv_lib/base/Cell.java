package com.lingtao.rv_lib.base;

import android.view.ViewGroup;

import java.io.Serializable;


/**
 * Created by zhouwei on 17/1/19.
 */

public abstract class Cell implements Serializable {


    /**
     * 需要回收资源子类自己去重写
     */
    protected void releaseResource() {

    }

    /**
     * 获取viewType
     *
     * @return
     */
    public abstract int getItemType();

    /**
     * 创建ViewHolder
     *
     * @param parent
     * @param viewType
     * @return
     */
    public abstract RVBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    /**
     * 数据绑定
     *
     * @param holder
     * @param position
     */
    public abstract void onBindViewHolder(RVBaseViewHolder holder, int position);

}

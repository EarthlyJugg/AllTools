package com.lingtao.thread_pool_manager;

public interface ICallback<T> {


    void onFailure(Exception e);


    void onResponse(T t);

}

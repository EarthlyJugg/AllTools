package com.stardust.sdk.zzftp.thread_pool_manager;

public interface ICallback<T> {


    void onFailure(Exception e);


    void onResponse(T t);

}

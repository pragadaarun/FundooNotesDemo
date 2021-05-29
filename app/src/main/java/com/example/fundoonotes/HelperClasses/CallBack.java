package com.example.fundoonotes.HelperClasses;

public interface CallBack<T>{
    void onSuccess(T data);
    void onFailure(Exception exception);
}

package com.mcal.kotlin.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mcal.kotlin.App;

public class Utils {

    public static boolean isNetworkAvailable() {
        ConnectivityManager connection = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connection.getActiveNetworkInfo();
        if (info == null) return false;
        else return info.isConnected();
    }

    static String reverseString(String string) {
        try {
            return new StringBuilder(string).reverse().toString();
        } catch (Exception e) {
            return "";
        }
    }
}
package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class BD {

    public static void SaveToken(String token, String tokentype, String username, Context activity)
    {
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor myEditor = myPreferences.edit();
        myEditor.putString("access_token", token);
        myEditor.putString("token_type",tokentype);
        myEditor.commit();
    }
    public static void SaveEmail(Context contex,String email)
    {
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(contex);
        SharedPreferences.Editor myEditor = myPreferences.edit();
        myEditor.putString("email", email);
        myEditor.commit();
    }
    public static String GetEmail(Context context)
    {
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return myPreferences.getString("email","not");
    }

    public static String GetToken(Context activity)
    {
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return myPreferences.getString("access_token","");
    }

    public static boolean CheckToken(Context activity)
    {
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return (myPreferences.contains("access_token")&&myPreferences.contains("user_name")&&myPreferences.contains("user_email"));
        //return false;
    }
    public static void DeleteToken(Context activity)
    {
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor myEditor = myPreferences.edit();
        myEditor.clear();
        myEditor.commit();
    }
    public static void SaveUserInfo(Context context, UserInfoModel model)
    {
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor myEditor = myPreferences.edit();
        myEditor.putString("user_name", model.Name);
        myEditor.putString("user_email",model.Email);
        myEditor.commit();
    }
    public static UserInfoModel GetUserInfo(Context context)
    {
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        UserInfoModel model=new UserInfoModel();
        model.Name=myPreferences.getString("user_name","");
        model.Email=myPreferences.getString("user_email","");
        return model;
    }
}

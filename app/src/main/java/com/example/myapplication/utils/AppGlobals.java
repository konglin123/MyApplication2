package com.example.myapplication.utils;

import android.app.Activity;
import android.app.Application;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AppGlobals {
    private static Application sApplication;
    //第二种获取context方法，第一种是Application的oncreate方法
    public static Application getApplication(){
         if(sApplication==null){
             try {
                 Method method = Class.forName("android.app.ActivityThread").getDeclaredMethod("currentApplication");
                 sApplication= (Application) method.invoke(null,null);
             } catch (Exception e) {
                 e.printStackTrace();
             }
         }
         return sApplication;
    }
}



package com.example.mydemopos.util;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/*
用于控制所有activity
 */
public class ActivityController {
    public static List<Activity> activityList = new ArrayList<>();
    public static void addActivity(Activity activity){
        if(!activityList.contains(activity)){
            activityList.add(activity);
        }
    }
    public static void removeActivity(Activity activity){
        activityList.remove(activity);
    }
    public static List<Activity> getAllActivity(){
        return activityList;
    }
}

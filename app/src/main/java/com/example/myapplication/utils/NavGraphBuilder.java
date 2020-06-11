package com.example.myapplication.utils;

import android.content.ComponentName;
import android.provider.Settings;

import com.example.libnavannotation.FragmentDestination;
import com.example.myapplication.FixFragmentNavigator;
import com.example.myapplication.model.Destination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.fragment.app.FragmentActivity;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavGraphNavigator;
import androidx.navigation.NavigatorProvider;
import androidx.navigation.fragment.FragmentNavigator;

public class NavGraphBuilder {
    /**
     * 自定义fragment导航器，用hide和show代替replace,oncretview方法只会在fragment为空时调用一次
     * @param controller
     * @param activity
     * @param containerId
     */
    public static void build(NavController controller, FragmentActivity activity,int containerId){
        NavigatorProvider provider = controller.getNavigatorProvider();
//        FragmentNavigator fragmentNavigator = provider.getNavigator(FragmentNavigator.class);
        FixFragmentNavigator fragmentNavigator=new FixFragmentNavigator(activity,activity.getSupportFragmentManager(),containerId);
        provider.addNavigator(fragmentNavigator);

        ActivityNavigator activityNavigator = provider.getNavigator(ActivityNavigator.class);

        NavGraph navGraph = new NavGraph(new NavGraphNavigator(provider));

        HashMap<String, Destination> destConfig = AppConfig.getDestConfig();
        for (Destination value : destConfig.values()) {
              if(value.isIsFragment()){
                  FragmentNavigator.Destination destination = fragmentNavigator.createDestination();
                  destination.setClassName(value.getClazName());
                  destination.setId(value.getId());
                  destination.addDeepLink(value.getPageUrl());
                  navGraph.addDestination(destination);
              }else{
                  ActivityNavigator.Destination destination = activityNavigator.createDestination();
                  destination.setId(value.getId());
                  destination.addDeepLink(value.getPageUrl());
                  destination.setComponentName(new ComponentName(AppGlobals.getApplication().getPackageName(),value.getClazName()));
                  navGraph.addDestination(destination);
              }

              if(value.isIsStarter()){
                 navGraph.setStartDestination(value.getId());
              }
        }
        controller.setGraph(navGraph);
    }

    /**
     * 系统fragment导航器，用的replace,每次切换fragment时都会调用oncreteview方法
     * @param controller
     */
    public static void build(NavController controller){
        NavigatorProvider provider = controller.getNavigatorProvider();
        FragmentNavigator fragmentNavigator = provider.getNavigator(FragmentNavigator.class);
        ActivityNavigator activityNavigator = provider.getNavigator(ActivityNavigator.class);

        NavGraph navGraph = new NavGraph(new NavGraphNavigator(provider));

        HashMap<String, Destination> destConfig = AppConfig.getDestConfig();
        for (Destination value : destConfig.values()) {
            if(value.isIsFragment()){
                FragmentNavigator.Destination destination = fragmentNavigator.createDestination();
                destination.setClassName(value.getClazName());
                destination.setId(value.getId());
                destination.addDeepLink(value.getPageUrl());
                navGraph.addDestination(destination);
            }else{
                ActivityNavigator.Destination destination = activityNavigator.createDestination();
                destination.setId(value.getId());
                destination.addDeepLink(value.getPageUrl());
                destination.setComponentName(new ComponentName(AppGlobals.getApplication().getPackageName(),value.getClazName()));
                navGraph.addDestination(destination);
            }

            if(value.isIsStarter()){
                navGraph.setStartDestination(value.getId());
            }
        }
        controller.setGraph(navGraph);
    }
}

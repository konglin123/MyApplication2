package com.example.myapplication.model;

public class Destination {

    /**
     * isFragment : true
     * needLogin : false
     * isStarter : false
     * clazName : com.example.myapplication.ui.dashboard.DashboardFragment
     * pageUrl : main/tabs/dash
     * id : 1855970549
     */

    private boolean isFragment;
    private boolean needLogin;
    private boolean isStarter;
    private String clazName;
    private String pageUrl;
    private int id;

    public boolean isIsFragment() {
        return isFragment;
    }

    public void setIsFragment(boolean isFragment) {
        this.isFragment = isFragment;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public boolean isIsStarter() {
        return isStarter;
    }

    public void setIsStarter(boolean isStarter) {
        this.isStarter = isStarter;
    }

    public String getClazName() {
        return clazName;
    }

    public void setClazName(String clazName) {
        this.clazName = clazName;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

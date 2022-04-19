package com.example.poolrdriver.classes;


import com.google.android.gms.maps.model.PolylineOptions;

public class Route {
    private String routeSource,routeDestination,userSource,userDestination;
    private static PolylineOptions polylineOptions;

    public Route(String routeSource, String routeDestination, String userSource, String userDestination) {
        this.routeSource = routeSource;
        this.routeDestination = routeDestination;
        this.userSource = userSource;
        this.userDestination = userDestination;
    }
    public Route(){}

    public static PolylineOptions getPolylineOptions() {
        return polylineOptions;
    }

    public void setPolylineOptions(PolylineOptions polylineOptions) {
        this.polylineOptions = polylineOptions;
    }

    public String getRouteSource() {
        return routeSource;
    }

    public void setRouteSource(String routeSource) {
        this.routeSource = routeSource;
    }

    public String getRouteDestination() {
        return routeDestination;
    }

    public void setRouteDestination(String routeDestination) {
        this.routeDestination = routeDestination;
    }

    public String getUserSource() {
        return userSource;
    }

    public void setUserSource(String userSource) {
        this.userSource = userSource;
    }

    public String getUserDestination() {
        return userDestination;
    }

    public void setUserDestination(String userDestination) {
        this.userDestination = userDestination;
    }
}

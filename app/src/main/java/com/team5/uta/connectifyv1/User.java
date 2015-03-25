package com.team5.uta.connectifyv1;

import android.location.Location;

import com.team5.uta.connectifyv1.adapter.Interest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by shreyas on 3/24/2015.
 */
public class User implements Serializable {

    private String uid;
    private String fname;
    private String lname;
    private String pwd;
    private String email;
    private Location currentLocation;
    private ArrayList<Interest> interests;

    public User(String fname, String lname, String pwd, String email, Location userLoc, ArrayList<Interest> interests) {
        this.setUid(UUID.randomUUID().toString());
        this.setFname(fname);
        this.setLname(lname);
        this.setPwd(pwd);
        this.setEmail(email);
        this.setCurrentLocation(userLoc);
        this.setInterests(interests);
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public ArrayList<Interest> getInterests() {
        return interests;
    }

    public void setInterests(ArrayList<Interest> interests) {
        this.interests = interests;
    }
}

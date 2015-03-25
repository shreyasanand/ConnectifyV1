package com.team5.uta.connectifyv1.adapter;

import java.io.Serializable;

/**
 * Created by shreyas on 2/20/2015.
 */
public class Interest implements Serializable{

    private String interestText;
    private int interestImageId;

    public Interest(String interestText, int interestImageId) {
        this.interestText = interestText;
        this.interestImageId = interestImageId;
    }

    public String getInterestText() {
        return interestText;
    }

    public int getInterestImageId() {
        return interestImageId;
    }

}

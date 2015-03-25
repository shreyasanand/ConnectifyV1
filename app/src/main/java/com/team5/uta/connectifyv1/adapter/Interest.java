package com.team5.uta.connectifyv1.adapter;

/**
 * Created by shreyas on 2/20/2015.
 */
public class Interest {

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

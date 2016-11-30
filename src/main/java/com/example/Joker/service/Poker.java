package com.example.Joker.service;

/**
 * Created by CoderSong on 16/11/30.
 */
public class Poker {

    private Integer point;
    private Integer color;

    public Poker(Integer point, Integer color){
        this.point = point;
        this.color = color;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }
}

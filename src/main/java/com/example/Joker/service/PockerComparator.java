package com.example.Joker.service;

import java.util.Comparator;

/**
 * Created by CoderSong on 16/12/19.
 */
public class PockerComparator implements Comparator {

    public int compare(Object arg0, Object arg1) {

        Poker poker1 = (Poker) arg0;
        Poker poker2 = (Poker) arg1;
        return poker1.getPoint().compareTo(poker2.getPoint());
    }
}

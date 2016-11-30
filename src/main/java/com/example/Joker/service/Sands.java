package com.example.Joker.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by CoderSong on 16/11/30.
 * 荷官类 完成发牌工作
 */
public class Sands {

    private List<Poker> pokers;
    private List<List<Poker>> players;

    public Sands() {
        pokers = new ArrayList<>();
        players = new ArrayList<>();
        List<Poker> playerOne = new ArrayList<>();
        List<Poker> playerTwo = new ArrayList<>();
        List<Poker> playerThree = new ArrayList<>();
        players.add(playerOne);
        players.add(playerTwo);
        players.add(playerThree);
    }

    /**
     * 初始化牌堆
     */
    public void addPokers() {
        for (int i = 1; i <= 4; i++) {
            for (int j = 3; j < 16; j++) {
                this.pokers.add(new Poker(j, i));
            }
        }

        Poker smallKing = new Poker(16, 1);
        Poker bigKing = new Poker(17, 1);
        this.pokers.add(smallKing);
        this.pokers.add(bigKing);
    }

    /**
     * 洗牌发牌
     */
    public void washPokers(){
        Random ra = new Random();
        for (int i = 0; i < this.pokers.size() - 3; i++) {
            int random = (int) ra.nextInt(3);
            while (this.players.get(random).size() == 17) {
                random = (int) ra.nextInt(3);
            }

            this.players.get(random).add(this.pokers.get(i));
        }
    }


    public List<List<Poker>> getPlayers() {
        return this.players;
    }
}

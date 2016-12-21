package com.example.Joker.domain;

import com.mongodb.*;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by CoderSong on 16/12/22.
 */
public class ReceiveScoreDBService {
    private Mongo mongo;
    private DB db;
    private DBCollection receiveScore;

    public ReceiveScoreDBService() {
        this.mongo = new Mongo("115.159.35.33", 27016);
        this.db = mongo.getDB("Jokers");
        this.receiveScore = db.getCollection("receiveScore");
    }


    /**
     * 保存数据
     *
     * @param receiveScoreData
     */
    public String saveData(DBObject receiveScoreData) {
        try {
            Date today = new Date();
            String todayString = DateFormat.getDateInstance().format(today);
            receiveScoreData.put("createDate", todayString);
            receiveScoreData.put("updateDate", todayString);
            this.receiveScore.save(receiveScoreData);
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "error";
        }
    }

    /**
     * 按日期和用户Id查找是否存在记录
     *
     * @return
     */
    public Integer findByDateAndUserId(String userId) {
        try {
            Date today = new Date();
            String todayString = DateFormat.getDateInstance().format(today);
            BasicDBObject basicObj = new BasicDBObject("createDate", todayString);
            Integer count = this.receiveScore.find(basicObj).count();
            return count;
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }
}

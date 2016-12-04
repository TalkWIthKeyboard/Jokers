package com.example.Joker.domain;

import com.mongodb.*;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.List;

/**
 * Created by CoderSong on 16/11/24.
 */
public class RoomDBService {

    private Mongo mongo;
    private DB db;
    private DBCollection room;

    public RoomDBService() {
        this.mongo = new Mongo("115.159.35.33", 27016);
        this.db = mongo.getDB("Jokers");
        this.room = db.getCollection("room");
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    public DBObject findById(String id) {
        try {
            BasicDBObject basicObj = new BasicDBObject("_id", new ObjectId(id));
            DBObject answer = this.room.findOne(basicObj);
            if (answer != null) {
                return answer;
            } else {
                // TODO 状态码和错误处理器
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 保存数据
     *
     * @param roomdata
     */
    public String saveData(DBObject roomdata) {
        try {
            Date today = new Date();
            roomdata.put("createDate", today);
            roomdata.put("updateDate", today);
            this.room.save(roomdata);
            return roomdata.get("_id").toString();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "error";
        }
    }


    /**
     * 更新数据
     *
     * @param id
     * @param user
     * @return
     */
    public String updateInfo(String id, DBObject user) {
        try {
            Date today = new Date();
            BasicDBObject basicObj = new BasicDBObject("_id", new ObjectId(id));
            user.put("updateDate", today);
            this.room.update(basicObj, user, false, false);
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "error";
        }
    }


    /**
     * 根据id删除数据
     *
     * @param id
     * @return
     */
    public String removeById(String id) {
        try {
            BasicDBObject basicObj = new BasicDBObject("_id", new ObjectId(id));
            this.room.remove(basicObj);
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "error";
        }
    }


    /**
     * 查找所有的房间信息
     *
     * @return
     */
    public List<DBObject> findAll() {
        try {
            List<DBObject> answer = this.room.find().toArray();
            if (answer.size() > 0) {
                return answer;
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}

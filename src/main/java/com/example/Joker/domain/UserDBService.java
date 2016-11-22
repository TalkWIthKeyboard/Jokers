package com.example.Joker.domain;

import com.mongodb.*;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.regex.Pattern;


/**
 * Created by CoderSong on 16/11/21.
 */


public class UserDBService {

    private Mongo mongo;
    private DB db;
    private DBCollection user;

    public UserDBService() {
        this.mongo = new Mongo();
        this.db = mongo.getDB("Jockers");
        this.user = db.getCollection("user");
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
            DBObject answer = this.user.findOne(basicObj);
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
     * 根据用户名进行查询
     *
     * @param account
     * @return
     */
    public DBObject findByAccount(String account) {
        try {
            BasicDBObject basicObj = new BasicDBObject("account", account);
            DBObject answer = this.user.findOne(basicObj);
            if (answer != null) {
                return answer;
            } else {
                return null;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * 根据名字分页查询
     *
     * @param username
     * @return
     */
    public List<DBObject> findByUsername(String username, Integer page) {
        try {
            // 构造正则
            Pattern pattern = Pattern.compile(".*" + username + ".*$", Pattern.CASE_INSENSITIVE);
            BasicDBObject basicObj = new BasicDBObject("account", pattern);
            List<DBObject> answer = this.user.find(basicObj).toArray();
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

    /**
     * 保存数据
     *
     * @param userdata
     */
    public String saveData(DBObject userdata) {
        try {
            this.user.save(userdata);
            return null;
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
            BasicDBObject basicObj = new BasicDBObject("_id", id);
            this.user.update(basicObj, user, false, false);
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return "error";
        }
    }

}

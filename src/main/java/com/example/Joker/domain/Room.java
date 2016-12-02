package com.example.Joker.domain;

/**
 * Created by CoderSong on 16/11/24.
 */
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection="room")
public class Room {

    private List<String> userList;
    // 是否是私人房间
    private Integer isPrivate;
    private String key;
    private Integer readyNum;
    private String landlordUserId;
    private Integer landlordScore;
    private Integer rodNumber;
    private Integer state;
    private Date createDate;
    private Date updateDate;

    public Room() {

    }

    public Room(List<String> userList,
                Integer isPrivate,
                String key,
                Date createDate,
                Date updateDate) {
        this.userList = userList;
        this.isPrivate = isPrivate;
        this.key = key;
        this.readyNum = 0;
        this.landlordScore = 0;
        this.landlordUserId = null;
        this.rodNumber = 0;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }


    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }

    public Integer getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(Integer isPrivate) {
        this.isPrivate = isPrivate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getReadyNum() {
        return readyNum;
    }

    public void setReadyNum(Integer readyNum) {
        this.readyNum = readyNum;
    }

    public Integer getRodNumber() {
        return rodNumber;
    }

    public void setRodNumber(Integer rodNumber) {
        this.rodNumber = rodNumber;
    }

    public String getLandlordUserId() {
        return landlordUserId;
    }

    public void setLandlordUserId(String landlordUserId) {
        this.landlordUserId = landlordUserId;
    }

    public Integer getLandlordScore() {
        return landlordScore;
    }

    public void setLandlordScore(Integer landlordScore) {
        this.landlordScore = landlordScore;
    }

}


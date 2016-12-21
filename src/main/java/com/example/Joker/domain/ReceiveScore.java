package com.example.Joker.domain;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Created by CoderSong on 16/12/22.
 */

@Document(collection="receiveScore")
public class ReceiveScore {

    private String userId;
    private String createDate;
    private String updateDate;

    public ReceiveScore(String userId, String createDate, String updateDate) {
        this.userId = userId;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public String getUserId() {
        return userId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}

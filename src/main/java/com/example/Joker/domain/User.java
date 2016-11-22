package com.example.Joker.domain;

/**
 * Created by CoderSong on 16/11/21.
 */

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection="user")
public class User {

    private String username;
    private String account;
    private String password;
    private String image;
    private Integer sex;
    private Integer score;
    private Date createDate;
    private Date updateDate;

    public User() {

    }

    public User(String username,
                String account,
                String password,
                String image,
                Integer sex,
                Integer score,
                Date createDate,
                Date updateDate) {
        this.username = username;
        this.account = account;
        this.password = password;
        this.image = image;
        this.sex = sex;
        this.score = score;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }




    // getter && setter
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}

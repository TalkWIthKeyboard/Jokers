package com.example.Joker;

/**
 * Created by CoderSong on 16/11/21.
 */

import com.example.Joker.service.ErrorHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Config {

    /**
     * 默认分页的每页个数
     */
    private Integer pageSize;

    /**
     * 统一错误处理器
     */
    private List<ErrorHandler> errorHandler;

    public Config() {
        this.pageSize = 10;
        List<ErrorHandler> list = new ArrayList<ErrorHandler>();
        ErrorHandler success = new ErrorHandler(200, "SUCCESS", "操作成功"),
                userError = new ErrorHandler(410, "USER_ERROR", "不存在这个用户"),
                pwdError = new ErrorHandler(411, "PWD_ERROR", "用户密码错误"),
                liveError = new ErrorHandler(412, "LIVE_ERROR", "用户没有登录"),
                insideError = new ErrorHandler(500, "INSIDE_ERROR", "方法内部错误"),
                dbSaveError = new ErrorHandler(420, "DB_SAVE_ERROR", "数据库的存储错误"),
                dbUpdateError = new ErrorHandler(421, "DB_UPDATE_ERROR", "数据库的更新错误"),
                dbRemoveError = new ErrorHandler(422, "DB_REMOVE_ERROR", "数据库的删除错误");

        list.add(success);
        list.add(userError);
        list.add(pwdError);
        list.add(insideError);
        list.add(liveError);
        list.add(dbSaveError);
        list.add(dbUpdateError);
        list.add(dbRemoveError);
        this.errorHandler = list;
    }

    public ErrorHandler getHandler(String key) {

        for (int i = 0; i < this.errorHandler.size(); i++) {
            ErrorHandler error = this.errorHandler.get(i);
            if (error.getErrorValue().containsKey(key)){
                return error;
            }
        }

        return null;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<ErrorHandler> getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(List<ErrorHandler> errorHandler) {
        this.errorHandler = errorHandler;
    }
}

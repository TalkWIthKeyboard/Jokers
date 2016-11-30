package com.example.Joker.service.tool;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CoderSong on 16/11/22.
 * 统一错误处理器
 */
public class ErrorHandler{

    private Integer errorNum;
    private Map<String,String> errorValue;
    private Object params;

    public ErrorHandler(Integer num, String key, String value){
        this.errorNum = num;
        Map map = new HashMap();
        map.put(key,value);
        this.errorValue = map;
    }

    public Map getErrorValue() {
        return errorValue;
    }

    public void setErrorValue(Map errorValue) {
        this.errorValue = errorValue;
    }

    public Integer getErrorNum() {
        return errorNum;
    }

    public void setErrorNum(Integer errorNum) {
        this.errorNum = errorNum;
    }


    public Object getParams() {
        return params;
    }

    public void setParams(Object params) {
        this.params = params;
    }
}

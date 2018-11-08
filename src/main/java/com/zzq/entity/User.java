package com.zzq.entity;

/**
 * @author zhangzhiqiang
 * @date 2018-08-06 16:15
 * &Desc 汽车报价大全yiche用户
 */
public class User {
    String tel;
    String password;
    String deviceId;
    String userAgent;
    Integer score=0;
    Integer earn=0;
    String  userId;
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getEarn() {
        return earn;
    }

    public void setEarn(Integer earn) {
        this.earn = earn;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

package com.baidu.paddle.lite.demo.common;

public class CovidSafeDetectorModel {
    private String user_no;
    private String timestamp;
    private String image_url;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public CovidSafeDetectorModel() {
    }

    public CovidSafeDetectorModel(String user_no, String image_url, String timestamp) {
        this.user_no = user_no;
        this.image_url = image_url;
        this.timestamp = timestamp;
    }

    public String getUser_no() {
        return user_no;
    }

    public void setUser_no(String user_no) {
        this.user_no = user_no;
    }

}

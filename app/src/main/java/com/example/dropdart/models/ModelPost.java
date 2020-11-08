package com.example.dropdart.models;

public class ModelPost {
    String pId, ptitle,  pTime, uid, uEmail, uDp, uName, pVideo;

    public ModelPost() {
    }

    public ModelPost(String pId, String ptitle, String pTime, String uid, String uEmail, String uDp, String uName, String pVideo) {
        this.pId = pId;
        this.ptitle = ptitle;
        this.pTime = pTime;
        this.uid = uid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uName = uName;
        this.pVideo = pVideo;
    }

    public String getpVideo() {
        return pVideo;
    }

    public void setpVideo(String pVideo) {
        this.pVideo = pVideo;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getptitle() {
        return ptitle;
    }

    public void setptitle(String ptitle) {
        this.ptitle = ptitle;
    }


    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }
}

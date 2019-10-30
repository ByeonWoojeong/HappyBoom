package app.woojeong.happyboom.DTO;

public class MainVideo {

    String idx, thumbnail, profieImage, nick, date, content, likeCnt, shareCnt,replyCnt, isLike;

    public MainVideo(String idx, String thumbnail, String profieImage, String nick, String date, String content, String likeCnt, String shareCnt, String replyCnt, String isLike) {
        this.idx = idx;
        this.thumbnail = thumbnail;
        this.profieImage = profieImage;
        this.nick = nick;
        this.date = date;
        this.content = content;
        this.likeCnt = likeCnt;
        this.shareCnt = shareCnt;
        this.replyCnt = replyCnt;
        this.isLike = isLike;
    }

    public MainVideo(String idx, String thumbnail, String profieImage, String nick, String date, String content, String likeCnt, String shareCnt, String isLike) {
        this.idx = idx;
        this.thumbnail = thumbnail;
        this.profieImage = profieImage;
        this.nick = nick;
        this.date = date;
        this.content = content;
        this.likeCnt = likeCnt;
        this.shareCnt = shareCnt;
        this.isLike = isLike;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getProfieImage() {
        return profieImage;
    }

    public void setProfieImage(String profieImage) {
        this.profieImage = profieImage;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLikeCnt() {
        return likeCnt;
    }

    public void setLikeCnt(String likeCnt) {
        this.likeCnt = likeCnt;
    }

    public String getShareCnt() {
        return shareCnt;
    }

    public void setShareCnt(String shareCnt) {
        this.shareCnt = shareCnt;
    }

    public String getReplyCnt() {
        return replyCnt;
    }

    public void setReplyCnt(String replyCnt) {
        this.replyCnt = replyCnt;
    }

    public String getIsLike() {
        return isLike;
    }

    public void setIsLike(String isLike) {
        this.isLike = isLike;
    }
}

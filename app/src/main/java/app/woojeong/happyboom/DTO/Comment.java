package app.woojeong.happyboom.DTO;

public class Comment {

    String key, nick, date, content, isMy, isReply, member;

    public Comment(String key, String nick, String date, String content, String isMy, String isReply, String member) {
        this.key = key;
        this.nick = nick;
        this.date = date;
        this.content = content;
        this.isMy = isMy;
        this.isReply = isReply;
        this.member = member;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getIsMy() {
        return isMy;
    }

    public void setIsMy(String isMy) {
        this.isMy = isMy;
    }

    public String getIsReply() {
        return isReply;
    }

    public void setIsReply(String isReply) {
        this.isReply = isReply;
    }

    public String getMember() {
        return member;
    }

    public void setMember(String member) {
        this.member = member;
    }
}

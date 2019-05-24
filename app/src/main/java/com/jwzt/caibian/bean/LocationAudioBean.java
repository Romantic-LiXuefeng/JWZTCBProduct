package com.jwzt.caibian.bean;

/**
 * Created by pc on 2018/10/10.
 */

public class LocationAudioBean {
    private String song;//歌曲名
    private String singer;//歌手
    private long size;//歌曲所占空间大小
    private int duration;//歌曲时间长度
    private String path;//歌曲地址
    private long createTime;//创建时间

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}

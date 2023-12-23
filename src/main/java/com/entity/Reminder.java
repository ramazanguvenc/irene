package com.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reminder")
public class Reminder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
    @Column(name = "chatid")
    private long chatid;
    
    @Column(name = "starttime")
    private long startTime;


    @Column(name = "howLong")
    private long howLong;

    @Column(name = "message")
    private String message;

    public String getMessage() {
        return message;
    }


    public void setMessage(String message) {
        this.message = message;
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public long getChatid() {
        return chatid;
    }


    public void setChatid(long chatid) {
        this.chatid = chatid;
    }


    public long getStartTime() {
        return startTime;
    }


    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }


    public long getHowLong() {
        return howLong;
    }


    public void setHowLong(long howLong) {
        this.howLong = howLong;
    }

}

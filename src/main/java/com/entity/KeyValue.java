package com.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

//Not best way to do it, but not a lot of people going to use this bot and this is going to work.
//In the end it is what it is
@Entity
@Table(name = "keyvalue")
public class KeyValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "`key`")
    private String key;
    
    @Column(name = "value")
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public KeyValue(){

    }

    public KeyValue(String key, String value){
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "KeyValue [id=" + id + ", key=" + key + ", value=" + value + "]";
    }



}

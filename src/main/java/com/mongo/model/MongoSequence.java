package com.mongo.model;

import org.springframework.data.annotation.Id;

/**
 * Created by Sunc on 2018/1/14.
 */

public class MongoSequence {
    @Id
    private String id;
    private int seq;

    public MongoSequence() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }
}

package com.testTask.storage;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class BaseEntity
    implements  IEntity {
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isSaved() {
        return id > 0;
    }

    public abstract IEntity copy();
}

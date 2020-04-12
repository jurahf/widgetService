package com.testTask.storage;

/**
 * Entity that may be in storage
 */
public interface IEntity {
    int getId();
    void setId(int id);
    boolean isSaved();
}

package com.testTask.storage;

public interface IUow<T extends IEntity> {
    void addChanges(T entity, ChangeKind change);
    void commitAllChanges();
}

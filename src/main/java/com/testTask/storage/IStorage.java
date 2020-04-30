package com.testTask.storage;

import java.util.List;

/**
 * Storage with CRUD-operations for entity
 * @param <T> entity
 */
public interface IStorage<T extends IEntity>
        extends IUow<T> {
    /** Insert or update entity to storage
     * @return object id */
    int save(T ent);

    /**
     * @return entity or null
     */
    T get(int id);

    /**
     * @return entity list or empty list
     */
    List<T> getAll();

    /**
     * @return <code>true</code> in case success
     */
    boolean delete(int id);
}

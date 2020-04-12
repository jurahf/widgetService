package com.testTask.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryStorage<T extends BaseEntity>
    implements  IStorage<T> {

    private final AtomicInteger idGenerator = new AtomicInteger();
    private HashMap<Integer, T> map = new HashMap<>();

    public int save(T ent)
        throws IllegalArgumentException {
        if (ent == null)
            throw new IllegalArgumentException("Entity for saving can't be empty.");

        if (ent.isSaved()) {
            // update
            map.replace(ent.getId(), ent);
        }
        else {
            // create
            ent.setId(idGenerator.incrementAndGet());
            map.put(ent.getId(), ent);
        }

        return ent.getId();
    }

    public T get(int id) {
        T result = map.get(id);

        if (result == null)
            return null;
        else
            return (T)result.copy(); // чтобы объект менялся извне только путем вызова save
    }

    public List<T> getAll() {
        return new ArrayList<>(map.values().stream().map(x -> (T)x.copy()).collect(Collectors.toList()));
    }

    public boolean delete(int id) {
        T old = map.remove(id);
        return old != null;
    }

}

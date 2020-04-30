package com.testTask.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InMemoryStorage<T extends BaseEntity>  // так-то H2 тоже in-memory
    implements  IStorage<T> {

    private final AtomicInteger idGenerator = new AtomicInteger();
    private HashMap<Integer, T> map = new HashMap<>();
    private HashMap<Integer, T> addedSaveChanges = new HashMap<>();
    private List<Integer> addedDeleteChanges = new ArrayList<>();

    private int generateNewId() {
        return idGenerator.incrementAndGet();
    }

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
            ent.setId(generateNewId());
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
        return map.values().stream().map(x -> (T)x.copy()).collect(Collectors.toList());
    }

    public boolean delete(int id) {
        T old = map.remove(id);
        return old != null;
    }

    public void addChanges(T entity, ChangeKind change) {
        if (change == ChangeKind.SAVE) {
            if (!entity.isSaved())
                entity.setId(generateNewId());

            if (addedSaveChanges.containsKey(entity.getId()))
                addedSaveChanges.replace(entity.getId(), entity);
            else
                addedSaveChanges.put(entity.getId(), entity);
        }
        else {
            if (entity.isSaved() && !addedDeleteChanges.contains(entity.getId()))
                addedDeleteChanges.add(entity.getId());
        }
    }

    public void commitAllChanges(){
        HashMap<Integer, T> copyMap = new HashMap<>(map.values().stream()
                        .map(x -> (T)x.copy())
                        .collect(Collectors.toMap(x -> x.getId(), x -> x)));

        for (int i : addedSaveChanges.keySet()) {
            if (copyMap.containsKey(i)) {
                copyMap.replace(i, addedSaveChanges.get(i));
            }
            else {
                copyMap.put(i, addedSaveChanges.get(i));
            }
        }

        for (int i : addedDeleteChanges) {
            copyMap.remove(i);
        }

        map = copyMap;
        addedSaveChanges = new HashMap<>();
        addedDeleteChanges = new ArrayList<>();
    }

}

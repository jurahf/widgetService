package com.testTask.storage;


import com.testTask.domain.Widget;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomStorageTest {

    private IStorage<Widget> CreateStorage() {
        return new InMemoryStorage<Widget>();
    }

    @Test
    public void insertAndSelect() {
        var storage = CreateStorage();
        Widget w = storage.get(1);
        assertEquals(null, w);

        w = new Widget(1, 1, 0, 100, 100);
        int id = storage.save(w);

        Widget wSaved = storage.get(id);
        assertEquals(1, wSaved.getX());
        assertEquals(1, wSaved.getY());
        assertEquals(0, wSaved.getZ_index());
        assertEquals(100, wSaved.getWidth());
        assertEquals(100, wSaved.getHeight());
    }

    @Test
    public void getAll() {
        var storage = CreateStorage();
        Widget w1 = new Widget(1, 1, 3, 1, 1);
        Widget w2 = new Widget(2, 2, 1, 2, 2);
        Widget w3 = new Widget(3, 3, 2, 3, 3);

        storage.save(w1);
        storage.save(w2);
        storage.save(w3);

        List<Widget> list = storage.getAll();
        assertEquals(3, list.size());
    }

    @Test
    public void deleteTest() {
        var storage = CreateStorage();
        boolean res = storage.delete(1);
        assertEquals(false, res);

        Widget w = new Widget(1, 1, 0, 100, 100);
        int id = storage.save(w);
        res = storage.delete(id);
        assertEquals(true, res);
    }

    @Test
    public void updateTest() {
        var storage = CreateStorage();
        Widget w = new Widget(1, 1, 0, 100, 100);
        int id = storage.save(w);

        assertEquals(id, w.getId());

        w.setX(10);
        storage.save(w);

        assertEquals(10, w.getX());
        assertEquals(1, w.getY());
        assertEquals(0, w.getZ_index());
        assertEquals(100, w.getWidth());
        assertEquals(100, w.getHeight());
    }
}

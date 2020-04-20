package com.testTask.widgetLogic;

import com.testTask.domain.Widget;
import com.testTask.storage.InMemoryStorage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WLogicCustomStorageTest {

    private WLogic createLogic(){
        WLogic logic = new WLogic();
        logic.setStorage(new InMemoryStorage<Widget>());
        return logic;
    }

    @Test
    public void insertAndSelect() {
        WLogic logic = createLogic();
        Widget w = logic.getWidget(1);
        assertEquals(null, w);

        w = logic.createWidget(1, 1, 0, 100, 100);

        assertEquals(1, w.getX());
        assertEquals(1, w.getY());
        assertEquals(0, w.getZ_index());
        assertEquals(100, w.getWidth());
        assertEquals(100, w.getHeight());
    }

    @Test
    public void getAll_Sorted() {
        WLogic logic = createLogic();
        logic.createWidget(1, 1, 3, 1, 1);
        logic.createWidget(2, 2, 1, 2, 2);
        logic.createWidget(3, 3, 2, 3, 3);

        List<Widget> list = logic.getAll();
        assertEquals(1, list.get(0).getZ_index());
        assertEquals(2, list.get(1).getZ_index());
        assertEquals(3, list.get(2).getZ_index());
    }

    @Test
    public void deleteTest() {
        WLogic logic = createLogic();
        boolean res = logic.delete(1);
        assertEquals(false, res);

        int id = logic.createWidget(1, 1, 0, 100, 100).getId();
        res = logic.delete(id);
        assertEquals(true, res);
    }

    @Test
    public void updateTest() {
        WLogic logic = createLogic();
        assertThrows(IllegalArgumentException.class, () ->
                logic.updateWidget(1, 10, null, null, null, null));

        int id = logic.createWidget(1, 1, 0, 100, 100).getId();
        logic.updateWidget(id, 10, null, null, null, null);
        Widget w = logic.getWidget(id);
        assertEquals(10, w.getX());
        assertEquals(1, w.getY());
        assertEquals(0, w.getZ_index());
        assertEquals(100, w.getWidth());
        assertEquals(100, w.getHeight());
    }

    @Test
    public void shiftZIndex(){
        WLogic logic = createLogic();

        int id1 = logic.createWidget(1, 1, 1, 1, 1).getId();
        int id2 = logic.createWidget(2, 2, 2, 2, 2).getId();
        int id3 = logic.createWidget(3, 3, 3, 3, 3).getId();

        logic.updateWidget(id3, null, null, 1, null, null);

        Widget w1 = logic.getWidget(id1);
        Widget w2 = logic.getWidget(id2);
        Widget w3 = logic.getWidget(id3);

        assertEquals(id1, w1.getId());
        assertEquals(2, w1.getZ_index());

        assertEquals(id2, w2.getId());
        assertEquals(3, w2.getZ_index());

        assertEquals(id3, w3.getId());
        assertEquals(1, w3.getZ_index());
    }

    @Test
    public void overflowZIndexShift() {
        WLogic logic = createLogic();
        logic.createWidget(0, 0, Integer.MAX_VALUE, 100, 100);
        assertThrows(StackOverflowError.class, () -> logic.createWidget(1, 1, Integer.MAX_VALUE, 100, 100));
    }

    @Test
    public void overflowZIndexFirstInsert() {
        WLogic logic = createLogic();
        logic.createWidget(0, 0, Integer.MAX_VALUE, 100, 100);
        assertThrows(StackOverflowError.class, () -> logic.createWidget(1, 1, null, 100, 100));
    }

    @Test
    public void pagination() {
        WLogic logic = createLogic();

        int id1 = logic.createWidget(1, 1, 1, 1, 1).getId();
        int id2 = logic.createWidget(2, 2, 2, 2, 2).getId();
        int id3 = logic.createWidget(3, 3, 3, 3, 3).getId();

        var list1 = logic.getPage(2, 1);
        var list2 = logic.getPage(2, 2);
        var list3 = logic.getPage(2, 3);

        assertEquals(2, list1.size());
        assertEquals(id1, list1.get(0).getId());
        assertEquals(id2, list1.get(1).getId());

        assertEquals(1, list2.size());
        assertEquals(id3, list2.get(0).getId());

        assertEquals(0, list3.size());
    }

}
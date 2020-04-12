package com.testTask.widgetLogic;

import com.testTask.domain.Widget;
import com.testTask.storage.InMemoryStorage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WLogicTest {

    private static WLogic logic;

    @BeforeAll
    public static void Init() {
        logic = new WLogic(new InMemoryStorage<Widget>());
    }

    @Test
    public void getAll_Sorted() {
        logic.createWidget(1, 1, 3, 1, 1);
        logic.createWidget(2, 2, 1, 2, 2);
        logic.createWidget(3, 3, 2, 3, 3);

        List<Widget> list = logic.getAll();
        assertEquals(1, list.get(0).getZ_index());
        assertEquals(2, list.get(1).getZ_index());
        assertEquals(3, list.get(2).getZ_index());
    }
}
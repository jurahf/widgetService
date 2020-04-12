package com.testTask.widgetLogic;

import com.testTask.domain.Widget;
import com.testTask.storage.IStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WLogic {
    private final IStorage<Widget> storage;
    private static Object syncObject = new Object();

    public WLogic(IStorage<Widget> storage) {
        this.storage = storage;
    }


    private int pushZIndexAndShiftAll(Integer z_index, Integer id) {
        int wishIndex;
        List<Widget> allWidgets = storage.getAll();

        // если индекс не указан - помещаем вперед, то есть ищем максимальный из существующих
        if (z_index == null) {
            Optional<Integer> maxOpt = allWidgets.stream()
                    .map(x -> x.getZ_index())
                    .collect(Collectors.maxBy(Integer::compareTo));

            if (maxOpt.isEmpty())
                wishIndex = 0;
            else
                wishIndex = maxOpt.get() + 1; // TODO: переполнение?
        }
        else {
            wishIndex = z_index;
        }

        // проверим, нет ли на желаемом индексе других виджетов
        Optional<Widget> onSamePlace = allWidgets.stream()
                .filter(x -> x.getZ_index() == wishIndex && x.getId() != id)
                .findFirst();

        if (!onSamePlace.isEmpty()) {
            // что-то есть, надо двигать все виджеты, которые выше
            List<Widget> uppers = allWidgets.stream()
                    .filter(x -> x.getZ_index() >= wishIndex /*&& x.getId() != id*/) // текущий изменяемый тоже надо передвинуть, чтобы никогда не было 2 виджета на одном уровне, хоть это и не тот индекс, который нужен.
                    .sorted(Comparator.comparingInt(Widget::getZ_index).reversed())
                    .collect(Collectors.toList());

            for (Widget item : uppers) {
                item.setZ_index(item.getZ_index() + 1); // TODO: переполнение?
                storage.save(item);
            }
        }

        return wishIndex;
    }

    public int createWidget(Integer x, Integer y, Integer z_index, Integer width, Integer height)
        throws IllegalArgumentException {
        if (x == null || y == null
            || width == null || height == null) {
            throw new IllegalArgumentException("During creation, all parameters can't be empty: x, y, width, height.");
        }

        synchronized (syncObject) {
            int z = pushZIndexAndShiftAll(z_index, null); // это должно быть внутри синхронизации, чтобы все виджеты переместились одновременно
            Widget widget = new Widget(x, y, z, width, height);

            return storage.save(widget);
        }
    }

    public int updateWidget(int id, Integer x, Integer y, Integer z_index, Integer width, Integer height)
        throws IllegalArgumentException {
        synchronized (syncObject) {
            Widget w = storage.get(id);

            if (w == null) {
                throw new IllegalArgumentException("Widget for updating not found.");
            } else {
                if (x != null)
                    w.setX(x);
                if (y != null)
                    w.setY(y);
                if (z_index != null) {
                    int z = pushZIndexAndShiftAll(z_index, id); // это должно быть внутри синхронизации, чтобы все виджеты переместились одновременно
                    w.setZ_index(z);
                }

                if (width != null)
                    w.setWidth(width);
                if (height != null)
                    w.setHeight(height);

                return storage.save(w);
            }
        }
    }

    public Widget getWidget(int id) {
        synchronized (syncObject) {
            return storage.get(id);
        }
    }

    public List<Widget> getAll() {
        synchronized (syncObject) {
            return storage.getAll().stream()
                    .sorted(Comparator.comparingInt(Widget::getZ_index))
                    .collect(Collectors.toList());
        }
    }

    public boolean delete(int id) {
        synchronized (syncObject) {
            return storage.delete(id);
        }
    }

}

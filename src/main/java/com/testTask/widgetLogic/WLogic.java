package com.testTask.widgetLogic;

import com.testTask.domain.Widget;
import com.testTask.storage.ChangeKind;
import com.testTask.storage.IStorage;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WLogic {
    @Autowired
    private IStorage<Widget> storage;
    private static Object syncObject = new Object();

    public void setStorage(IStorage<Widget> s) {
        this.storage = s;
    }


    private int getWishIndex(Integer z_index, Integer id, List<Widget> allWidgets) {
        int wishIndex;

        if (z_index == null) {
            Optional<Integer> maxOpt = allWidgets.stream()
                    .map(x -> x.getZ_index())
                    .collect(Collectors.maxBy(Integer::compareTo));

            if (maxOpt.isEmpty())
                wishIndex = 0;
            else {
                int maxLevel = maxOpt.get();
                if (maxLevel >= Integer.MAX_VALUE)
                    throw new StackOverflowError("Upper levels are already taken");
                wishIndex = maxLevel + 1;
            }
        }
        else {
            wishIndex = z_index;
        }

        return wishIndex;
    }


    /**
     * Передвигает виджеты, чтобы можно было вставить новый на место z_index.
     * Добавляет изменения виджетов в транзакцию хранилища.
     * @param z_index
     * @param id
     * @return
     * @throws StackOverflowError
     */
    private int pushZIndexAndShiftAll(Integer z_index, Integer id)
        throws StackOverflowError {

        List<Widget> allWidgets = storage.getAll();
        int wishIndex = getWishIndex(z_index, id, allWidgets);

        // проверим, нет ли на желаемом индексе других виджетов
        Optional<Widget> onSamePlace = allWidgets.stream()
                .filter(x -> x.getZ_index() == wishIndex && (Integer)x.getId() != id)
                .findFirst();

        if (!onSamePlace.isEmpty()) {
            // что-то есть, надо двигать все виджеты, которые выше
            List<Widget> uppers = allWidgets.stream()
                    .filter(x -> x.getZ_index() >= wishIndex /*&& x.getId() != id*/) // текущий изменяемый тоже надо передвинуть, чтобы никогда не было 2 виджета на одном уровне, хоть это и не тот индекс, который нужен.
                    .sorted(Comparator.comparingInt(Widget::getZ_index).reversed())
                    .collect(Collectors.toList());

            for (Widget item : uppers) {
                int maxLevel = item.getZ_index();
                if (maxLevel >= Integer.MAX_VALUE)
                    throw new StackOverflowError("Upper levels are already taken");
                item.setZ_index(maxLevel + 1);
                item.setLastModificationDateTime(LocalDateTime.now());
                storage.addChanges(item, ChangeKind.SAVE);
            }
        }

        return wishIndex;
    }

    public Widget createWidget(Integer x, Integer y, Integer z_index, Integer width, Integer height)
        throws IllegalArgumentException {
        if (x == null || y == null
            || width == null || height == null) {
            throw new IllegalArgumentException("During creation, all parameters can't be empty: x, y, width, height.");
        }

        synchronized (syncObject) {
            int z = pushZIndexAndShiftAll(z_index, null); // тоже добавляет изменения в транзакцию
            Widget widget = new Widget(x, y, z, width, height);
            storage.addChanges(widget, ChangeKind.SAVE);
            storage.commitAllChanges();
            return widget;
        }
    }

    public Widget updateWidget(int id, Integer x, Integer y, Integer z_index, Integer width, Integer height)
        throws IllegalArgumentException {
        synchronized (syncObject) {
            Widget w = storage.get(id);

            if (w == null) {
                throw new IllegalArgumentException("Widget for updating is not found.");
            } else {
                if (x != null)
                    w.setX(x);
                if (y != null)
                    w.setY(y);
                if (z_index != null) {
                    int z = pushZIndexAndShiftAll(z_index, id); // тоже добавляет изменения в транзакцию
                    w.setZ_index(z);
                }

                if (width != null)
                    w.setWidth(width);
                if (height != null)
                    w.setHeight(height);

                w.setLastModificationDateTime(LocalDateTime.now());
                storage.addChanges(w, ChangeKind.SAVE);
                storage.commitAllChanges();
                return w;
            }
        }
    }

    public Widget getWidget(int id) {
        return storage.get(id);
    }

    public List<Widget> getAll() {
        return storage.getAll().stream()
                .sorted(Comparator.comparingInt(Widget::getZ_index))
                .collect(Collectors.toList());
    }

    public List<Widget> getPage(int limit, int page)
        throws IllegalArgumentException {
        if (page <= 0 || limit <= 0)
            throw new IllegalArgumentException("Limit and Page must be positive.");

        if (limit > 500)
            throw new IllegalArgumentException("Limit can't be more than 500.");

        List<Widget> all = getAll();    // то есть мы читаем и материализуем все равно все
        return all.subList(Math.min((page - 1) * limit, all.size()), Math.min(page * limit, all.size()));
    }

    public boolean delete(int id)
        throws IllegalArgumentException {
        synchronized (syncObject) {
            if (storage.delete(id))
                return true;
            else
                throw new IllegalArgumentException("Widget for delete is not found.");
        }
    }

}

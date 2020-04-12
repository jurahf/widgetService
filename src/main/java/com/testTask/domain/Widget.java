package com.testTask.domain;

import com.testTask.storage.BaseEntity;
import com.testTask.storage.IEntity;

import java.time.LocalDateTime;

/**
 * */
public class Widget
        extends BaseEntity {
    private int x;
    private int y;
    private int z_index;

    private int height;
    private int width;

    private LocalDateTime lastModificationDateTime;     // все-таки не только дата, но и время

    public Widget(int x, int y, int z, int width, int height) {
        this.x = x;
        this.y = y;
        this.z_index = z;
        this.width = width;
        this.height = height;
        lastModificationDateTime = LocalDateTime.now();
    }


    public void setX(int x) {
        lastModificationDateTime = LocalDateTime.now();
        this.x = x;
    }
    public int getX() {
        return x;
    }

    public void setY(int y) {
        lastModificationDateTime = LocalDateTime.now();
        this.y = y;
    }
    public int getY() {
        return y;
    }

    public void setZ_index(int z) {
        lastModificationDateTime = LocalDateTime.now();
        this.z_index = z;
    }
    public int getZ_index(){
        return z_index;
    }

    public void setHeight(int h) {
        lastModificationDateTime = LocalDateTime.now();
        this.height = h;
    }
    public int getHeight(){
        return height;
    }

    public void setWidth(int w) {
        lastModificationDateTime = LocalDateTime.now();
        this.width = w;
    }
    public int getWidth() {
        return width;
    }

    public LocalDateTime getLastModificationDateTime() {
        return lastModificationDateTime;
    }

    /**
     * Full copy, with id*/
    @Override
    public Widget copy() {
        Widget result = new Widget(x, y, z_index, width, height);
        result.setId(this.getId());
        return result;
    }
}

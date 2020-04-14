package com.testTask.domain;

import com.testTask.storage.BaseEntity;

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

    private LocalDateTime lastModificationDateTime;

    public Widget() {
    }

    public Widget(int x, int y, int z, int width, int height) {
        this.x = x;
        this.y = y;
        this.z_index = z;
        this.width = width;
        this.height = height;
        lastModificationDateTime = LocalDateTime.now();
    }


    public void setX(int x) {
        this.x = x;
    }
    public int getX() {
        return x;
    }

    public void setY(int y) {
        this.y = y;
    }
    public int getY() {
        return y;
    }

    public void setZ_index(int z) {
        this.z_index = z;
    }
    public int getZ_index(){
        return z_index;
    }

    public void setHeight(int h) {
        this.height = h;
    }
    public int getHeight(){
        return height;
    }

    public void setWidth(int w) {
        this.width = w;
    }
    public int getWidth() {
        return width;
    }

    public LocalDateTime getLastModificationDateTime() {
        return lastModificationDateTime;
    }

    public void setLastModificationDateTime(LocalDateTime dateTime) {
        this.lastModificationDateTime = dateTime;
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

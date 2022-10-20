package com.example.mydemopos.entity;

public enum FontSize {
    TINY(12),
    SMALL(14),
    MEDIUM(16),
    LARGE(20),
    LARGER(24);

    private final Integer size;
    private FontSize(int size) {
        this.size = size;
    }

    public Integer getSize() {
        return size;
    }
}
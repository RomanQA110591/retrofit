package ru.geekbrains.enums;

import lombok.Getter;

public enum CategoryType {
    FOOD("Food", 1),
    ELECTRONICS("Electronic", 2),
    FURNITURE("Furniture", 3);

    @Getter
    private final String title;
    @Getter
    private final Integer id;
    private String name;

    CategoryType(String title, Integer id) {
        this.title = title;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

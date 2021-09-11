package ru.geekbrains.dto;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@With
@ToString
public class Product {
    Integer id;
    String title;
    Integer price;
    String categoryTitle;

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getPrice() {
        return price;
    }

    public String getCategoryTitle() {
        return categoryTitle;
    }

    public Integer withPrice(Integer zeroPrice) {
        return zeroPrice;
    }

    public Product withTitle(String fixedString) {
        return null;
    }

    public Object withId (Integer wrongProductId) {
        return wrongProductId;
    }

    public String withCategoryTitle (String title) {
        return title;
    }
}
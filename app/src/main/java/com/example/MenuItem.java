package com.example;

public class MenuItem {
    private int dishId;
    private String category;
    private String nameDish;
    private int price;
    private int iconResource;
    private String version;

    public MenuItem(String nameDish, int price, int iconResource) {
        this.nameDish = nameDish;
        this.price = price;
        this.iconResource = iconResource;
    }

    public MenuItem(String category, String nameDish, int price, int iconResource) {
        this.dishId = dishId;
        this.category = category;
        this.nameDish = nameDish;
        this.price = price;
        this.iconResource = iconResource;
        this.version = version;
    }

    public MenuItem(int dishId, String category, String nameDish, int price, int iconResource, String version) {
        this.dishId = dishId;
        this.category = category;
        this.nameDish = nameDish;
        this.price = price;
        this.iconResource = iconResource;
        this.version = version;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNameDish() {
        return nameDish;
    }

    public void setNameDish(String nameDish) {
        this.nameDish = nameDish;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}

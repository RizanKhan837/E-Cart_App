package com.example.e_cartapp.model;

public class PopularProducts {
    private String name, image, description, type;
    private double price, discount, stock;
    private int id;
   // private int quantity;

    public PopularProducts(){

    }

    public PopularProducts(String name, String image, double price, String type, String description, double discount, double stock) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.type = type;
        this.description = description;
        this.discount = discount;
        this.stock = stock;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public double getDiscount() {
        return discount;
    }
    public void setDiscount(double discount) {
        this.discount = discount;
    }
    public double getStock() {
        return stock;
    }
    public void setStock(double stock) {
        this.stock = stock;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
}

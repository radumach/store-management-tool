package com.store.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private double price;
    private int quantity;

    public Product() {
    }

    public Product(Long id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }

    public static class Builder {
        private Long id;
        private String name;
        private double price;
        private int quantity;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setPrice(double price) {
            this.price = price;
            return this;
        }

        public Builder setQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Product build() {
            return new Product(id, name, price, quantity);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Product copy(Product product) {
        return builder()
                .setId(product.getId())
                .setName(product.getName())
                .setPrice(product.getPrice())
                .setQuantity(product.getQuantity())
                .build();
    }

    public static Product copy(Product product, double newPrice) {
        return builder()
                .setId(product.getId())
                .setName(product.getName())
                .setPrice(newPrice)
                .setQuantity(product.getQuantity())
                .build();
    }

}
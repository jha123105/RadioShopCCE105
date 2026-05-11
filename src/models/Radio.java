package models;

import java.io.Serializable;

public class Radio implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String model;
    private String brand;
    private RadioType type;
    private double price;
    private int quantity;
    private String frequency;
    private int powerOutput;
    private String description;

    public Radio(int id, String model, String brand, RadioType type, double price,
            int quantity, String frequency, int powerOutput, String description) {
        this.id = id;
        this.model = model;
        this.brand = brand;
        this.type = type;
        this.price = price;
        this.quantity = quantity;
        this.frequency = frequency;
        this.powerOutput = powerOutput;
        this.description = description;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public RadioType getType() {
        return type;
    }

    public void setType(RadioType type) {
        this.type = type;
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

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public int getPowerOutput() {
        return powerOutput;
    }

    public void setPowerOutput(int powerOutput) {
        this.powerOutput = powerOutput;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("%d|%s|%s|%s|%.2f|%d|%s|%d|%s",
                id, model, brand, type.name(), price, quantity, frequency, powerOutput, description);
    }
}
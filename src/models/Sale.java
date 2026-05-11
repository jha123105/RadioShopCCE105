package models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Sale implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int radioId;
    private int quantity;
    private double totalPrice;
    private String customerName;
    private String customerPhone;
    private int soldBy;
    private LocalDateTime saleDate;

    public Sale(int id, int radioId, int quantity, double totalPrice,
            String customerName, String customerPhone, int soldBy) {
        this.id = id;
        this.radioId = radioId;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.soldBy = soldBy;
        this.saleDate = LocalDateTime.now();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRadioId() {
        return radioId;
    }

    public void setRadioId(int radioId) {
        this.radioId = radioId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public int getSoldBy() {
        return soldBy;
    }

    public void setSoldBy(int soldBy) {
        this.soldBy = soldBy;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return saleDate.format(formatter);
    }

    @Override
    public String toString() {
        return String.format("%d|%d|%d|%.2f|%s|%s|%d|%s",
                id, radioId, quantity, totalPrice, customerName, customerPhone,
                soldBy, saleDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }
}
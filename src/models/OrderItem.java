package models;

import java.util.ArrayList;
import java.util.List;

public class OrderItem {

    private int orderItemId;

    private Order order;

    private Product product;

    private int quantity;

    private double itemAmount;

    // Getters and Setters

    public int getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(int orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(double itemAmount) {
        this.itemAmount = itemAmount;
    }

    @Override
    public String toString() {
        return "name:" + product.getName() +
                ", quantity:" + quantity +
                ", itemAmount:" + itemAmount;
    }
}
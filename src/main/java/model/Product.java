package model;

import repository.impl.OrderToProductRepositoryImpl;

import java.util.List;

public class Product {

    private Long id;
    private String productName;
    private Long price;

    private List<Order> orderList;

    public Product() {
    }

    public Product(Long id, String productName, Long price, List<Order> orderList) {
        this.id = id;
        this.productName = productName;
        this.price = price;
        this.orderList = orderList;
    }

    public Long getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public List<Order> getOrderList() {
        if (orderList == null) {
            orderList = OrderToProductRepositoryImpl.getInstance().findOrdersByProductId(this.id);
        }
        return orderList;
    }
}

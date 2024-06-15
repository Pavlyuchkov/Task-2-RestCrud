package model;

import repository.impl.OrderToProductRepositoryImpl;

import java.util.List;

public class Order {

    private Long id;
    private String orderStatus;
    private Customer customer;
    private List<Product> productList;

    public Order() {
    }

    public Order(Long id, String orderStatus, Customer customer, List<Product> productList) {
        this.id = id;
        this.orderStatus = orderStatus;
        this.customer = customer;
        this.productList = productList;
    }

    public Long getId() {
        return id;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Product> getProductList() {
        if (productList == null) {
            productList = OrderToProductRepositoryImpl.getInstance().findProductsByOrderId(this.id);
        }
        return productList;
    }

    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }
}

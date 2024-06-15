package dto.order;

import model.Customer;

public class OrderIncDTO {

    private String orderStatus;
    private Customer customer;

    public OrderIncDTO() {
    }

    public OrderIncDTO(String orderStatus, Customer customer) {
        this.orderStatus = orderStatus;
        this.customer = customer;
    }

    public String getOrderStatus() {
        return orderStatus;
    }
    public Customer getCustomer() {
        return customer;
    }


}

package dto.order;

import dto.customer.CustomerUpdDTO;
import dto.product.ProductUpdDTO;

import java.util.List;

public class OrderUpdDTO {

    private Long orderId;
    private String orderStatus;
    private CustomerUpdDTO customerUpdDTO;
    private List<ProductUpdDTO> productList;

    public OrderUpdDTO() {
    }

    public OrderUpdDTO(Long orderId, String orderStatus, CustomerUpdDTO customerUpdDTO, List<ProductUpdDTO> productList) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.customerUpdDTO = customerUpdDTO;
        this.productList = productList;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public CustomerUpdDTO getCustomerUpdDTO() {
        return customerUpdDTO;
    }

    public List<ProductUpdDTO> getProductList() {
        return productList;
    }

}

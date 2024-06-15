package dto.order;

import dto.customer.CustomerOutDTO;
import dto.product.ProductOutDTO;

import java.util.List;

public class OrderOutDTO {

    private Long orderId;
    private String orderStatus;
    private CustomerOutDTO customerOutDTO;
    private List<ProductOutDTO> productList;

    public OrderOutDTO() {
    }

    public OrderOutDTO(Long id, String orderStatus, CustomerOutDTO customerOutDTO, List<ProductOutDTO> productList) {
        this.orderId = id;
        this.orderStatus = orderStatus;
        this.customerOutDTO = customerOutDTO;
        this.productList = productList;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public CustomerOutDTO getCustomerOutDTO() {
        return customerOutDTO;
    }

    public List<ProductOutDTO> getProductList() {
        return productList;
    }


}

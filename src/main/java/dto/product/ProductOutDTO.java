package dto.product;

import dto.order.OrderSecondOutDTO;

import java.util.List;

public class ProductOutDTO {

    private Long productId;
    private String productName;
    private Long price;
    private List<OrderSecondOutDTO> orderList;

    public ProductOutDTO() {
    }

    public ProductOutDTO(Long productId, String productName, Long price, List<OrderSecondOutDTO> orderList) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.orderList = orderList;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public List<OrderSecondOutDTO> getOrderList() {
        return orderList;
    }
}

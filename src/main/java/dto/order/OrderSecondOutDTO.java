package dto.order;

public class OrderSecondOutDTO {

    private Long orderId;
    private String orderStatus;

    public OrderSecondOutDTO() {
    }

    public OrderSecondOutDTO(Long orderId, String orderStatus) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

}

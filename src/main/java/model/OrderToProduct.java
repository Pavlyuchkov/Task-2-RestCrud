package model;

public class OrderToProduct {

    private Long orderToProductId;
    private Long orderId;
    private Long productId;

    public OrderToProduct() {
    }

    public OrderToProduct(Long orderToProductId, Long orderId, Long productId) {
        this.orderToProductId = orderToProductId;
        this.orderId = orderId;
        this.productId = productId;
    }

    public Long getOrderToProductId() {
        return orderToProductId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}

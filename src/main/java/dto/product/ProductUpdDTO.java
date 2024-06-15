package dto.product;

public class ProductUpdDTO {

    private Long productId;
    private String productName;
    private Long price;

    public ProductUpdDTO(Long productId, String productName, Long price) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
    }

    public ProductUpdDTO() {
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public Long getPrice() {
        return price;
    }
}
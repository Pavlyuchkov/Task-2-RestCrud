package dto.product;

public class ProductIncDTO {

    private String productName;
    private Long price;

    public ProductIncDTO() {
    }

    public ProductIncDTO(String productName, Long price) {
        this.productName = productName;
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public Long getPrice() {
        return price;
    }
}

package mapper.impl;

import dto.order.OrderSecondOutDTO;
import dto.product.ProductIncDTO;
import dto.product.ProductOutDTO;
import dto.product.ProductUpdDTO;
import mapper.ProductDTOMapper;
import model.Product;
import java.util.List;

public class ProductDTOMapperImpl implements ProductDTOMapper {

    private static ProductDTOMapper instance;

    private ProductDTOMapperImpl() {
    }

    public static synchronized ProductDTOMapper getInstance() {
        if (instance == null) {
            instance = new ProductDTOMapperImpl();
        }
        return instance;
    }

    @Override
    public Product map(ProductIncDTO dto) {
        return new Product(
                null,
                dto.getProductName(),
                dto.getPrice(),
                null
        );
    }

    @Override
    public ProductOutDTO map(Product product) {
        List<OrderSecondOutDTO> orderList = product.getOrderList()
                .stream().map(user -> new OrderSecondOutDTO(
                        user.getId(),
                        user.getOrderStatus()
                )).toList();

        return new ProductOutDTO(
                product.getId(),
                product.getProductName(),
                product.getPrice(),
                orderList
        );
    }

    @Override
    public Product map(ProductUpdDTO updDTO) {
        return new Product(
                updDTO.getProductId(),
                updDTO.getProductName(),
                updDTO.getPrice(),
                null
        );
    }

    @Override
    public List<ProductOutDTO> map(List<Product> productList) {
        return productList.stream().map(this::map).toList();
    }

    @Override
    public List<Product> mapUpdateList(List<ProductUpdDTO> productList) {
        return productList.stream().map(this::map).toList();
    }
}
package mapper;

import dto.product.ProductIncDTO;
import dto.product.ProductOutDTO;
import dto.product.ProductUpdDTO;
import mapper.impl.ProductDTOMapperImpl;
import model.Order;
import model.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ProductDTOMapperImplTest {

    private ProductDTOMapper productDTOMapper;

    @BeforeEach
    void setUp() {
        productDTOMapper = ProductDTOMapperImpl.getInstance();
    }


    @Test
    void mapIncDto() {
        ProductIncDTO productIncDTO = new ProductIncDTO("Помидоры", 4L);
        Product result = productDTOMapper.map(productIncDTO);

        Assertions.assertNull(result.getId());
        Assertions.assertEquals(productIncDTO.getProductName(), result.getProductName());
    }

    @Test
    void mapOutDto() {
        Product product = new Product(30L, "Cыр", 8L, List.of(new Order(), new Order()));

        ProductOutDTO result = productDTOMapper.map(product);

        Assertions.assertEquals(product.getId(), result.getProductId());
        Assertions.assertEquals(product.getProductName(), result.getProductName());
        Assertions.assertEquals(product.getOrderList().size(), result.getOrderList().size());
    }

    @Test
    void mapOutDtoList() {
        List<Product> productList = List.of(
                new Product(1L, "Груши", 7L, List.of()),
                new Product(2L, "Бананы", 8L, List.of()),
                new Product(3L, "Ананасы", 9L, List.of())
        );
        List<ProductOutDTO> result = productDTOMapper.map(productList);

        Assertions.assertEquals(3, result.size());
    }

    @Test
    void mapUpdDto() {
        ProductUpdDTO productUpdDTO = new ProductUpdDTO(18L, "Сливки", 5L);

        Product result = productDTOMapper.map(productUpdDTO);
        Assertions.assertEquals(productUpdDTO.getProductId(), result.getId());
        Assertions.assertEquals(productUpdDTO.getProductName(), result.getProductName());
    }

    @Test
    void mapUpdDtoList() {
        List<ProductUpdDTO> productUpdDTOList = List.of(
                new ProductUpdDTO(21L, "Груши", 7L),
                new ProductUpdDTO(22L, "Бананы", 8L),
                new ProductUpdDTO(23L, "Ананасы", 9L)
        );
        List<Product> result = productDTOMapper.mapUpdateList(productUpdDTOList);

        Assertions.assertEquals(3, result.size());
    }


}

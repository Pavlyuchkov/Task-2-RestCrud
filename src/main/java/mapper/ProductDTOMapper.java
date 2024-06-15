package mapper;

import dto.product.ProductIncDTO;
import dto.product.ProductOutDTO;
import dto.product.ProductUpdDTO;
import model.Product;

import java.util.List;

public interface ProductDTOMapper {

    Product map(ProductIncDTO productIncDTO);

    ProductOutDTO map(Product product);

    Product map(ProductUpdDTO productNewDTO);

    List<ProductOutDTO> map(List<Product> productList);

    List<Product> mapUpdateList(List<ProductUpdDTO> productList);

}

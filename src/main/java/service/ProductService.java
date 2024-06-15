package service;

import dto.product.ProductIncDTO;
import dto.product.ProductOutDTO;
import dto.product.ProductUpdDTO;
import exception.NotFoundException;

import java.util.List;

public interface ProductService {

    ProductOutDTO save(ProductIncDTO product);

    void update(ProductUpdDTO product) throws NotFoundException;

    ProductOutDTO findById(Long productId) throws NotFoundException;

    List<ProductOutDTO> findAll();

    void delete(Long productId) throws NotFoundException;

    void deleteProductFromOrder(Long productId, Long userId) throws NotFoundException; // исправить на исключить Продукт из Закзаа

    void addProductToOrder(Long productId, Long userId) throws NotFoundException;// исправить на добавить Продукт в Заказ

}

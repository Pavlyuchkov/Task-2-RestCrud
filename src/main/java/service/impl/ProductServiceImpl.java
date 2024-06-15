package service.impl;

import dto.product.ProductIncDTO;
import dto.product.ProductOutDTO;
import dto.product.ProductUpdDTO;
import exception.NotFoundException;
import mapper.ProductDTOMapper;
import mapper.impl.ProductDTOMapperImpl;
import model.OrderToProduct;
import model.Product;
import repository.OrderRepository;
import repository.OrderToProductRepository;
import repository.ProductRepository;
import repository.impl.OrderRepositoryImpl;
import repository.impl.OrderToProductRepositoryImpl;
import repository.impl.ProductRepositoryImpl;
import service.ProductService;

import java.util.List;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository = ProductRepositoryImpl.getInstance();
    private final OrderRepository orderRepository = OrderRepositoryImpl.getInstance();
    private final OrderToProductRepository orderToProductRepository = OrderToProductRepositoryImpl.getInstance();
    private static final ProductDTOMapper PRODUCT_DTO_MAPPER = ProductDTOMapperImpl.getInstance();
    private static ProductService instance;


    private ProductServiceImpl() {
    }

    public static ProductService getInstance() {
        if (instance == null) {
            instance = new ProductServiceImpl();
        }
        return instance;
    }

    private void checkProductIfExists(Long productId) throws NotFoundException {
        if (!productRepository.existsById(productId)) {
            throw new NotFoundException("Выбранный продукт не найден!");
        }
    }

    @Override
    public ProductOutDTO save(ProductIncDTO productIncDTO) {
        Product product = PRODUCT_DTO_MAPPER.map(productIncDTO);
        product = productRepository.save(product);
        return PRODUCT_DTO_MAPPER.map(product);
    }

    @Override
    public void update(ProductUpdDTO productUpdDTO) throws NotFoundException {
        checkProductIfExists(productUpdDTO.getProductId());
        Product product = PRODUCT_DTO_MAPPER.map(productUpdDTO);
        productRepository.update(product);
    }

    @Override
    public ProductOutDTO findById(Long productId) throws NotFoundException {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new NotFoundException("Выбранный продукт не найден!"));
        return PRODUCT_DTO_MAPPER.map(product);
    }

    @Override
    public List<ProductOutDTO> findAll() {
        List<Product> productList = productRepository.findAll();
        return PRODUCT_DTO_MAPPER.map(productList);
    }

    @Override
    public void delete(Long productId) throws NotFoundException {
        checkProductIfExists(productId);
        productRepository.deleteById(productId);
    }

    @Override
    public void deleteProductFromOrder(Long productId, Long orderId) throws NotFoundException {
        checkProductIfExists(productId);
        if (orderRepository.existsById(orderId)) {
            OrderToProduct linkOrderProduct = orderToProductRepository.findByOrderIdAndProductId(orderId, productId)
                    .orElseThrow(() -> new NotFoundException("В выбранном заказе нет такого продукта!"));

            orderToProductRepository.deleteById(linkOrderProduct.getOrderToProductId());
        } else {
            throw new NotFoundException("Выбранного заказа не существует!");
        }
    }

    @Override
    public void addProductToOrder(Long productId, Long orderId) throws NotFoundException {
        checkProductIfExists(productId);
        if (orderRepository.existsById(orderId)) {
            OrderToProduct orderToProduct = new OrderToProduct(
                    null,
                    orderId,
                    productId
            );
            orderToProductRepository.save(orderToProduct);
        } else {
            throw new NotFoundException("Выбранного заказа не существует!");
        }

    }

}

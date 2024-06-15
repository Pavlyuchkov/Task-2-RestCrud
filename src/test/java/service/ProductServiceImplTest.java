package service;

import dto.product.ProductIncDTO;
import dto.product.ProductOutDTO;
import dto.product.ProductUpdDTO;
import exception.NotFoundException;
import model.OrderToProduct;
import model.Product;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import repository.OrderRepository;
import repository.OrderToProductRepository;
import repository.ProductRepository;
import repository.impl.OrderRepositoryImpl;
import repository.impl.OrderToProductRepositoryImpl;
import repository.impl.ProductRepositoryImpl;
import service.impl.ProductServiceImpl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

public class ProductServiceImplTest {

    private static ProductService productService;
    private static ProductRepository mockProductRepository;
    private static OrderRepository mockOrderRepository;
    private static OrderToProductRepository mockOrderToProductRepository;
    private static ProductRepositoryImpl firstProductInstance;
    private static OrderRepositoryImpl firstOrderInstance;
    private static OrderToProductRepositoryImpl firstOrderToProductInstance;

    private static void setMock(ProductRepository mock) {
        try {
            Field instance = ProductRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            firstProductInstance = (ProductRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setMock(OrderRepository mock) {
        try {
            Field instance = OrderRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            firstOrderInstance = (OrderRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setMock(OrderToProductRepository mock) {
        try {
            Field instance = OrderToProductRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            firstOrderToProductInstance = (OrderToProductRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockProductRepository = Mockito.mock(ProductRepository.class);
        setMock(mockProductRepository);
        mockOrderRepository = Mockito.mock(OrderRepository.class);
        setMock(mockOrderRepository);
        mockOrderToProductRepository = Mockito.mock(OrderToProductRepository.class);
        setMock(mockOrderToProductRepository);

        productService = ProductServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = ProductRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, firstProductInstance);

        instance = OrderRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, firstOrderInstance);

        instance = OrderToProductRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, firstOrderToProductInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockProductRepository);
    }

    @Test
    void findAll() {
        productService.findAll();
        Mockito.verify(mockProductRepository).findAll();
    }

    @Test
    void findById() throws NotFoundException {

        Optional<Product> product = Optional.of(new Product(1L, "Хлеб", 2L, List.of()));

        Mockito.doReturn(true).when(mockProductRepository).existsById(Mockito.any());
        Mockito.doReturn(product).when(mockProductRepository).findById(Mockito.anyLong());

        ProductOutDTO dto = productService.findById(1L);

        Assertions.assertEquals(1L, dto.getProductId());
    }

    @Test
    void findByIdNotFound() {

        Mockito.doReturn(false).when(mockProductRepository).existsById(Mockito.any());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> productService.findById(30L), "Продукт не найден!"
        );
        Assertions.assertEquals("Выбранный продукт не найден!", exception.getMessage());
    }

    @Test
    void save() {

        ProductIncDTO dto = new ProductIncDTO("Творог", 3L);
        Product product = new Product(10L, "Творог", 3L, List.of());

        Mockito.doReturn(product).when(mockProductRepository).save(Mockito.any(Product.class));

        ProductOutDTO result = productService.save(dto);

        Assertions.assertEquals(10L, result.getProductId());
    }

    @Test
    void update() throws NotFoundException {

        ProductUpdDTO dto = new ProductUpdDTO(5L, "Кофе", 13L);

        Mockito.doReturn(true).when(mockProductRepository).existsById(Mockito.any());

        productService.update(dto);

        ArgumentCaptor<Product> argumentCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(mockProductRepository).update(argumentCaptor.capture());

        Product result = argumentCaptor.getValue();
        Assertions.assertEquals(5L, result.getId());
    }

    @Test
    void updateNotFound() {
        ProductUpdDTO dto = new ProductUpdDTO(30L, "Сметана", 5L);

        Mockito.doReturn(false).when(mockProductRepository).existsById(Mockito.any());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> productService.update(dto), "Продукт не найден!"
        );
        Assertions.assertEquals("Выбранный продукт не найден!", exception.getMessage());
    }

    @Test
    void addProductToOrder() throws NotFoundException {

        Mockito.doReturn(true).when(mockOrderRepository).existsById(Mockito.any());
        Mockito.doReturn(true).when(mockProductRepository).existsById(Mockito.any());

        productService.addProductToOrder(3L, 5L);

        ArgumentCaptor<OrderToProduct> argumentCaptor = ArgumentCaptor.forClass(OrderToProduct.class);
        Mockito.verify(mockOrderToProductRepository).save(argumentCaptor.capture());
        OrderToProduct result = argumentCaptor.getValue();

        Assertions.assertEquals(5L, result.getOrderId());
        Assertions.assertEquals(3L, result.getProductId());
    }

    @Test
    void delete() throws NotFoundException {

        Mockito.doReturn(true).when(mockProductRepository).existsById(Mockito.any());
        productService.delete(7L);

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockProductRepository).deleteById(argumentCaptor.capture());

        Long result = argumentCaptor.getValue();
        Assertions.assertEquals(7L, result);
    }

    @Test
    void deleteProductFromOrder() throws NotFoundException {
        Optional<OrderToProduct> link = Optional.of(new OrderToProduct(3L, 1L, 2L));

        Mockito.doReturn(true).when(mockOrderRepository).existsById(Mockito.any());
        Mockito.doReturn(true).when(mockProductRepository).existsById(Mockito.any());
        Mockito.doReturn(link).when(mockOrderToProductRepository).findByOrderIdAndProductId(Mockito.anyLong(), Mockito.anyLong());

        productService.deleteProductFromOrder(1L, 1L);

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockOrderToProductRepository).deleteById(argumentCaptor.capture());
        Long result = argumentCaptor.getValue();
        Assertions.assertEquals(3L, result);
    }


}

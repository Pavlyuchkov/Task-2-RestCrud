package repository;

import config.TestContainerManagerTest;
import model.Customer;
import model.Order;
import model.Product;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Testcontainers;
import repository.impl.OrderRepositoryImpl;

import java.util.List;
import java.util.Optional;

import static config.TestContainerManagerTest.INIT_SQL;
import static config.TestContainerManagerTest.postgres;

@Testcontainers
public class OrderRepositoryImplTest {

    public static OrderRepository orderRepository;
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() {
        TestContainerManagerTest.start();
        orderRepository = OrderRepositoryImpl.getInstance();
        jdbcDatabaseDelegate = new JdbcDatabaseDelegate(postgres, "");
    }

    @AfterAll
    static void afterAll() {
        TestContainerManagerTest.stop();
    }

    @BeforeEach
    void setUp() {
        ScriptUtils.runInitScript(jdbcDatabaseDelegate, INIT_SQL);
    }

    @Test
    void findAll() {
        int expectedSize = 8;
        int resultSize = orderRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1, true",
            "5, true",
            "7, true",
            "15, false",
            "30, false"
    })
    void findById(Long expectedId, Boolean expectedValue) {
        Optional<Order> order = orderRepository.findById(expectedId);
        Assertions.assertEquals(expectedValue, order.isPresent());
        order.ifPresent(value -> Assertions.assertEquals(expectedId, value.getId()));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1, true",
            "5, true",
            "7, true",
            "15, false",
            "30, false"
    })
    void existsById(Long orderId, Boolean expectedValue) {
        boolean ifExists = orderRepository.existsById(orderId);

        Assertions.assertEquals(expectedValue, ifExists);
    }

    @Test
    void save() {
        String expectedStatus = "Оплачен";

        Order order = new Order(
                null,
                expectedStatus,
                null,
                null
        );
        order = orderRepository.save(order);
        Optional<Order> optionalOrder = orderRepository.findById(order.getId());

        Assertions.assertTrue(optionalOrder.isPresent());
        Assertions.assertEquals(expectedStatus, optionalOrder.get().getOrderStatus());
    }

    @Test
    void update() {
        String expectedStatus = "Выполнен";
        Long expectedCustomerId = 5L;

        Order updOrder = orderRepository.findById(6L).get();

        List<Product> productList = updOrder.getProductList();
        int productListSize = updOrder.getProductList().size();
        Customer oldCustomer = updOrder.getCustomer();

        Assertions.assertNotEquals(expectedCustomerId, updOrder.getCustomer().getId());
        Assertions.assertNotEquals(expectedStatus, updOrder.getOrderStatus());

        updOrder.setOrderStatus(expectedStatus);
        orderRepository.update(updOrder);

        Order resultOrder = orderRepository.findById(6L).get();

        Assertions.assertEquals(expectedStatus, resultOrder.getOrderStatus());
        Assertions.assertEquals(productListSize, resultOrder.getProductList().size());
        Assertions.assertEquals(oldCustomer.getId(), resultOrder.getCustomer().getId());

        updOrder.setProductList(List.of());
        updOrder.setCustomer(new Customer(expectedCustomerId, null, null));
        orderRepository.update(updOrder);
        resultOrder = orderRepository.findById(6L).get();

        Assertions.assertEquals(0, resultOrder.getProductList().size());
        Assertions.assertEquals(expectedCustomerId, resultOrder.getCustomer().getId());

        productList.add(new Product(1L, "Хлеб", 2L, null));
        productList.add(new Product(2L, "Молоко", 3L, null));
        updOrder.setProductList(productList);
        orderRepository.update(updOrder);
        resultOrder = orderRepository.findById(6L).get();

        Assertions.assertEquals(5, resultOrder.getProductList().size());

        productList.remove(2);
        updOrder.setProductList(productList);
        orderRepository.update(updOrder);
        resultOrder = orderRepository.findById(6L).get();

        Assertions.assertEquals(4, resultOrder.getProductList().size());

    }

    @Test
    void deleteById() {
        Boolean expectedValue = true;
        int expectedSize = orderRepository.findAll().size();

        Order deletingOrder = new Order(
                null,
                "Статус",
                null,
                null
        );
        deletingOrder = orderRepository.save(deletingOrder);

        boolean resultDelete = orderRepository.deleteById(deletingOrder.getId());
        int orderListRemaining = orderRepository.findAll().size();

        Assertions.assertEquals(expectedValue, resultDelete);
        Assertions.assertEquals(expectedSize, orderListRemaining);
    }
}

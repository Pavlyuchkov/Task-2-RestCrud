package repository;

import config.TestContainerManagerTest;
import model.OrderToProduct;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Testcontainers;
import repository.impl.OrderToProductRepositoryImpl;

import java.util.Optional;

import static config.TestContainerManagerTest.INIT_SQL;
import static config.TestContainerManagerTest.postgres;

@Testcontainers
public class OrderToProductRepositoryImplTest {

    public static OrderToProductRepository orderToProductRepository;
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() {
        TestContainerManagerTest.start();
        orderToProductRepository = OrderToProductRepositoryImpl.getInstance();
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
        int expectedSize = 21;
        int resultSize = orderToProductRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1, 3",
            "4, 3",
            "7, 2",
            "8, 2",
            "30, 0",
            "45, 0"
    })
    void findAllByOrderId(Long orderId, int expectedSize) {
        int resultSize = orderToProductRepository.findAllByOrderId(orderId).size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1, 4",
            "2, 2",
            "3, 4",
            "5, 3",
            "45, 0",
            "50, 0",
    })
    void findAllByProductId(Long productId, int expectedSize) {
        int resultSize = orderToProductRepository.findAllByProductId(productId).size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1, true, 1, 3",
            "5, true, 2, 1",
            "9, true, 4, 4",
            "12, true, 5, 6",
            "30, false, 0, 0",
            "35, false, 0, 0"
    })
    void findById(Long expectedId, Boolean expectedValue, Long expectedOrderId, Long expectedProductId) {
        Optional<OrderToProduct> link = orderToProductRepository.findById(expectedId);

        Assertions.assertEquals(expectedValue, link.isPresent());
        if (link.isPresent()) {
            Assertions.assertEquals(expectedId, link.get().getOrderToProductId());
            Assertions.assertEquals(expectedOrderId, link.get().getOrderId());
            Assertions.assertEquals(expectedProductId, link.get().getProductId());
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1, true",
            "5, true",
            "9, true",
            "12, true",
            "30, false",
            "35, false"
    })
    void existsById(Long expectedId, Boolean expectedValue) {
        Boolean resultValue = orderToProductRepository.existsById(expectedId);

        Assertions.assertEquals(expectedValue, resultValue);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1, 4",
            "2, 2",
            "3, 4",
            "5, 3",
            "45, 0",
            "50, 0",
    })
    void findOrdersByProductId(Long productId, int expectedSize) {
        int resultSize = orderToProductRepository.findOrdersByProductId(productId).size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1, 3",
            "4, 3",
            "7, 2",
            "8, 2",
            "30, 0",
            "45, 0"
    })
    void findProductsByOrderId(Long orderId, int expectedSize) {
        int resultSize = orderToProductRepository.findProductsByOrderId(orderId).size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "2, 4, true",
            "4, 1, true",
            "6, 2, true",
            "7, 6, true",
            "3, 6, false",
            "8, 2, false"
    })
    void findByOrderIdAndProductId(Long orderId, Long productId, Boolean expectedValue) {
        Optional<OrderToProduct> link = orderToProductRepository.findByOrderIdAndProductId(orderId, productId);

        Assertions.assertEquals(expectedValue, link.isPresent());
    }

    @Test
    void save() {
        Long expectedOrderId = 4L;
        Long expectedProductId = 5L;
        OrderToProduct link = new OrderToProduct(
                null,
                expectedOrderId,
                expectedProductId
        );
        link = orderToProductRepository.save(link);
        Optional<OrderToProduct> resultLink = orderToProductRepository.findById(link.getOrderToProductId());

        Assertions.assertTrue(resultLink.isPresent());
        Assertions.assertEquals(expectedOrderId, resultLink.get().getOrderId());
        Assertions.assertEquals(expectedProductId, resultLink.get().getProductId());
    }

    @Test
    void update() {
        Long expectedOrderId = 5L;
        Long expectedProductId = 5L;

        OrderToProduct link = orderToProductRepository.findById(9L).get();

        Long oldProductId = link.getProductId();
        Long oldOrderId = link.getOrderId();

        Assertions.assertNotEquals(expectedOrderId, oldOrderId);
        Assertions.assertNotEquals(expectedProductId, oldProductId);

        link.setOrderId(expectedOrderId);
        link.setProductId(expectedProductId);

        orderToProductRepository.update(link);

        OrderToProduct resultLink = orderToProductRepository.findById(9L).get();

        Assertions.assertEquals(link.getOrderToProductId(), resultLink.getOrderToProductId());
        Assertions.assertEquals(expectedOrderId, resultLink.getOrderId());
        Assertions.assertEquals(expectedProductId, resultLink.getProductId());
    }

    @Test
    void deleteById() {
        Boolean expectedValue = true;
        int expectedSize = orderToProductRepository.findAll().size();

        OrderToProduct link = new OrderToProduct(null, 7L, 5L);
        link = orderToProductRepository.save(link);

        int firstCondition = orderToProductRepository.findAll().size();
        Assertions.assertNotEquals(expectedSize, firstCondition);

        boolean resultDelete = orderToProductRepository.deleteById(link.getOrderToProductId());

        int secondCondition = orderToProductRepository.findAll().size();

        Assertions.assertEquals(expectedValue, resultDelete);
        Assertions.assertEquals(expectedSize, secondCondition);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1, true",
            "3, true",
            "5, true",
            "15, false",
            "20, false"
    })
    void deleteByOrderId(Long expectedOrderId, Boolean expectedValue) {
        int beforeSize = orderToProductRepository.findAllByOrderId(expectedOrderId).size();
        Boolean resultDelete = orderToProductRepository.deleteByOrderId(expectedOrderId);

        int afterDelete = orderToProductRepository.findAllByOrderId(expectedOrderId).size();

        Assertions.assertEquals(expectedValue, resultDelete);
        if (beforeSize != 0) {
            Assertions.assertNotEquals(beforeSize, afterDelete);
        }
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1, true",
            "3, true",
            "5, true",
            "15, false",
            "20, false"
    })
    void deleteByProductId(Long expectedProductId, Boolean expectedValue) {
        int beforeSize = orderToProductRepository.findAllByProductId(expectedProductId).size();
        Boolean resultDelete = orderToProductRepository.deleteByProductId(expectedProductId);

        int afterDelete = orderToProductRepository.findAllByProductId(expectedProductId).size();

        Assertions.assertEquals(expectedValue, resultDelete);
        if (beforeSize != 0) {
            Assertions.assertNotEquals(beforeSize, afterDelete);
        }
    }

















}

package repository;

import config.TestContainerManagerTest;
import model.Product;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Testcontainers;
import repository.impl.ProductRepositoryImpl;

import java.util.List;
import java.util.Optional;

import static config.TestContainerManagerTest.INIT_SQL;
import static config.TestContainerManagerTest.postgres;

@Testcontainers
public class ProductRepositoryImplTest {

    public static ProductRepository productRepository;
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() {
        TestContainerManagerTest.start();
        productRepository = ProductRepositoryImpl.getInstance();
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
        int expectedSize = 7;
        int resultSize = productRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "5; true",
            "7; true",
            "15; false",
            "30; false"
    }, delimiter = ';')
    void findById(Long expectedId, Boolean expectedValue) {
        Optional<Product> product = productRepository.findById(expectedId);

        Assertions.assertEquals(expectedValue, product.isPresent());
        product.ifPresent(value -> Assertions.assertEquals(expectedId, value.getId()));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1; true",
            "5; true",
            "7; true",
            "15; false",
            "30; false"
    }, delimiter = ';')
    void existsById(Long productId, Boolean expectedValue) {
        boolean ifExists = productRepository.existsById(productId);

        Assertions.assertEquals(expectedValue, ifExists);
    }

    @Test
    void save() {
        String expectedName = "Чипсы";
        Long expectedPrice = 3L;
        Product product = new Product(
                null,
                expectedName,
                expectedPrice,
                null
        );
        product = productRepository.save(product);
        Optional<Product> optionalProduct = productRepository.findById(product.getId());

        Assertions.assertTrue(optionalProduct.isPresent());
        Assertions.assertEquals(expectedName, optionalProduct.get().getProductName());
        Assertions.assertEquals(expectedPrice, optionalProduct.get().getPrice());
    }

    @Test
    void update() {
        String expectedName = "Сосиски";
        Long expectedPrice = 6L;

        Product product = productRepository.findById(6L).get();
        String oldName = product.getProductName();
        int expectedOrders = product.getOrderList().size();
        product.setProductName(expectedName);
        product.setPrice(expectedPrice);
        productRepository.update(product);

        Product result = productRepository.findById(6L).get();
        int resultOrders = result.getOrderList().size();

        Assertions.assertNotEquals(expectedName, oldName);
        Assertions.assertEquals(expectedName, result.getProductName());
        Assertions.assertEquals(expectedOrders, resultOrders);
    }

    @Test
    void deleteById() {
        Boolean expectedValue = true;
        int expectedSize = productRepository.findAll().size();

        Product deletingProduct = new Product(null, "Сухарики", 2L, List.of());
        deletingProduct = productRepository.save(deletingProduct);

        int resultSizeBefore = productRepository.findAll().size();
        Assertions.assertNotEquals(expectedSize, resultSizeBefore);

        boolean resultDelete = productRepository.deleteById(deletingProduct.getId());
        int resultSizeAfter = productRepository.findAll().size();

        Assertions.assertEquals(expectedValue, resultDelete);
        Assertions.assertEquals(expectedSize, resultSizeAfter);

    }
}
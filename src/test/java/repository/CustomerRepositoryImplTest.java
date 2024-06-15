package repository;

import config.TestContainerManagerTest;
import model.Customer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.testcontainers.ext.ScriptUtils;
import org.testcontainers.jdbc.JdbcDatabaseDelegate;
import org.testcontainers.junit.jupiter.Testcontainers;
import repository.impl.CustomerRepositoryImpl;
import java.util.Optional;

import static config.TestContainerManagerTest.INIT_SQL;
import static config.TestContainerManagerTest.postgres;


@Testcontainers
public class CustomerRepositoryImplTest {

    public static CustomerRepository customerRepository;
    private static JdbcDatabaseDelegate jdbcDatabaseDelegate;

    @BeforeAll
    static void beforeAll() {
        TestContainerManagerTest.start();
        customerRepository = CustomerRepositoryImpl.getInstance();
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
        int expectedSize = 5;
        int resultSize = customerRepository.findAll().size();

        Assertions.assertEquals(expectedSize, resultSize);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1, true",
            "3, true",
            "5, true",
            "90, false",
            "150, false"
    })
    void findById(Long expectedId, Boolean expectedValue) {
        Optional<Customer> customer = customerRepository.findById(expectedId);

        Assertions.assertEquals(expectedValue, customer.isPresent());
        customer.ifPresent(value -> Assertions.assertEquals(expectedId, value.getId()));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1, true",
            "3, true",
            "5, true",
            "90, false",
            "150, false"
    })
    void existsById(Long customerId, Boolean expectedValue) {
        boolean ifExists = customerRepository.existsById(customerId);

        Assertions.assertEquals(expectedValue, ifExists);
    }

    @Test
    void save() {
        String expectedName = "Александр";
        String expectedSurname = "Александров";
        Customer customer = new Customer(null, expectedName, expectedSurname);
        customer = customerRepository.save(customer);
        Optional<Customer> result = customerRepository.findById(customer.getId());

        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(expectedName, result.get().getName());
        Assertions.assertEquals(expectedSurname, result.get().getSurname());
    }

    @Test
    void update() {
        String expectedName = "Никита";
        String expectedSurname = "Никитин";

        Customer updCustomer = customerRepository.findById(1L).get();
        String oldCustomerName = updCustomer.getName();

        updCustomer.setName(expectedName);
        updCustomer.setSurname(expectedSurname);
        customerRepository.update(updCustomer);

        Customer customer = customerRepository.findById(1L).get();

        Assertions.assertNotEquals(expectedName, oldCustomerName);
        Assertions.assertEquals(expectedName, customer.getName());
        Assertions.assertEquals(expectedSurname, customer.getSurname());
    }

    @Test
    void deleteById() {
        Boolean expectedValue = true;
        int expectedSize = customerRepository.findAll().size();

        Customer customer = new Customer(null, "Егор", "Егоров");
        customer = customerRepository.save(customer);

        boolean resultDelete = customerRepository.deleteById(customer.getId());
        int customerListSize = customerRepository.findAll().size();

        Assertions.assertEquals(expectedValue, resultDelete);
        Assertions.assertEquals(expectedSize, customerListSize);
    }


}

package mapper;

import dto.customer.CustomerIncDTO;
import dto.customer.CustomerOutDTO;
import dto.customer.CustomerUpdDTO;
import mapper.impl.CustomerDTOMapperImpl;
import model.Customer;
import org.junit.jupiter.api.*;
import java.util.List;

public class CustomerDTOMapperImplTest {

    private static Customer customer;
    private static CustomerIncDTO customerIncDTO;
    private static CustomerUpdDTO customerUpdDTO;
    private CustomerDTOMapper customerDTOMapper;

    @BeforeAll
    static void beforeAll() {
        customer = new Customer(
                10L,
                "Nikolay",
                "Nikolayev"
        );

        customerIncDTO = new CustomerIncDTO(
                "Nikolay",
                "Nikolayev"
        );

        customerUpdDTO = new CustomerUpdDTO(
                5L,
                "Nikolay",
                "Nikolayev"
        );
    }

    @BeforeEach
    void setUp() {
        customerDTOMapper = CustomerDTOMapperImpl.getInstance();
    }


    @Test
    void mapIncDto() {
        Customer result = customerDTOMapper.map(customerIncDTO);

        Assertions.assertNull(result.getId());
        Assertions.assertEquals(customerIncDTO.getCustomerName(), result.getName());
        Assertions.assertEquals(customerIncDTO.getCustomerSurname(), result.getSurname());
    }

    @Test
    void mapOutDto() {
        CustomerOutDTO result = customerDTOMapper.map(customer);

        Assertions.assertEquals(customer.getId(), result.getCustomerId());
        Assertions.assertEquals(customer.getName(), result.getCustomerName());
        Assertions.assertEquals(customer.getSurname(), result.getCustomerSurname());
    }

    @Test
    void mapUpdDto() {
        Customer result = customerDTOMapper.map(customerUpdDTO);

        Assertions.assertEquals(customerUpdDTO.getCustomerId(), result.getId());
        Assertions.assertEquals(customerUpdDTO.getCustomerName(), result.getName());
        Assertions.assertEquals(customerUpdDTO.getCustomerSurname(), result.getSurname());
    }

    @Test
    void testMapList() {
        List<CustomerOutDTO> customerOutDTOList = customerDTOMapper.map(
                List.of(
                        customer,
                        customer,
                        customer,
                        customer,
                        customer
                )
        );
        Assertions.assertEquals(5, customerOutDTOList.size());
    }
}

package service;

import dto.customer.CustomerIncDTO;
import dto.customer.CustomerOutDTO;
import dto.customer.CustomerUpdDTO;
import exception.NotFoundException;
import model.Customer;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.CustomerRepository;
import repository.impl.CustomerRepositoryImpl;
import service.impl.CustomerServiceImpl;
import java.lang.reflect.Field;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    private static CustomerRepositoryImpl firstInstance;
    private static CustomerService customerService;
    private static CustomerRepository mockCustomerRepository;

    private static void setMock(CustomerRepository mock) {
        try {
            Field instance = CustomerRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            firstInstance = (CustomerRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockCustomerRepository = Mockito.mock(CustomerRepository.class);
        setMock(mockCustomerRepository);
        customerService = CustomerServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = CustomerRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, firstInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockCustomerRepository);
    }

    @Test
    void findAll() {
        customerService.findAll();
        Mockito.verify(mockCustomerRepository).findAll();
    }

    @Test
    void findById() throws NotFoundException {

        Optional<Customer> customer = Optional.of(new Customer(4L, "Dmitry", "Dmitriev"));

        Mockito.doReturn(true).when(mockCustomerRepository).existsById(Mockito.any());
        Mockito.doReturn(customer).when(mockCustomerRepository).findById(Mockito.anyLong());

        CustomerOutDTO dto = customerService.findById(4L);

        Assertions.assertEquals(4L, dto.getCustomerId());
        Assertions.assertEquals("Dmitry", dto.getCustomerName());
        Assertions.assertEquals("Dmitriev", dto.getCustomerSurname());
    }

    @Test
    void findByIdNotFound() {

        Mockito.doReturn(false).when(mockCustomerRepository).existsById(Mockito.any());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> customerService.findById(1L), "Покупатель не найден!"
        );
        Assertions.assertEquals("Выбранный Покупатель не найден!", exception.getMessage());
    }

    @Test
    void save() {

        CustomerIncDTO dto = new CustomerIncDTO("Petr", "Petrov");
        Customer customer = new Customer(1L, "Petr", "Petrov");

        Mockito.doReturn(customer).when(mockCustomerRepository).save(Mockito.any(Customer.class));

        CustomerOutDTO result = customerService.save(dto);

        Assertions.assertEquals(1L, result.getCustomerId());
        Assertions.assertEquals("Petr", result.getCustomerName());
        Assertions.assertEquals("Petrov", result.getCustomerSurname());
    }

    @Test
    void update() throws NotFoundException {

        CustomerUpdDTO dto = new CustomerUpdDTO(2L, "Maxim", "Maximov");

        Mockito.doReturn(true).when(mockCustomerRepository).existsById(Mockito.any());

        customerService.update(dto);

        ArgumentCaptor<Customer> argumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(mockCustomerRepository).update(argumentCaptor.capture());
        Customer result = argumentCaptor.getValue();

        Assertions.assertEquals(2L, result.getId());
        Assertions.assertEquals("Maxim", result.getName());
        Assertions.assertEquals("Maximov", result.getSurname());
    }

    @Test
    void updateNotFound() {
        CustomerUpdDTO dto = new CustomerUpdDTO(3L, "Georgy", "Georgiev");

        Mockito.doReturn(false).when(mockCustomerRepository).existsById((Mockito.any()));

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> customerService.update(dto), "Покупатель не найден!"
        );
        Assertions.assertEquals("Выбранный Покупатель не найден!", exception.getMessage());
    }

    @Test
    void delete() throws NotFoundException {

        Mockito.doReturn(true).when(mockCustomerRepository).existsById(30L);

        customerService.delete(30L);

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockCustomerRepository).deleteById(argumentCaptor.capture());

        Long result = argumentCaptor.getValue();
        Assertions.assertEquals(30L, result);
    }
}
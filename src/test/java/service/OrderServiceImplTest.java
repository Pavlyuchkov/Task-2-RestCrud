package service;

import dto.customer.CustomerUpdDTO;
import dto.order.OrderIncDTO;
import dto.order.OrderOutDTO;
import dto.order.OrderUpdDTO;
import exception.NotFoundException;
import model.Customer;
import model.Order;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import repository.OrderRepository;
import repository.impl.OrderRepositoryImpl;
import service.impl.OrderServiceImpl;
import java.lang.reflect.Field;
import java.util.List;

public class OrderServiceImplTest {

    private static Customer customer;
    private static OrderRepositoryImpl firstInstance;
    private static OrderService orderService;
    private static OrderRepository mockOrderRepository;

    private static void setMock(OrderRepository mock) {
        try {
            Field instance = OrderRepositoryImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            firstInstance = (OrderRepositoryImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        customer = new Customer(1L, "Иван", "Иванов");
        mockOrderRepository = Mockito.mock(OrderRepository.class);
        setMock(mockOrderRepository);
        orderService = OrderServiceImpl.getInstance();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = OrderRepositoryImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, firstInstance);
    }

    @BeforeEach
    void setUp() {
        Mockito.reset(mockOrderRepository);
    }

    @Test
    void findAll() {
        orderService.findAll();
        Mockito.verify(mockOrderRepository).findAll();
    }

    @Test
    void findByIdNotFound() {
        Mockito.doReturn(false).when(mockOrderRepository).existsById(Mockito.any());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> orderService.findById(1L), "Заказ не найден!"
        );
        Assertions.assertEquals("Выбранный заказ не найден!", exception.getMessage());
    }

    @Test
    void save() {
        Long expectedId = 1L;

        OrderIncDTO dto = new OrderIncDTO("Оформлен", customer);
        Order order = new Order(expectedId, "Оформлен", customer, List.of());

        Mockito.doReturn(order).when(mockOrderRepository).save(Mockito.any(Order.class));

        OrderOutDTO result = orderService.save(dto);

        Assertions.assertEquals(expectedId, result.getOrderId());
    }

    @Test
    void update() throws NotFoundException {

        OrderUpdDTO dto = new OrderUpdDTO(1L, "Выполнен",
                new CustomerUpdDTO(1L, "Ivan", "Ivanov"), List.of());

        Mockito.doReturn(true).when(mockOrderRepository).existsById(Mockito.any());

        orderService.update(dto);

        ArgumentCaptor<Order> argumentCaptor = ArgumentCaptor.forClass(Order.class);
        Mockito.verify(mockOrderRepository).update(argumentCaptor.capture());

        Order result = argumentCaptor.getValue();
        Assertions.assertEquals(1L, result.getId());
    }

    @Test
    void updateNotFound() {
        OrderUpdDTO dto = new OrderUpdDTO(1L, "Задерживается", null, null);

        Mockito.doReturn(false).when(mockOrderRepository).existsById(Mockito.any());

        NotFoundException exception = Assertions.assertThrows(NotFoundException.class,
                () -> orderService.update(dto), "Заказ не найден!"
        );
        Assertions.assertEquals("Выбранный заказ не найден!", exception.getMessage());
    }

    @Test
    void delete() throws NotFoundException {

        Mockito.doReturn(true).when(mockOrderRepository).existsById(Mockito.any());
        orderService.delete(5L);

        ArgumentCaptor<Long> argumentCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(mockOrderRepository).deleteById(argumentCaptor.capture());

        Long result = argumentCaptor.getValue();
        Assertions.assertEquals(5L, result);
    }
}

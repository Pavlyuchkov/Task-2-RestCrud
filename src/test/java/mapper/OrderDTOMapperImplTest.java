package mapper;

import dto.customer.CustomerUpdDTO;
import dto.order.OrderIncDTO;
import dto.order.OrderOutDTO;
import dto.order.OrderUpdDTO;
import dto.product.ProductUpdDTO;
import mapper.impl.OrderDTOMapperImpl;
import model.Customer;
import model.Order;
import model.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

public class OrderDTOMapperImplTest {

    private OrderDTOMapper orderDTOMapper;

    @BeforeEach
    void setUp() {
        orderDTOMapper = OrderDTOMapperImpl.getInstance();
    }


    @Test
    void mapIncDto() {
        OrderIncDTO orderIncDTO = new OrderIncDTO(
                "В процессе доставки",
                new Customer(15L, "Игорь", "Игорев")
        );
        Order result = orderDTOMapper.map(orderIncDTO);

        Assertions.assertNull(result.getId());
        Assertions.assertEquals(orderIncDTO.getOrderStatus(), result.getOrderStatus());
        Assertions.assertEquals(orderIncDTO.getCustomer().getId(), result.getCustomer().getId());
    }

    @Test
    void mapOutDto() {
        Order order = new Order(
                20L,
                "Выполнен",
                new Customer(5L, "Алексей", "Алексеев"),
                List.of(new Product(2L, "Молоко", 3L, List.of()))
        );
        OrderOutDTO result = orderDTOMapper.map(order);

        Assertions.assertEquals(order.getId(), result.getOrderId());
        Assertions.assertEquals(order.getOrderStatus(), result.getOrderStatus());
        Assertions.assertEquals(order.getCustomer().getId(), result.getCustomerOutDTO().getCustomerId());
        Assertions.assertEquals(order.getProductList().size(), result.getProductList().size());
    }

    @Test
    void mapUpdDto() {
        OrderUpdDTO orderUpdDTO = new OrderUpdDTO(
                7L,
                "Выполнен",
                new CustomerUpdDTO(1L, "Иван", "Иванов"),
                List.of(new ProductUpdDTO(4L, "Кофе", 15L))
        );
        Order result = orderDTOMapper.map(orderUpdDTO);

        Assertions.assertEquals(orderUpdDTO.getOrderId(), result.getId());
        Assertions.assertEquals(orderUpdDTO.getOrderStatus(), result.getOrderStatus());
        Assertions.assertEquals(orderUpdDTO.getCustomerUpdDTO().getCustomerId(), result.getCustomer().getId());
        Assertions.assertEquals(orderUpdDTO.getProductList().size(), result.getProductList().size());
    }

    @Test
    void mapOutDtoList() {
        List<Order> orderList = List.of(
                new Order(
                        14L,
                        "Комплектуется",
                        new Customer(4L, "Кирилл", "Кириллов"),
                        List.of(new Product(3L, "Чай", 10L, List.of()))
                ),
                new Order(
                        15L,
                        "В процессе доставки",
                        new Customer(3L, "Филипп", "Филиппов"),
                        List.of(new Product(6L, "Говядина", 20L, List.of()))
                )
        );
        int result = orderDTOMapper.map(orderList).size();
        Assertions.assertEquals(orderList.size(), result);
    }
}

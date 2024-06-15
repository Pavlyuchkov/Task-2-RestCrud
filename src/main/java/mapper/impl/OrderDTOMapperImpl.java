package mapper.impl;

import dto.order.OrderIncDTO;
import dto.order.OrderOutDTO;
import dto.order.OrderUpdDTO;
import mapper.CustomerDTOMapper;
import mapper.OrderDTOMapper;
import mapper.ProductDTOMapper;
import model.Order;

import java.util.List;

public class OrderDTOMapperImpl implements OrderDTOMapper {

    private static final CustomerDTOMapper CUSTOMER_DTO_MAPPER = CustomerDTOMapperImpl.getInstance();
    private static final ProductDTOMapper PRODUCT_DTO_MAPPER = ProductDTOMapperImpl.getInstance();
    private static OrderDTOMapper instance;

    private OrderDTOMapperImpl() {
    }

    public static OrderDTOMapper getInstance() {
        if (instance == null) {
            instance = new OrderDTOMapperImpl();
        }
        return instance;
    }

    @Override
    public Order map(OrderIncDTO orderDto) {
        return new Order(
                null,
                orderDto.getOrderStatus(),
                orderDto.getCustomer(),
                null);
    }

    @Override
    public Order map(OrderUpdDTO orderDto) {
        return new Order(
                orderDto.getOrderId(),
                orderDto.getOrderStatus(),
                CUSTOMER_DTO_MAPPER.map(orderDto.getCustomerUpdDTO()),
                PRODUCT_DTO_MAPPER.mapUpdateList(orderDto.getProductList())
        );
    }

    @Override
    public OrderOutDTO map(Order order) {
        return new OrderOutDTO(
                order.getId(),
                order.getOrderStatus(),
                CUSTOMER_DTO_MAPPER.map(order.getCustomer()),
                PRODUCT_DTO_MAPPER.map(order.getProductList())
        );
    }

    @Override
    public List<OrderOutDTO> map(List<Order> user) {
        return user.stream().map(this::map).toList();
    }


}

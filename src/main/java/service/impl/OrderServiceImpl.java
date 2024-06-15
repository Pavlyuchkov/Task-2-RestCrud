package service.impl;

import dto.order.OrderIncDTO;
import dto.order.OrderOutDTO;
import dto.order.OrderUpdDTO;
import exception.NotFoundException;
import mapper.OrderDTOMapper;
import mapper.impl.OrderDTOMapperImpl;
import model.Order;
import repository.OrderRepository;
import repository.impl.OrderRepositoryImpl;
import service.OrderService;

import java.util.List;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository = OrderRepositoryImpl.getInstance();
    private static final OrderDTOMapper orderDtoMapper = OrderDTOMapperImpl.getInstance();
    private static OrderService instance;


    private OrderServiceImpl() {
    }

    public static OrderService getInstance() {
        if (instance == null) {
            instance = new OrderServiceImpl();
        }
        return instance;
    }

    private void checkExistOrder(Long orderId) throws NotFoundException {
        if (!orderRepository.existsById(orderId)) {
            throw new NotFoundException("Выбранный заказ не найден!");
        }
    }

    @Override
    public OrderOutDTO save(OrderIncDTO orderDto) {
        Order order = orderRepository.save(orderDtoMapper.map(orderDto));
        return orderDtoMapper.map(orderRepository.findById(order.getId()).orElse(order));
    }

    @Override
    public void update(OrderUpdDTO orderDto) throws NotFoundException {
        if (orderDto == null || orderDto.getOrderId() == null) {
            throw new IllegalArgumentException();
        }
        checkExistOrder(orderDto.getOrderId());
        orderRepository.update(orderDtoMapper.map(orderDto));
    }

    @Override
    public OrderOutDTO findById(Long orderId) throws NotFoundException {
        checkExistOrder(orderId);
        Order order = orderRepository.findById(orderId).orElseThrow();
        return orderDtoMapper.map(order);
    }

    @Override
    public List<OrderOutDTO> findAll() {
        List<Order> all = orderRepository.findAll();
        return orderDtoMapper.map(all);
    }

    @Override
    public void delete(Long orderId) throws NotFoundException {
        checkExistOrder(orderId);
        orderRepository.deleteById(orderId);
    }

}

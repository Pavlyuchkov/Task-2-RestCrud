package service;

import dto.order.OrderIncDTO;
import dto.order.OrderOutDTO;
import dto.order.OrderUpdDTO;
import exception.NotFoundException;

import java.util.List;

public interface OrderService {

    OrderOutDTO save(OrderIncDTO orderDto);

    void update(OrderUpdDTO orderDto) throws NotFoundException;

    OrderOutDTO findById(Long orderId) throws NotFoundException;

    List<OrderOutDTO> findAll();

    void delete(Long orderId) throws NotFoundException;

}

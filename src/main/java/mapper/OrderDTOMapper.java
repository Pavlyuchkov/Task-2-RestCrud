package mapper;

import dto.order.OrderIncDTO;
import dto.order.OrderOutDTO;
import dto.order.OrderUpdDTO;
import model.Order;

import java.util.List;

public interface OrderDTOMapper {

    Order map(OrderIncDTO orderIncDTO);

    Order map(OrderUpdDTO orderUpdDTO);

    OrderOutDTO map(Order order);

    List<OrderOutDTO> map(List<Order> orders);


}

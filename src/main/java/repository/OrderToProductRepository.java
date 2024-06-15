package repository;

import model.Order;
import model.OrderToProduct;
import model.Product;

import java.util.List;
import java.util.Optional;

public interface OrderToProductRepository extends Repository<OrderToProduct, Long> {

    boolean deleteByOrderId(Long orderId);

    boolean deleteByProductId(Long productId);

    List<OrderToProduct> findAllByOrderId(Long orderId);

    List<Product> findProductsByOrderId(Long orderId);

    List<OrderToProduct> findAllByProductId(Long productId);

    List<Order> findOrdersByProductId(Long productId);

    Optional<OrderToProduct> findByOrderIdAndProductId(Long orderId, Long productId);
}

package repository.impl;

import config.ConnectionManager;
import config.ConnectionManagerImpl;
import exception.RepositoryException;
import model.Customer;
import model.Order;
import model.OrderToProduct;
import model.Product;
import repository.CustomerRepository;
import repository.OrderRepository;
import repository.OrderToProductRepository;
import repository.ProductRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderRepositoryImpl implements OrderRepository {

    private static final String SAVE_SQL = """
            INSERT INTO orders (order_status, customer_id)
            VALUES (?, ?) ;
            """;
    private static final String UPDATE_SQL = """
            UPDATE orders
            SET order_status = ?,
                customer_id =?
            WHERE order_id = ? ;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM orders
            WHERE order_id = ? ;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT order_id, order_status, customer_id FROM orders
            WHERE order_id = ?
            LIMIT 1;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT order_id, order_status, customer_id FROM orders;
            """;
    private static final String EXIST_BY_ID_SQL = """
                SELECT exists (
                SELECT 1
                    FROM orders
                        WHERE order_id = ?
                        LIMIT 1);
            """;
    private static OrderRepository instance;
    private final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();
    private final OrderToProductRepository orderToProductRepository = OrderToProductRepositoryImpl.getInstance();
    private final CustomerRepository customerRepository = CustomerRepositoryImpl.getInstance();
    private final ProductRepository productRepository = ProductRepositoryImpl.getInstance();

    private OrderRepositoryImpl() {
    }

    public static OrderRepository getInstance() {
        if (instance == null) {
            instance = new OrderRepositoryImpl();
        }
        return instance;
    }

    @Override
    public Order save(Order order) {
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, order.getOrderStatus());
            if (order.getCustomer() == null) {
                preparedStatement.setNull(2, Types.NULL);
            } else {
                preparedStatement.setLong(2, order.getCustomer().getId());
            }
            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                order = new Order(
                        resultSet.getLong("order_id"),
                        order.getOrderStatus(),
                        order.getCustomer(),
                        null);
            }
            saveProductList(order);
            order.getProductList();
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return order;
    }

    private void saveProductList(Order order) {
        if (order.getProductList() != null && !order.getProductList().isEmpty()) {
            List<Long> productIdList = new ArrayList<>(
                    order.getProductList()
                            .stream()
                            .map(Product::getId)
                            .toList()
            );
            List<OrderToProduct> existsProductList = orderToProductRepository.findAllByOrderId(order.getId());
            for (OrderToProduct orderToProduct : existsProductList) {
                if (!productIdList.contains(orderToProduct.getProductId())) {
                    orderToProductRepository.deleteById(orderToProduct.getOrderToProductId());
                }
                productIdList.remove(orderToProduct.getProductId());
            }
            for (Long productId : productIdList) {
                if (productRepository.existsById(productId)) {
                    OrderToProduct orderToProduct = new OrderToProduct(
                            null,
                            order.getId(),
                            productId
                    );
                    orderToProductRepository.save(orderToProduct);
                }
            }
        } else {
            orderToProductRepository.deleteByOrderId(order.getId());
        }
    }

    @Override
    public void update(Order order) {
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);) {

            preparedStatement.setString(1, order.getOrderStatus());
            if (order.getCustomer() == null) {
                preparedStatement.setNull(2, Types.NULL);
            } else {
                preparedStatement.setLong(2, order.getCustomer().getId());
            }
            preparedStatement.setLong(3, order.getId());

            preparedStatement.executeUpdate();
            saveProductList(order);
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        boolean deleteResult;
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL);) {

            orderToProductRepository.deleteByOrderId(id);

            preparedStatement.setLong(1, id);
            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return deleteResult;
    }

    @Override
    public Optional<Order> findById(Long id) {
        Order order = null;
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                order = createOrder(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(order);
    }

    @Override
    public List<Order> findAll() {
        List<Order> orderList = new ArrayList<>();
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                orderList.add(createOrder(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return orderList;
    }

    @Override
    public boolean existsById(Long id) {
        boolean isExists = false;
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(EXIST_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                isExists = resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return isExists;
    }

    private Order createOrder(ResultSet resultSet) throws SQLException {
        Long orderId = resultSet.getLong("order_id");
        Customer customer = customerRepository.findById(resultSet.getLong("customer_id")).orElse(null);

        return new Order(
                orderId,
                resultSet.getString("order_status"),
                customer,
                null
        );
    }
}

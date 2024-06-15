package repository.impl;

import config.ConnectionManager;
import config.ConnectionManagerImpl;
import exception.RepositoryException;
import model.Order;
import model.OrderToProduct;
import model.Product;
import repository.OrderRepository;
import repository.OrderToProductRepository;
import repository.ProductRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderToProductRepositoryImpl implements OrderToProductRepository {

    private static final String SAVE_SQL = """
            INSERT INTO orders_products (order_id, product_id)
            VALUES (?, ?);
            """;
    private static final String UPDATE_SQL = """
            UPDATE orders_products
            SET order_id = ?,
                product_id = ?
            WHERE order_product_id = ?;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM orders_products
            WHERE order_product_id = ? ;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT order_product_id, order_id, product_id FROM orders_products
            WHERE order_product_id = ?
            LIMIT 1;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT order_product_id, order_id, product_id FROM orders_products;
            """;
    private static final String FIND_ALL_BY_ORDER_ID_SQL = """
            SELECT order_product_id, order_id, product_id FROM orders_products
            WHERE order_id = ?;
            """;
    private static final String FIND_ALL_BY_PRODUCT_ID_SQL = """
            SELECT order_product_id, order_id, product_id FROM orders_products
            WHERE product_id = ?;
            """;
    private static final String FIND_BY_ORDER_ID_AND_PRODUCT_ID_SQL = """
            SELECT order_product_id, order_id, product_id FROM orders_products
            WHERE order_id = ? AND product_id = ?
            LIMIT 1;
            """;
    private static final String DELETE_BY_ORDER_ID_SQL = """
            DELETE FROM orders_products
            WHERE order_id = ?;
            """;
    private static final String DELETE_BY_PRODUCT_ID_SQL = """
            DELETE FROM orders_products
            WHERE product_id = ?;
            """;
    private static final String EXIST_BY_ID_SQL = """
                SELECT exists (
                SELECT 1
                    FROM orders_products
                        WHERE order_product_id = ?
                        LIMIT 1);
            """;


    private final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();
    private static final ProductRepository productRepository = ProductRepositoryImpl.getInstance();
    private static final OrderRepository orderRepository = OrderRepositoryImpl.getInstance();
    private static OrderToProductRepository instance;

    private OrderToProductRepositoryImpl() {
    }

    public static OrderToProductRepository getInstance() {
        if (instance == null) {
            instance = new OrderToProductRepositoryImpl();
        }
        return instance;
    }

    private static OrderToProduct createOrderToProduct(ResultSet resultSet) throws SQLException {
        OrderToProduct orderToProduct;
        orderToProduct = new OrderToProduct(
                resultSet.getLong("order_product_id"),
                resultSet.getLong("order_id"),
                resultSet.getLong("product_id")
        );
        return orderToProduct;
    }

    @Override
    public OrderToProduct save(OrderToProduct orderToProduct) {
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setLong(1, orderToProduct.getOrderId());
            preparedStatement.setLong(2, orderToProduct.getProductId());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                orderToProduct = new OrderToProduct(
                        resultSet.getLong("order_product_id"),
                        orderToProduct.getOrderId(),
                        orderToProduct.getProductId()
                );
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return orderToProduct;
    }

    @Override
    public void update(OrderToProduct orderToProduct) {
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);) {

            preparedStatement.setLong(1, orderToProduct.getOrderId());
            preparedStatement.setLong(2, orderToProduct.getProductId());
            preparedStatement.setLong(3, orderToProduct.getOrderToProductId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        boolean deleteResult;
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL);) {

            preparedStatement.setLong(1, id);

            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return deleteResult;
    }

    @Override
    public boolean deleteByOrderId(Long orderId) {
        boolean deleteResult;
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_ORDER_ID_SQL);) {

            preparedStatement.setLong(1, orderId);

            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return deleteResult;
    }

    @Override
    public boolean deleteByProductId(Long productId) {
        boolean deleteResult;
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_PRODUCT_ID_SQL);) {

            preparedStatement.setLong(1, productId);

            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return deleteResult;
    }

    @Override
    public Optional<OrderToProduct> findById(Long id) {
        OrderToProduct orderToProduct = null;
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                orderToProduct = createOrderToProduct(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(orderToProduct);
    }

    @Override
    public List<OrderToProduct> findAll() {
        List<OrderToProduct> orderToProductList = new ArrayList<>();
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                orderToProductList.add(createOrderToProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return orderToProductList;
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

    public List<OrderToProduct> findAllByOrderId(Long orderId) {
        List<OrderToProduct> orderToProductList = new ArrayList<>();
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_ORDER_ID_SQL)) {

            preparedStatement.setLong(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                orderToProductList.add(createOrderToProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return orderToProductList;
    }

    @Override
    public List<Product> findProductsByOrderId(Long orderId) {
        List<Product> productList = new ArrayList<>();
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_ORDER_ID_SQL)) {

            preparedStatement.setLong(1, orderId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Long productId = resultSet.getLong("product_id");
                Optional<Product> optionalProduct = productRepository.findById(productId);
                optionalProduct.ifPresent(productList::add);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return productList;
    }

    public List<OrderToProduct> findAllByProductId(Long productId) {
        List<OrderToProduct> orderToProductList = new ArrayList<>();
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_PRODUCT_ID_SQL)) {

            preparedStatement.setLong(1, productId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                orderToProductList.add(createOrderToProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return orderToProductList;
    }

    public List<Order> findOrdersByProductId(Long productId) {
        List<Order> orderList = new ArrayList<>();
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_PRODUCT_ID_SQL)) {

            preparedStatement.setLong(1, productId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                long orderId = resultSet.getLong("order_id");
                Optional<Order> optionalOrder = orderRepository.findById(orderId);
                optionalOrder.ifPresent(orderList::add);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return orderList;
    }

    @Override
    public Optional<OrderToProduct> findByOrderIdAndProductId(Long orderId, Long productId) {
        Optional<OrderToProduct> orderToProduct = Optional.empty();
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ORDER_ID_AND_PRODUCT_ID_SQL)) {

            preparedStatement.setLong(1, orderId);
            preparedStatement.setLong(2, productId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                orderToProduct = Optional.of(createOrderToProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return orderToProduct;
    }


}

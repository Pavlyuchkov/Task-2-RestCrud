package repository.impl;

import config.ConnectionManager;
import config.ConnectionManagerImpl;
import exception.RepositoryException;
import model.Product;
import repository.OrderToProductRepository;
import repository.ProductRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductRepositoryImpl implements ProductRepository {

    private static final String SAVE_SQL = """
            INSERT INTO products (product_name, price)
            VALUES (?, ?);
            """;

    private static final String UPDATE_SQL = """
            UPDATE products
            SET product_name = ?,
                price = ?
            WHERE product_id = ?;
            """;

    private static final String DELETE_SQL = """
            DELETE FROM products
            WHERE product_id = ?;
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT product_id, product_name, price FROM products
            WHERE product_id = ?
            LIMIT 1;
            """;

    private static final String FIND_ALL_SQL = """
            SELECT product_id, product_name, price FROM products;
            """;

    private static final String EXIST_BY_ID_SQL = """
                SELECT exists (
                SELECT 1
                    FROM products
                        WHERE product_id = ?
                        LIMIT 1);
            """;
    private static ProductRepository instance;
    private final OrderToProductRepository orderToProductRepository = OrderToProductRepositoryImpl.getInstance();
    private final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();

    private ProductRepositoryImpl() {
    }

    public static ProductRepository getInstance() {
        if (instance == null) {
            instance = new ProductRepositoryImpl();
        }
        return instance;
    }

    private static Product createProduct (ResultSet resultSet) throws SQLException {
        Product product;
        product = new Product(
                resultSet.getLong("product_id"),
                resultSet.getString("product_name"),
                resultSet.getLong("price"),
                null);
        return product;
    }

    @Override
    public Product save(Product product) {
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, product.getProductName());
            preparedStatement.setLong(2, product.getPrice());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                product = new Product(
                        resultSet.getLong("product_id"),
                        product.getProductName(),
                        product.getPrice(),
                        null
                );
                product.getOrderList();
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return product;
    }

    @Override
    public void update(Product product) {
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);) {

            preparedStatement.setString(1, product.getProductName());
            preparedStatement.setLong(2, product.getPrice());
            preparedStatement.setLong(3, product.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public boolean deleteById(Long id) {
        boolean deleteResult = true;
        try (java.sql.Connection connection = connectionManager.getConnection();

             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL);) {
            orderToProductRepository.deleteByProductId(id);
            preparedStatement.setLong(1, id);

            deleteResult = preparedStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return deleteResult;
    }

    @Override
    public Optional<Product> findById(Long id) {
        Product product = null;
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                product = createProduct(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(product);
    }

    @Override
    public List<Product> findAll() {
        List<Product> productList = new ArrayList<>();
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                productList.add(createProduct(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return productList;
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
}
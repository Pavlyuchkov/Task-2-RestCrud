package repository.impl;

import config.ConnectionManager;
import config.ConnectionManagerImpl;
import exception.RepositoryException;
import model.Customer;
import repository.CustomerRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerRepositoryImpl implements CustomerRepository {

    private static final String SAVE_SQL = """
            INSERT INTO customers (customer_name, customer_surname)
            VALUES (?, ?) ;
            """;
    private static final String UPDATE_SQL = """
            UPDATE customers
            SET customer_name = ?,
            customer_surname = ?
            WHERE customer_id = ?
            """;
    private static final String DELETE_SQL = """
            DELETE FROM customers
            WHERE customer_id = ? ;
            """;
    private static final String FIND_BY_ID_SQL = """
            SELECT customer_id, customer_name, customer_surname FROM customers
            WHERE customer_id = ?
            LIMIT 1;
            """;
    private static final String FIND_ALL_SQL = """
            SELECT customer_id, customer_name, customer_surname FROM customers ;
            """;
    private static final String EXIST_BY_ID_SQL = """
                SELECT exists (
                SELECT 1
                    FROM customers
                        WHERE customer_id = ?
                        LIMIT 1);
            """;
    private static CustomerRepository instance;
    private final ConnectionManager connectionManager = ConnectionManagerImpl.getInstance();

    private CustomerRepositoryImpl() {
    }

    public static CustomerRepository getInstance() {
        if (instance == null) {
            instance = new CustomerRepositoryImpl();
        }
        return instance;
    }

    private static Customer createCustomer(ResultSet resultSet) throws SQLException {
        Customer customer;
        customer = new Customer(resultSet.getLong("customer_id"),
                resultSet.getString("customer_name"),
                resultSet.getString("customer_surname"));
        return customer;
    }

    @Override
    public Customer save(Customer customer) {
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, customer.getName());
            preparedStatement.setString(2, customer.getSurname());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                customer = new Customer(
                        resultSet.getLong("customer_id"),
                        customer.getName(),
                        customer.getSurname());
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }

        return customer;
    }

    @Override
    public void update(Customer customer) {
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL);) {

            preparedStatement.setString(1, customer.getName());
            preparedStatement.setString(2, customer.getSurname());
            preparedStatement.setLong(3, customer.getId());

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
    public Optional<Customer> findById(Long id) {
        Customer customer = null;
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setLong(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                customer = createCustomer(resultSet);
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return Optional.ofNullable(customer);
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> customerList = new ArrayList<>();
        try (java.sql.Connection connection = connectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                customerList.add(createCustomer(resultSet));
            }
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
        return customerList;
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

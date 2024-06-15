package mapper.impl;

import dto.customer.CustomerIncDTO;
import dto.customer.CustomerOutDTO;
import dto.customer.CustomerUpdDTO;
import mapper.CustomerDTOMapper;
import model.Customer;

import java.util.List;

public class CustomerDTOMapperImpl implements CustomerDTOMapper {

    private static CustomerDTOMapper instance;

    private CustomerDTOMapperImpl() {
    }

    public static synchronized CustomerDTOMapper getInstance() {
        if (instance == null) {
            instance = new CustomerDTOMapperImpl();
        }
        return instance;
    }

    @Override
    public Customer map(CustomerIncDTO customerIncDTO) {
        return new Customer(
                null,
                customerIncDTO.getCustomerName(),
                customerIncDTO.getCustomerSurname()
        );
    }

    @Override
    public Customer map(CustomerUpdDTO customerUpdDTO) {
        return new Customer(
                customerUpdDTO.getCustomerId(),
                customerUpdDTO.getCustomerName(),
                customerUpdDTO.getCustomerSurname()
        );
    }

    @Override
    public CustomerOutDTO map(Customer customer) {
        return new CustomerOutDTO(
                customer.getId(),
                customer.getName(),
                customer.getSurname()
        );
    }

    @Override
    public List<CustomerOutDTO> map(List<Customer> customerList) {
        return customerList.stream().map(this::map).toList();
    }
}

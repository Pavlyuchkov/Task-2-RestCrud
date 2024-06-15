package service;

import dto.customer.CustomerIncDTO;
import dto.customer.CustomerOutDTO;
import dto.customer.CustomerUpdDTO;
import exception.NotFoundException;

import java.util.List;

public interface CustomerService {

    CustomerOutDTO save(CustomerIncDTO customer);

    void update(CustomerUpdDTO customer) throws NotFoundException;

    CustomerOutDTO findById(Long customerId) throws NotFoundException;

    List<CustomerOutDTO> findAll();

    boolean delete(Long customerId) throws NotFoundException;

}

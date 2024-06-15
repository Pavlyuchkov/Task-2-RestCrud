package service.impl;

import dto.customer.CustomerIncDTO;
import dto.customer.CustomerOutDTO;
import dto.customer.CustomerUpdDTO;
import exception.NotFoundException;
import mapper.CustomerDTOMapper;
import mapper.impl.CustomerDTOMapperImpl;
import model.Customer;
import repository.CustomerRepository;
import repository.impl.CustomerRepositoryImpl;
import service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository = CustomerRepositoryImpl.getInstance();
    private static CustomerService instance;
    private final CustomerDTOMapper customerDTOMapper = CustomerDTOMapperImpl.getInstance();

    private CustomerServiceImpl() {
    }

    public static CustomerService getInstance() {
        if (instance == null) {
            instance = new CustomerServiceImpl();
        }
        return instance;
    }

    @Override
    public CustomerOutDTO save(CustomerIncDTO customerIncDTO) {
        Customer customer = customerDTOMapper.map(customerIncDTO);
        customer = customerRepository.save(customer);
        return customerDTOMapper.map(customer);
    }

    @Override
    public void update(CustomerUpdDTO customerUpdDTO) throws NotFoundException {
        checkCustomerExist(customerUpdDTO.getCustomerId());
        Customer customer = customerDTOMapper.map(customerUpdDTO);
        customerRepository.update(customer);
    }

    @Override
    public CustomerOutDTO findById(Long customerId) throws NotFoundException {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() ->
                new NotFoundException("Выбранный Покупатель не найден!"));
        return customerDTOMapper.map(customer);
    }

    @Override
    public List<CustomerOutDTO> findAll() {
        List<Customer> customerList = customerRepository.findAll();
        return customerDTOMapper.map(customerList);
    }

    @Override
    public boolean delete(Long customerId) throws NotFoundException {
        checkCustomerExist(customerId);
        return customerRepository.deleteById(customerId);
    }

    private void checkCustomerExist(Long customerId) throws NotFoundException {
        if (!customerRepository.existsById(customerId)) {
            throw new NotFoundException("Выбранный Покупатель не найден!");
        }
    }

}

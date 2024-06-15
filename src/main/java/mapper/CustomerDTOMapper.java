package mapper;

import dto.customer.CustomerIncDTO;
import dto.customer.CustomerOutDTO;
import dto.customer.CustomerUpdDTO;
import model.Customer;

import java.util.List;

public interface CustomerDTOMapper {

    Customer map(CustomerIncDTO customerIncDTO);

    Customer map(CustomerUpdDTO customerNewDTO);

    CustomerOutDTO map(Customer customer);

    List<CustomerOutDTO> map(List<Customer> customerList);
}

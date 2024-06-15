package dto.customer;

public class CustomerUpdDTO {

    private Long customerId;
    private String customerName;
    private String customerSurname;

    public CustomerUpdDTO() {
    }

    public CustomerUpdDTO(Long customerId, String customerName, String customerSurname) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerSurname = customerSurname;
    }

    public Long getCustomerId() {
        return customerId;
    }
    public String getCustomerName() {
        return customerName;
    }
    public String getCustomerSurname() {
        return customerSurname;
    }

}

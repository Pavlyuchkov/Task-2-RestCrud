package dto.customer;

public class CustomerIncDTO {

    private String customerName;
    private String customerSurname;

    public CustomerIncDTO() {
    }

    public CustomerIncDTO(String customerName, String customerSurname) {
        this.customerName = customerName;
        this.customerSurname = customerSurname;
    }

    public String getCustomerName() {
        return customerName;
    }
    public String getCustomerSurname() {
        return customerSurname;
    }
}

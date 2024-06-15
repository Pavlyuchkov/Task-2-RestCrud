package servlet;

import dto.customer.CustomerIncDTO;
import dto.customer.CustomerUpdDTO;
import exception.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import service.CustomerService;
import service.impl.CustomerServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
public class CustomerServletTest {

    private static CustomerService mockCustomerService;
    private static CustomerServiceImpl firstInstance;

    @InjectMocks
    private static CustomerServlet customerServlet;

    @Mock
    private BufferedReader mockBufferedReader;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    private static void setMock(CustomerService mock) {
        try {
            Field instance = CustomerServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            firstInstance = (CustomerServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockCustomerService = Mockito.mock(CustomerService.class);
        setMock(mockCustomerService);
        customerServlet = new CustomerServlet();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = CustomerServiceImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, firstInstance);
    }

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(mockCustomerService);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("customers/all").when(mockRequest).getPathInfo();

        customerServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockCustomerService).findAll();
    }

    @Test
    void doGetById() throws IOException, NotFoundException {
        Mockito.doReturn("customers/2").when(mockRequest).getPathInfo();

        customerServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockCustomerService).findById(Mockito.anyLong());
    }

    @Test
    void doGetBadRequest() throws IOException {
        Mockito.doReturn("customers/5th").when(mockRequest).getPathInfo();

        customerServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doGetNotFoundException() throws IOException, NotFoundException {
        Mockito.doReturn("customers/400").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("Выбранный Покупатель не найден!")).when(mockCustomerService).findById(400L);

        customerServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doPost() throws IOException {
        String expectedName = "Григорий";
        String expectedSurname = "Григорьев";
        String jsonInput = "{\"customerName\":\"" + expectedName + "\", \"customerSurname\":\"" + expectedSurname + "\"}";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.when(mockBufferedReader.readLine())
                .thenReturn(jsonInput)
                .thenReturn(null);

        customerServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<CustomerIncDTO> argumentCaptor = ArgumentCaptor.forClass(CustomerIncDTO.class);
        Mockito.verify(mockCustomerService).save(argumentCaptor.capture());

        CustomerIncDTO result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getCustomerName());
        Assertions.assertEquals(expectedSurname, result.getCustomerSurname());
    }

    @Test
    void doPostBadRequest() throws IOException {
        String invalidJson = "{\"create\":8}";
        Mockito.when(mockRequest.getReader()).thenReturn(mockBufferedReader);
        Mockito.when(mockBufferedReader.readLine())
                .thenReturn(invalidJson)
                .thenReturn(null);

        customerServlet.doPost(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPut() throws IOException, NotFoundException {
        Long expectedId = 4L;
        String expectedName = "Никита";
        String expectedSurname = "Никитин";
        String jsonInput = "{\"customerId\":" + expectedId + ", \"customerName\":\"" +
                expectedName + "\", \"customerSurname\":\"" + expectedSurname + "\"}";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.when(mockBufferedReader.readLine())
                .thenReturn(jsonInput)
                .thenReturn(null);

        customerServlet.doPut(mockRequest, mockResponse);

        ArgumentCaptor<CustomerUpdDTO> argumentCaptor = ArgumentCaptor.forClass(CustomerUpdDTO.class);
        Mockito.verify(mockCustomerService).update(argumentCaptor.capture());

        CustomerUpdDTO result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedId, result.getCustomerId());
        Assertions.assertEquals(expectedName, result.getCustomerName());
        Assertions.assertEquals(expectedSurname, result.getCustomerSurname());
    }

    @Test
    void doPutBadRequest() throws IOException {
        String invalidJsonInput = "{update:3}";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.when(mockBufferedReader.readLine())
                .thenReturn(invalidJsonInput)
                .thenReturn(null);

        customerServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPutNotFound() throws IOException, NotFoundException {
        String jsonInput = "{\"customerId\": 4, \"customerName\": \"Владислав\", \"customerSurname\": \"Владиславов\"}";
        String errorMessage = "Выбранный Покупатель не найден!";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.when(mockBufferedReader.readLine())
                .thenReturn(jsonInput)
                .thenReturn(null);
        Mockito.doThrow(new NotFoundException(errorMessage)).when(mockCustomerService)
                .update(Mockito.any(CustomerUpdDTO.class));

        customerServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
        Mockito.verify(mockCustomerService).update(Mockito.any(CustomerUpdDTO.class));
    }

    @Test
    void doDelete() throws IOException, NotFoundException {
        Mockito.doReturn("customers/2").when(mockRequest).getPathInfo();
        Mockito.doReturn(true).when(mockCustomerService).delete(Mockito.anyLong());

        customerServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockCustomerService).delete(Mockito.anyLong());
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDeleteNotFound() throws IOException, NotFoundException {
        Mockito.doReturn("customers/400").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("Выбранный Покупатель не найден!"))
                .when(mockCustomerService).delete(400L);

        customerServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
        Mockito.verify(mockCustomerService).delete(Mockito.anyLong());
    }

    @Test
    void doDeleteBadRequest() throws IOException {
        Mockito.doReturn("customers/2nd").when(mockRequest).getPathInfo();

        customerServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}

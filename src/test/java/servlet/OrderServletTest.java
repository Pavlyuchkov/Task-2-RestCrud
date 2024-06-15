package servlet;

import dto.order.OrderIncDTO;
import dto.order.OrderUpdDTO;
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
import service.OrderService;
import service.impl.OrderServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
public class OrderServletTest {

    private static OrderService mockOrderService;
    private static OrderServiceImpl firstInstance;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private BufferedReader mockBufferedReader;

    @InjectMocks
    private static OrderServlet orderServlet;


    private static void setMock(OrderService mock) {
        try {
            Field instance = OrderServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            firstInstance = (OrderServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockOrderService = Mockito.mock(OrderService.class);
        setMock(mockOrderService);
        orderServlet = new OrderServlet();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = OrderServiceImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, firstInstance);
    }

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(mockOrderService);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("orders/all").when(mockRequest).getPathInfo();

        orderServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockOrderService).findAll();
    }

    @Test
    void doGetById() throws IOException, NotFoundException {
        Mockito.doReturn("orders/5").when(mockRequest).getPathInfo();

        orderServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockOrderService).findById(Mockito.anyLong());
    }

    @Test
    void doGetNotFoundException() throws IOException, NotFoundException {
        Mockito.doReturn("orders/50").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("Заказ не найден!")).when(mockOrderService).findById(50L);

        orderServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetBadRequest() throws IOException {
        Mockito.doReturn("orders/1st").when(mockRequest).getPathInfo();

        orderServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPost() throws IOException {
        String orderStatus = "Комплектуется";
        long expectedCustomerId = 3L;
        String customerName = "Филипп";
        String customerSurname = "Филиппов";

        String jsonInput = "{\"orderStatus\":\"" + orderStatus + "\",\"customer\"" +
                ":{\"id\":" + expectedCustomerId + ",\"name\":\"" +
                customerName + "\",\"surname\":\"" + customerSurname + "\"}}";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.when(mockBufferedReader.readLine())
                .thenReturn(jsonInput)
                .thenReturn(null);

        orderServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<OrderIncDTO> argumentCaptor = ArgumentCaptor.forClass(OrderIncDTO.class);
        Mockito.verify(mockOrderService).save(argumentCaptor.capture());

        OrderIncDTO result = argumentCaptor.getValue();
        Assertions.assertEquals(orderStatus, result.getOrderStatus());
    }

    @Test
    void doPut() throws IOException, NotFoundException {
        long orderId = 7L;
        String orderStatus = "В процессе доставки";
        long customerId = 5L;

        String jsonInput = "{\"orderId\":" + orderId + ",\"orderStatus\":\"" + orderStatus + "\",\"customerUpdDTO\"" +
                ":{\"customerId\":" + customerId + "},\"productList\":[]}";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.when(mockBufferedReader.readLine())
                .thenReturn(jsonInput)
                .thenReturn(null);

        orderServlet.doPut(mockRequest, mockResponse);

        ArgumentCaptor<OrderUpdDTO> argumentCaptor = ArgumentCaptor.forClass(OrderUpdDTO.class);
        Mockito.verify(mockOrderService).update(argumentCaptor.capture());

        OrderUpdDTO result = argumentCaptor.getValue();
        Assertions.assertEquals(orderId, result.getOrderId());
        Assertions.assertEquals(orderStatus, result.getOrderStatus());
        Assertions.assertEquals(customerId, result.getCustomerUpdDTO().getCustomerId());
        Assertions.assertTrue(result.getProductList().isEmpty());
    }

    @Test
    void doPutBadRequest() throws IOException {
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.when(mockBufferedReader.readLine())
                .thenReturn("{update:5}")
                .thenReturn(null);

        orderServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDelete() throws IOException, NotFoundException {
        Mockito.doReturn("orders/6").when(mockRequest).getPathInfo();

        orderServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockOrderService).delete(Mockito.anyLong());
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDeleteBadRequest() throws IOException {
        Mockito.doReturn("orders/3rd").when(mockRequest).getPathInfo();

        orderServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDeleteNotFound() throws IOException, NotFoundException {
        Mockito.doReturn("orders/50").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("Заказ для удаления не найден!"))
                .when(mockOrderService).delete(50L);

        orderServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
        Mockito.verify(mockOrderService).delete(50L);
    }

}

package servlet;

import dto.product.ProductIncDTO;
import dto.product.ProductUpdDTO;
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
import service.ProductService;
import service.impl.ProductServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Field;

@ExtendWith(MockitoExtension.class)
public class ProductServletTest {

    private static ProductService mockProductService;
    private static ProductServiceImpl firstInstance;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private BufferedReader mockBufferedReader;

    @InjectMocks
    private static ProductServlet productServlet;


    private static void setMock(ProductService mock) {
        try {
            Field instance = ProductServiceImpl.class.getDeclaredField("instance");
            instance.setAccessible(true);
            firstInstance = (ProductServiceImpl) instance.get(instance);
            instance.set(instance, mock);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeAll
    static void beforeAll() {
        mockProductService = Mockito.mock(ProductService.class);
        setMock(mockProductService);
        productServlet = new ProductServlet();
    }

    @AfterAll
    static void afterAll() throws Exception {
        Field instance = ProductServiceImpl.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(instance, firstInstance);
    }

    @BeforeEach
    void setUp() throws IOException {
        Mockito.doReturn(new PrintWriter(Writer.nullWriter())).when(mockResponse).getWriter();
    }

    @AfterEach
    void tearDown() {
        Mockito.reset(mockProductService);
    }

    @Test
    void doGetAll() throws IOException {
        Mockito.doReturn("products/all").when(mockRequest).getPathInfo();

        productServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockProductService).findAll();
    }

    @Test
    void doGetById() throws IOException, NotFoundException {
        Mockito.doReturn("products/5").when(mockRequest).getPathInfo();

        productServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockProductService).findById(Mockito.anyLong());
    }

    @Test
    void doGetNotFoundException() throws IOException, NotFoundException {
        Mockito.doReturn("products/50").when(mockRequest).getPathInfo();
        Mockito.doThrow(new NotFoundException("Продукт не найден!")).when(mockProductService).findById(50L);

        productServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    void doGetBadRequest() throws IOException {
        Mockito.doReturn("products/1st").when(mockRequest).getPathInfo();

        productServlet.doGet(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doDelete() throws IOException, NotFoundException {
        Mockito.doReturn("products/4").when(mockRequest).getPathInfo();

        productServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockProductService).delete(Mockito.anyLong());
        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @Test
    void doDeleteBadRequest() throws IOException {
        Mockito.doReturn("products/2nd").when(mockRequest).getPathInfo();

        productServlet.doDelete(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Test
    void doPost() throws IOException {
        String expectedName = "Колбаса";
        Long expectedPrice = 8L;

        String jsonInput = "{\"productName\":\"" + expectedName + "\",\"price\":" + expectedPrice + "}";

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.when(mockBufferedReader.readLine())
                .thenReturn(jsonInput)
                .thenReturn(null);

        productServlet.doPost(mockRequest, mockResponse);

        ArgumentCaptor<ProductIncDTO> argumentCaptor = ArgumentCaptor.forClass(ProductIncDTO.class);
        Mockito.verify(mockProductService).save(argumentCaptor.capture());

        ProductIncDTO result = argumentCaptor.getValue();
        Assertions.assertEquals(expectedName, result.getProductName());
        Assertions.assertEquals(expectedPrice, result.getPrice());
    }

    @Test
    void doPut() throws IOException, NotFoundException {
        long productId = 3L;
        String productName = "Какао";
        Long price = 7L;

        String jsonInput = "{\"productId\":" + productId + ",\"productName\":\"" + productName +
                "\",\"price\":" + price + "}";

        Mockito.doReturn("/products/" + productId).when(mockRequest).getPathInfo();
        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.when(mockBufferedReader.readLine())
                .thenReturn(jsonInput)
                .thenReturn(null);

        productServlet.doPut(mockRequest, mockResponse);

        ArgumentCaptor<ProductUpdDTO> argumentCaptor = ArgumentCaptor.forClass(ProductUpdDTO.class);
        Mockito.verify(mockProductService).update(argumentCaptor.capture());

        ProductUpdDTO result = argumentCaptor.getValue();
        Assertions.assertEquals(productId, result.getProductId());
        Assertions.assertEquals(productName, result.getProductName());
        Assertions.assertEquals(price, result.getPrice());
    }

    @Test
    void doPutBadRequest() throws IOException {
        Mockito.doReturn("/products/").when(mockRequest).getPathInfo();

        Mockito.doReturn(mockBufferedReader).when(mockRequest).getReader();
        Mockito.when(mockBufferedReader.readLine())
                .thenReturn("{Bad json:1}")
                .thenReturn(null);

        productServlet.doPut(mockRequest, mockResponse);

        Mockito.verify(mockResponse).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }

}

package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.product.ProductIncDTO;
import dto.product.ProductOutDTO;
import dto.product.ProductUpdDTO;
import exception.NotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ProductService;
import service.impl.ProductServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/products/*"})
public class ProductServlet extends HttpServlet {
    private final transient ProductService productService = ProductServiceImpl.getInstance();
    private final ObjectMapper objectMapper;

    public ProductServlet() {
        this.objectMapper = new ObjectMapper();
    }

    private static void setJsonHeader(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private static String getJson(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader postData = req.getReader();
        String line;
        while ((line = postData.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);

        String responseAnswer;
        try {
            String[] pathPart = req.getPathInfo().split("/");
            if ("all".equals(pathPart[1])) {
                List<ProductOutDTO> productDtoList = productService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(productDtoList);
            } else {
                Long productId = Long.parseLong(pathPart[1]);
                ProductOutDTO productDto = productService.findById(productId);
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(productDto);
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Некорректный ввод...";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String responseAnswer = null;
        try {
            String[] pathPart = req.getPathInfo().split("/");
            Long productId = Long.parseLong(pathPart[1]);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            if (req.getPathInfo().contains("/deleteProduct/")) {
                if ("deleteProduct".equals(pathPart[2])) {
                    Long orderId = Long.parseLong(pathPart[3]);
                    productService.deleteProductFromOrder(productId, orderId);
                }
            } else {
                productService.delete(productId);
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Некорректный ввод...";
        }
        PrintWriter printWriter = resp.getWriter();
        if (responseAnswer != null) {
            printWriter.write(responseAnswer);
        }
        printWriter.flush();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String json = getJson(req);

        String responseAnswer;
        Optional<ProductIncDTO> productResponse;
        try {
            productResponse = Optional.ofNullable(objectMapper.readValue(json, ProductIncDTO.class));
            ProductIncDTO product = productResponse.orElseThrow(IllegalArgumentException::new);
            responseAnswer = objectMapper.writeValueAsString(productService.save(product));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Некорректный ввод...";
        }
        PrintWriter printWriter = resp.getWriter();
        printWriter.write(responseAnswer);
        printWriter.flush();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        setJsonHeader(resp);
        String json = getJson(req);

        String responseAnswer = "";
        Optional<ProductUpdDTO> productResponse;
        try {
            if (req.getPathInfo().contains("/addIntoOrder/")) {
                String[] pathPart = req.getPathInfo().split("/");
                if (pathPart.length > 2 && "addIntoOrder".equals(pathPart[2])) {
                    Long productId = Long.parseLong(pathPart[1]);
                    resp.setStatus(HttpServletResponse.SC_OK);
                    Long orderId = Long.parseLong(pathPart[3]);
                    productService.addProductToOrder(productId, orderId);
                }
            } else {
                productResponse = Optional.ofNullable(objectMapper.readValue(json, ProductUpdDTO.class));
                ProductUpdDTO productUpdDTO = productResponse.orElseThrow(IllegalArgumentException::new);
                productService.update(productUpdDTO);
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Некорректный ввод...";
        }
        PrintWriter printWriter = resp.getWriter();
        if (responseAnswer != null) {
            printWriter.write(responseAnswer);
        }
        printWriter.flush();
    }
}

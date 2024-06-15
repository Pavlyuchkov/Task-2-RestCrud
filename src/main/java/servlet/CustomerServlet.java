package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.customer.CustomerIncDTO;
import dto.customer.CustomerOutDTO;
import dto.customer.CustomerUpdDTO;
import exception.NotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CustomerService;
import service.impl.CustomerServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/customers/*"})
public class CustomerServlet extends HttpServlet {

    private final transient CustomerService customerService;
    private final ObjectMapper objectMapper;

    public CustomerServlet() {
        this.customerService = CustomerServiceImpl.getInstance();
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
                List<CustomerOutDTO> customerOutDtoList = customerService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(customerOutDtoList);
            } else {
                Long customerId = Long.parseLong(pathPart[1]);
                CustomerOutDTO customerDto = customerService.findById(customerId);
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(customerDto);
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
            Long customerId = Long.parseLong(pathPart[1]);
            if (customerService.delete(customerId)) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
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
        Optional<CustomerIncDTO> customerResponse;
        try {
            customerResponse = Optional.ofNullable(objectMapper.readValue(json, CustomerIncDTO.class));
            CustomerIncDTO customer = customerResponse.orElseThrow(IllegalArgumentException::new);
            responseAnswer = objectMapper.writeValueAsString(customerService.save(customer));
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

        String responseAnswer = null;
        Optional<CustomerUpdDTO> customerResponse;
        try {
            customerResponse = Optional.ofNullable(objectMapper.readValue(json, CustomerUpdDTO.class));
            CustomerUpdDTO customerUpdDTO = customerResponse.orElseThrow(IllegalArgumentException::new);
            customerService.update(customerUpdDTO);
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
package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.order.OrderIncDTO;
import dto.order.OrderOutDTO;
import dto.order.OrderUpdDTO;
import exception.NotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.OrderService;
import service.impl.OrderServiceImpl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/orders/*"})
public class OrderServlet extends HttpServlet {

    private final OrderService orderService = OrderServiceImpl.getInstance();
    private final ObjectMapper objectMapper;

    public OrderServlet() {
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
                List<OrderOutDTO> orderDtoList = orderService.findAll();
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(orderDtoList);
            } else {
                Long orderId = Long.parseLong(pathPart[1]);
                OrderOutDTO orderOutDTO = orderService.findById(orderId);
                resp.setStatus(HttpServletResponse.SC_OK);
                responseAnswer = objectMapper.writeValueAsString(orderOutDTO);
            }
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Неверный запрос!";
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
            Long orderId = Long.parseLong(pathPart[1]);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            orderService.delete(orderId);
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Неверный запрос!";
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
        Optional<OrderIncDTO> orderResponse;
        try {
            orderResponse = Optional.ofNullable(objectMapper.readValue(json, OrderIncDTO.class));
            OrderIncDTO orderIncDTO = orderResponse.orElseThrow(IllegalArgumentException::new);
            responseAnswer = objectMapper.writeValueAsString(orderService.save(orderIncDTO));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Некорректный запрос!";
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
        Optional<OrderUpdDTO> orderResponse;
        try {
            orderResponse = Optional.ofNullable(objectMapper.readValue(json, OrderUpdDTO.class));
            OrderUpdDTO orderUpdateDto = orderResponse.orElseThrow(IllegalArgumentException::new);
            orderService.update(orderUpdateDto);
        } catch (NotFoundException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            responseAnswer = e.getMessage();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            responseAnswer = "Некорректный запрос!";
        }
        PrintWriter printWriter = resp.getWriter();
        if (responseAnswer != null) {
            printWriter.write(responseAnswer);
        }
        printWriter.flush();
    }
}
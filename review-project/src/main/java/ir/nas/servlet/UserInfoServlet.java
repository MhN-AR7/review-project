package ir.nas.servlet;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ir.nas.dto.UserInfoRequestDTO;
import ir.nas.dto.UserInfoResponseDTO;
import ir.nas.repository.UserInfoRepository;
import ir.nas.service.UserInfoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "userInfoServlet", value = "/user-info/*")
public class UserInfoServlet extends HttpServlet
{
    private final UserInfoService uService = new UserInfoService(new UserInfoRepository());
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        String pathInfo = req.getPathInfo();
        System.out.println(pathInfo);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        if (!"application/json".equalsIgnoreCase(req.getContentType())) {
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Content Type must be application/json");
            return;
        }

        UserInfoRequestDTO userInfoRequestDTO = null;

        try {
            userInfoRequestDTO = this.objectMapper.readValue(req.getReader(), UserInfoRequestDTO.class);
        } catch (JsonParseException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid Json Body");
        }

        UserInfoResponseDTO userInfoResponseDTO = this.uService.create(userInfoRequestDTO);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.setContentType("applciation/json");
        resp.setCharacterEncoding("UTF-8");

        this.objectMapper.writeValue(resp.getWriter(), userInfoResponseDTO);
    }
}

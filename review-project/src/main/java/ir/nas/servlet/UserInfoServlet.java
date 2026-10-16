package ir.nas.servlet;

import java.io.IOException;
import java.util.Optional;

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

        if(Optional.ofNullable(pathInfo).isEmpty() || pathInfo.equals("/")) {
            this.objectMapper.writeValue(resp.getWriter(), uService.findAll());
        }
        else {
            this.objectMapper.writeValue(resp.getWriter(), uService.findById(
                    Long.parseLong(pathInfo.substring(1))
            ).get());
        }
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

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        long id;

        if(Optional.ofNullable(pathInfo).isEmpty() || !pathInfo.matches("/\\d+")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Numeric id is required");
            return;
        }

        if (!"application/json".equalsIgnoreCase(req.getContentType())) {
            resp.sendError(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, "Content type must be application/json");
            return;
        }

        UserInfoRequestDTO userInfoRequestDTO;

        try {
            userInfoRequestDTO = this.objectMapper.readValue(req.getReader(), UserInfoRequestDTO.class);
        } catch (JsonParseException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid json body");
            return;
        }

        id = Long.parseLong(pathInfo.substring(1));

        UserInfoResponseDTO userInfoResponseDTO = this.uService.update(id, userInfoRequestDTO);

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        objectMapper.writeValue(resp.getWriter(), userInfoResponseDTO);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        long id;

        if (Optional.ofNullable(pathInfo).isEmpty() || !pathInfo.matches("/\\d+")) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Numeric id is required");
            return;
        }

        id = Long.parseLong(pathInfo.substring(1));

        boolean isDeleted = uService.delete(id);

        if (isDeleted) resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        else resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}

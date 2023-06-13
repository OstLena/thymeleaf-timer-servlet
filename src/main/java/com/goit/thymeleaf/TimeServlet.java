package com.goit.thymeleaf;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static com.goit.thymeleaf.ThymeleafAppServlet.BASE_URL;
import static com.goit.thymeleaf.ThymeleafAppServlet.THYMELEAF;

public class TimeServlet extends HttpServlet {

    private String baseUrl;
    private TemplateEngine templateEngine;

    @Override
    public void init(ServletConfig config) throws ServletException {
        templateEngine = (TemplateEngine) config.getServletContext().getAttribute(THYMELEAF);
        baseUrl = (String) config.getServletContext().getAttribute(BASE_URL);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        createCustomerPage(req, resp);
    }

    private void createCustomerPage(HttpServletRequest req, HttpServletResponse resp) {
        try {
            resp.setContentType("text/html; charset=utf-8");

            Map<String, Object> pageParameters = Map.of(
                    "currentTime", currentTime(req, resp),
                    "baseUrl", baseUrl
            );

            Context simpleContext = new Context(
                    req.getLocale(),
                    pageParameters
            );

            templateEngine.process("time", simpleContext, resp.getWriter());
            resp.getWriter().close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String currentTime(HttpServletRequest req, HttpServletResponse resp) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss O");
        String timezone = req.getParameter("timezone");
        String zoneId;
        if (timezone != null) {
            zoneId = timezone;
            resp.addCookie(new Cookie("timezone", timezone));
        } else {
            String timezoneFromCookies = readTimezoneFromCookies(req);
            if (timezoneFromCookies != null) {
                zoneId = timezoneFromCookies;
            } else {
                zoneId = "UTC";
            }
        }
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(zoneId));
        return dtf.format(now);
    }

    private String readTimezoneFromCookies(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if ("timezone".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}

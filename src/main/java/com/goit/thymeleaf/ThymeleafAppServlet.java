package com.goit.thymeleaf;

import com.goit.config.AppEnv;
import com.goit.config.ThymeleafServletConfiguration;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServlet;
import org.thymeleaf.TemplateEngine;

import java.net.MalformedURLException;
import java.net.URL;


public class ThymeleafAppServlet extends HttpServlet {
    public static final String THYMELEAF = "org.thymeleaf";
    public static final String APP_ENV = "app.env";
    public static final String BASE_URL = "app.base.url";
    public static final String HOST_PORT = "server.host-port";
    public static final String PROTOCOL = "server.protocol";
    @Override
    public void init(ServletConfig config) {
        AppEnv env = AppEnv.load();
        try {
            String baseUrl = getBaseUrl(env);
            TemplateEngine engine = ThymeleafServletConfiguration.setup(config);
            config.getServletContext().setAttribute(THYMELEAF, engine);
            config.getServletContext().setAttribute(BASE_URL, baseUrl);
        } catch (Exception e) {
            throw new RuntimeException("Configuration setup failed", e);
        }
    }

    private static String getBaseUrl(AppEnv env) {
        try {
            String host = env.getProperty(HOST_PORT);
            String protocol = env.getProperty(PROTOCOL);
            URL url = new URL(protocol + "://" + host);
            return url.toString();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
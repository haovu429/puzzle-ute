package hcmute.puzzle.filter;

//import javax.servlet.*;
//import javax.servlet.annotation.*;
//import javax.servlet.http.HttpServletResponse;

import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(filterName = "CorsFilter")
public class CorsFilter implements Filter {
    public void init(FilterConfig config) throws ServletException {
    }

    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {

        HttpServletResponse newResponse = (HttpServletResponse) response;
        newResponse.setHeader("Access-Control-Allow-Origin", "*");
        newResponse.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
        newResponse.setHeader("Access-Control-Max-Age", "3600");
        newResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Content-Length, X-Requested-With");
        chain.doFilter(request, response);

    }
}

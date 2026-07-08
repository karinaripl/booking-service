package com.booking.gateway_service.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.util.*;

public class HeaderMutatingRequest extends HttpServletRequestWrapper {

    private final String userEmail;

    public HeaderMutatingRequest(HttpServletRequest request, String userEmail) {
        super(request);
        this.userEmail = userEmail;
    }

    @Override
    public String getHeader(String name) {
        if ("X-User-Email".equalsIgnoreCase(name)) {
            return userEmail;
        }
        return super.getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if ("X-User-Email".equalsIgnoreCase(name)) {
            return Collections.enumeration(List.of(userEmail));
        }
        return super.getHeaders(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> names = Collections.list(super.getHeaderNames());
        names.add("X-User-Email");
        return Collections.enumeration(names);
    }
}

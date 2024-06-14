package com.ticket.TicketSystem;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MyErrorController implements ErrorController {

    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (requestUri != null && requestUri.startsWith("/admin/api")) {
                // Return JSON response for API requests
                Map<String, Object> errorAttributes = new HashMap<>();
                errorAttributes.put("status", statusCode);
                errorAttributes.put("error", HttpStatus.valueOf(statusCode).getReasonPhrase());

                if (statusCode == HttpStatus.NOT_FOUND.value()) {
                    errorAttributes.put("message", "Page not found");
                } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                    errorAttributes.put("message", "Internal server error");
                } else {
                    errorAttributes.put("message", "An unexpected error occurred");
                }

                return new ResponseEntity<>(errorAttributes, HttpStatus.valueOf(statusCode));
            } else if (requestUri != null && requestUri.startsWith("/admin")) {
                // Return error page for non-API requests
                if (statusCode == HttpStatus.NOT_FOUND.value()) {
                    return "admin/error-404";
                } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                    return "admin/error-500";
                } else {
                    return "admin/error";
                }
            }
        }

        // Fallback to general error handling
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            if (requestUri != null && requestUri.startsWith("/api")) {
                // Return JSON response for API requests
                Map<String, Object> errorAttributes = new HashMap<>();
                errorAttributes.put("status", statusCode);
                errorAttributes.put("error", HttpStatus.valueOf(statusCode).getReasonPhrase());

                if (statusCode == HttpStatus.NOT_FOUND.value()) {
                    errorAttributes.put("message", "Page not found");
                } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                    errorAttributes.put("message", "Internal server error");
                } else {
                    errorAttributes.put("message", "An unexpected error occurred");
                }

                return new ResponseEntity<>(errorAttributes, HttpStatus.valueOf(statusCode));
            } else{
                if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error-404";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error-500";
            }
            }
        }

        return "error";
    }
}

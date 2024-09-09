package maqlu.maqlulibrary.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ErrorPagesControllers implements ErrorController {
    @RequestMapping(value="/error")
    public ResponseEntity<String> errorHandler(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {

            int statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                return ResponseEntity.ok("error 400");
            }

            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return ResponseEntity.ok("error 403");
            }

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return ResponseEntity.ok("error 404");
            }

            if (statusCode == HttpStatus.REQUEST_TIMEOUT.value()) {
                return ResponseEntity.ok("error 408");
            }

            if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return ResponseEntity.ok("error 500");
            }

            if (statusCode == HttpStatus.BAD_GATEWAY.value()) {
                return ResponseEntity.ok("error 502");
            }

            if (statusCode == HttpStatus.SERVICE_UNAVAILABLE.value()) {
                return ResponseEntity.ok("error 503");
            }

            if (statusCode == HttpStatus.GATEWAY_TIMEOUT.value()) {
                return ResponseEntity.ok("error 504");
            }
        }
        return ResponseEntity.ok("error");
    }

    public String getErrorPath() {
        return "/error";
    }
}

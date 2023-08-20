/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pedantic.config;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author sayedazp
 */
@Provider
public class ExceptionMappers implements ExceptionMapper<ConstraintViolationException>{
    

    @Override
    public Response toResponse(ConstraintViolationException exception) {
//        Map<String, String> vioMap = new HashMap<>();
        JsonObjectBuilder errorBuilder = Json.createObjectBuilder().add("Error", "there were data violation errors");
        JsonObjectBuilder obB = Json.createObjectBuilder();
        Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
        for (ConstraintViolation cv: constraintViolations)
        {
            String prop = cv.getPropertyPath().toString().split("\\.")[2];
            String msg = cv.getMessage();
            obB.add(prop,msg);
        }
        errorBuilder.add("violated fields", obB.build());
        return Response.status(Response.Status.EXPECTATION_FAILED)
                .entity(errorBuilder.build()).build();
    }
}

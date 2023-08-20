/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pedantic.config;

import com.pedantic.entity.TodoUser;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.math.BigDecimal;

/**
 *
 * @author sayedazp
 */
//@Provider
//@Produces(MediaType.APPLICATION_JSON)
public class MyTodoUserBodyWriter implements MessageBodyWriter<TodoUser>{

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return TodoUser.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(TodoUser t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        JsonObject todoUserJsonObject = Json.createObjectBuilder().add("email", t.getEmail())
                .add("fullName", t.getFullName())
                .add("id", t.getId()).build();
        OutputStreamWriter streamWriter = new OutputStreamWriter(entityStream);
        streamWriter.write(todoUserJsonObject.toString());
        streamWriter.close();
        entityStream.close();
    }
}

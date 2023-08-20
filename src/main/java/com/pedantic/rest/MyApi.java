package com.pedantic.rest;

import com.pedantic.entity.TodoUser;
import com.pedantic.service.JDBC;
import com.pedantic.service.MySession;
import com.pedantic.service.PersistenceService;
import com.pedantic.service.QueryService;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.jms.Session;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import java.util.List;

@Path("hello") //https://foo.bax/api/v1/hello/{name}
public class MyApi {
    
    @EJB
    JDBC ss;

    @Inject
    PersistenceService ps;

    @Inject
    MySession session;
    
    @Inject
    QueryService qs;

    @Path("{name}")
    @GET
    public JsonObject greet(@PathParam("name") String name) {
        TodoUser tu = new TodoUser();
        tu.setFullName("sayed");
        tu.setPassword("12222222223");
        session.setEmail("sss@sss.com");
        tu.setEmail("sss@sss.com");
        ps.saveTodoUser(tu);
        return Json.createObjectBuilder().add("name", name)
                .add("greeting", "Hello, " + name)
                .add("message", "Hello from Jakarta EE!").build();
    }
    @Path("tr")
    @GET
    public void print()
    {
        List<TodoUser> todos = qs.findAllTodoUsers();
        for (TodoUser x: todos)
        {
            System.out.println(x.getId() + " and email is " + x.getEmail());
        }
    }
}

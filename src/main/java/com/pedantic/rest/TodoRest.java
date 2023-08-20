/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pedantic.rest;

import com.pedantic.config.SecureAuth;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.pedantic.entity.Todo;
import com.pedantic.service.PersistenceService;
import com.pedantic.service.QueryService;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;

/**
 *
 * @author sayedazp
 */
@Path("todo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
//@SecureAuth
public class TodoRest {
    
    @Inject
    private PersistenceService ps;
    
    @Inject 
    private QueryService qs;
    
    @Inject
    private UriInfo uriInfo;
    
    @POST
    @Path("create")
    @SecureAuth
    public Response createTodo(Todo todo)
    {
        ps.saveTodo(todo);
        URI todoByIdPath = uriInfo.getRequestUriBuilder().path(TodoRest.class).path(todo.getId().toString()).build();
        URI listPath = uriInfo.getBaseUriBuilder().path(TodoRest.class).path(TodoRest.class, "listTodos").build();
        JsonObject jsonObject =  Json.createObjectBuilder().add("_links", Json.createArrayBuilder()
                .add(Json.createObjectBuilder()
                .add("_self", todoByIdPath.toString())
                .add("_others",listPath.toString()).build())).build();
        return Response.ok(jsonObject.toString()).header("links",jsonObject.toString()).build();
//        return Response.ok(todo).build();
    }
    @POST
    @Path("{id}")
    public Response findByID(@NotNull @PathParam("id") Long id)
    {
       return Response.ok(qs.findTodoById(id)).build();
    }
    
    @GET
    @Path("list")
    @SecureAuth
    public Response listTodos()
    {
        return Response.ok(qs.findAllTodos()).build();
    }
    
    @GET
    @Path("find")
    public Response findTodoById(@NotNull @QueryParam("id") Long id)
    {
        return Response.ok(qs.findTodoById (id)).build();
    }
    
    @PUT
    @Path("mark")
    public Response markTodoAsCompleted(@NotNull @QueryParam("id") Long id)
    {
        qs.markTodoAsCompleted(id);
        return Response.ok().build();
    }
    
    @GET
    @Path("completed")
    public Response getAllCompleted()
    {
        return Response.ok(qs.getAllTodos(true)).build();
    }
    
    @GET
    @Path("uncompleted")
    public  Response getAllUncompleted()
   {
        return Response.ok(qs.getAllTodos(false)).build();
   }
    @GET
    @Path("due-date")
   public Response getTodoByDueDate(@NotNull @QueryParam("date") String date)
   {
        LocalDate dueDate = LocalDate.parse(date);
        return Response.ok(qs.getTodosByLocalDate(dueDate)).build();
   }
           
   @PUT
   @Path("archive")
   public  Response archiveTodo(@NotNull @QueryParam("id") Long id)
   {
       qs.markTodoAsArchived(id);
       return Response.ok().build();
   }
}

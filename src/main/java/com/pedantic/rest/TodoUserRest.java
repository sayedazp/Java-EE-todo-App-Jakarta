/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pedantic.rest;

import com.pedantic.config.SecureAuth;
import com.pedantic.entity.TodoUser;
import com.pedantic.service.MySession;
import com.pedantic.service.PersistenceService;
import com.pedantic.service.QueryService;
import com.pedantic.service.SecurityUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.batch.api.BatchProperty;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.HttpHeaders;




import jakarta.inject.Inject;
import jakarta.inject.Scope;
import jakarta.json.Json;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static jakarta.ws.rs.core.HttpHeaders.AUTHORIZATION;
import jakarta.ws.rs.core.SecurityContext;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;


/**
 *
 * @author sayedazp
 */
@Path("user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TodoUserRest {
    
    @Inject @BatchProperty Logger logger;
    
    @Inject
    private MySession mySession;
   
    @Context
    UriInfo uriInfo;
    
    @Inject
    private SecurityUtil securityUtil;
    
    @Inject
    private PersistenceService ps;
    
    @Inject
    private QueryService qs;
    
    @Inject
    private SecurityContext sc;
    
    @Path("create")
    @POST
    public Response createTodoUser(@NotNull @Valid TodoUser todoUser)       
    {
        
        ps.saveTodoUser(todoUser);
        return Response.ok(todoUser).build();
    }
    
    @Path("update")
    @POST
    @SecureAuth
    public Response updateTodoUser(@NotNull @Valid TodoUser todoUser)
    {
        ps.saveTodoUser(todoUser);
        return Response.ok(todoUser).build();
    }
    
    @Path("find/{email}")
    @GET
    @SecureAuth
    public TodoUser findTodoUserByEmail(@PathParam("email") @NotNull String email)
    {
        return qs.findTodoUserByEmail(email);
    }
    
    @Path("query")
    @GET
    @SecureAuth
    public TodoUser findTodoUserByEmailQueryVer(@QueryParam("email") @NotNull String email)
    {
        System.out.println(email);
        return qs.findTodoUserByEmail(email);
    }

    @GET
    @Path("search")
    @SecureAuth
    public Response searchTodoUserByName(@NotNull @QueryParam("name") String name)
    {
        return (Response.ok(qs.findTodoUsersByName(name)).build());
    }
    
   
    @GET
    @Path("count")
    @SecureAuth
    public Response countTodoUserByEmail(@QueryParam("email") @NotNull String email) {

        return Response.ok(qs.countTodoUserByEmail(email)).build();
    }

    @GET
    @Path("list")
    @SecureAuth
    public Response listAllTodoUsers() {
        
        System.out.println("fron endpoint " + sc.getUserPrincipal().getName());
        return Response.ok(qs.findAllTodoUsers()).build();
    }

    @PUT
    @Path("update-email")
    @SecureAuth
    public Response updateEmail(@NotNull @QueryParam("id") Long id, @NotNull @QueryParam("email") String email) {
        TodoUser todoUser = ps.updateTodoUserEmail(id, email);

        return Response.ok(todoUser).build();
    }
//            Key key = securityUtil.generateKey(email + "la;skdlsakdlkas;dk;saldk;lsadklksadjashdkashdkjashdashjdhaskjd");

    private String getToken(String email)
    {
        System.out.println("entered get token");
        Key key = securityUtil.generateKey(email);
        System.out.println("key generated" + key);
        String token = Jwts.builder().setSubject(email).setIssuer(uriInfo.getAbsolutePath().toString())

                .setIssuedAt(new Date()).setExpiration(securityUtil.toDate(LocalDateTime.now().plusMinutes(15)))

                .signWith(key, SignatureAlgorithm.HS512).setAudience(uriInfo.getBaseUri().toString())

                .compact();
//        logger
//        logger.log(Level.INFO, "Generated token is {0}", token);
        return token;
        
    }
    
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@NotEmpty(message = "email must be set")
                        @FormParam("email") String email,
                        @NotEmpty(message = "password must be set")
                        @FormParam("password") String password)
    {
        /*
        Authentication
        Generate token
        return generated token
        */
        if (!securityUtil.authenticateUser(email, password))
        {
            throw new SecurityException("email or password is invalid");
        }
        System.out.println("entered email is " + email + " password is " + password);
        String token = getToken(email);
        mySession.setEmail(email);
        return Response.ok().header(AUTHORIZATION, "Bearer " + token).build();
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pedantic.config;

import com.pedantic.service.MySession;
import com.pedantic.service.SecurityUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Key;
import java.security.Principal;

/**
 *
 * @author sayedazp
 */
@Provider
@SecureAuth
@Priority(Priorities.AUTHENTICATION)
public class SecurityFilter implements ContainerRequestFilter{
    public static final String BEARER = "Bearer";
    @Inject
    private SecurityUtil securityUtil;
    
    @Inject
    private MySession mySession;
    
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
     // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
     String authString = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
     if (authString == null || authString.isEmpty() || !authString.startsWith(BEARER))
     {
         System.err.println("No valid String token found");
         JsonObject jsonObject = Json.createObjectBuilder().add("error-message", "No valid String token found").build();
         throw new NotAuthorizedException(Response.status(Response.Status.UNAUTHORIZED).entity(jsonObject).build());
         
     }
     
     String token = authString.substring(BEARER.length()).trim();
//        System.err.println("recieved token : " + token);
     
     try{
         System.out.println(mySession.getEmail());
        Key key = securityUtil.generateKey(mySession.getEmail());
//         Jwt jwt1 =  Jwts.parser().setSigningKey(key).parse(token);
         Jws<Claims> jwsClaims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
         SecurityContext originalSecurityContext = requestContext.getSecurityContext();
         System.out.println( "subject is " + jwsClaims.getBody().getSubject());
         SecurityContext newSecurityContext = new SecurityContext() {
             @Override
             public Principal getUserPrincipal() {
                 return new Principal() {
                     @Override
                     public String getName() {
                         return jwsClaims.getBody().getSubject();
                     }
                 };
             }

             @Override
             public boolean isUserInRole(String role) {
                 return originalSecurityContext.isUserInRole(role);
             }

             @Override
             public boolean isSecure() {
                 return originalSecurityContext.isSecure();
             }

             @Override
             public String getAuthenticationScheme() {
                 return  originalSecurityContext.getAuthenticationScheme();
             }
         };
            
         requestContext.setSecurityContext(newSecurityContext);
     }catch (Exception e){
         System.out.println(e);
         requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
         
     }
     
    }
}

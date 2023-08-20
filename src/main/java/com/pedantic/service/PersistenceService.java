package com.pedantic.service;


import com.pedantic.entity.Todo;
import com.pedantic.entity.TodoUser;
import jakarta.annotation.PostConstruct;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.Singleton;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.sqlite.javax.SQLiteConnectionPoolDataSource;
import org.postgresql.Driver;
import org.sqlite.SQLiteDataSource;
import org.sqlite.jdbc4.JDBC4PooledConnection;
import org.postgresql.jdbc3.Jdbc3PoolingDataSource;

@DataSourceDefinition(
        className = "org.postgresql.jdbc3.Jdbc3PoolingDataSource",
        name = "java:/PostgresDS",
        url = "jdbc:postgresql://localhost:5432/postgres",
        user = "postgres",
        password = "sayedspassword"
        )
@Stateless
public class PersistenceService {
    
    @Inject
    private SecurityUtil securityUtil;

    @Inject
    private MySession mySession;

    @Inject
    private QueryService queryService;

//    @Context
//    private SecurityContext sc;
  

    @PersistenceContext
    EntityManager entityManager;        //new SomeConcreteImplementation;

    //1. Grab email of currently executing user
    //2. Grab same user from DB using grabbed email in 1.
    //3. Assign the user returned in 2 to a new Todo



    public TodoUser saveTodoUser(TodoUser todoUser) {
        //TODO Implement
        //someInterface.saveToDB(todoUser)
        System.out.print( "1" + todoUser.getEmail());
        List list = queryService.countTodoUserByEmail(todoUser.getEmail());
        System.out.print( "2" + todoUser.getEmail());
        Long count = (Long) list.get(0);
        
        Map<String, String> credentialMap = securityUtil.hashPassword(todoUser.getPassword());
                System.out.print( "3" + todoUser.getEmail());

        if (todoUser.getId() == null && count == 0) { //If it's a new entity, save it
            
            System.out.print(todoUser.getPassword());
            System.out.print(todoUser.getSalt());
            todoUser.setPassword(credentialMap.get("hashedPassword"));
            todoUser.setSalt(credentialMap.get("salt"));

            entityManager.persist(todoUser); //#save}
        }
        credentialMap.clear();
        return todoUser;
    }


    public TodoUser updateTodoUser(TodoUser todoUser) {


        List list = queryService.countTodoUser(todoUser.getId(), todoUser.getEmail());
        Long count = (Long) list.get(0);


        if (todoUser.getId() != null && count == 1) {
            entityManager.merge(todoUser);
        }
        return todoUser;
    }




 
    public TodoUser updateTodoUserEmail(Long id, String email) {
        //Count and see if the email exists or not
        //Find the TodoUser
        //Assign the email
        //Merge back to the persistence context
        //return

        List list = queryService.countTodoUserByEmail(email);
        Long count = (Long) list.get(0);

        if (count == 0) {
            TodoUser todoUser = queryService.findTodoUser(id); //Substitute for findTodoUserById(Long id)
            if (todoUser != null) {
                todoUser.setEmail(email);
                entityManager.merge(todoUser);

                return todoUser;
            }

        }

        return null;


    }


    public Todo saveTodo(Todo todo) {
        System.out.print("entered");
        String email = "sayed@sayed.com";

        TodoUser todoUserByEmail = queryService.findTodoUserByEmail(email);


        if (todo.getId() == null && todoUserByEmail != null) {

            todo.setTodoOwner(todoUserByEmail);
            entityManager.persist(todo);
        } else {
            entityManager.merge(todo);
        }

        return todo;
    }
}

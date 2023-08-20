package com.pedantic.service;

import com.pedantic.entity.Todo;
import com.pedantic.entity.TodoUser;
import jakarta.annotation.PostConstruct;

import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.NonUniqueResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Stateless
public class QueryService {

    @PersistenceContext
    EntityManager entityManager;

    @Inject
    private MySession mySession;
    
    @Inject
    private SecurityContext sc;
    
    @Inject
    SecurityUtil securityUtil;
    
    
    
//    private String userEmail;
    
//    @PostConstruct
//    private void init()
//    {
//        userEmail = sc.getUserPrincipal().getName();
//    }
        
    public TodoUser findTodoUserByEmail(String email) {
        //TODO impl method body


        try {
            return entityManager.createNamedQuery(TodoUser.FIND_TODO_USER_BY_EMAIL, TodoUser.class)
                    .setParameter("email", email).getSingleResult();

        } catch (NonUniqueResultException | NoResultException e) {
            
            return null;
        }


    }

    public List<TodoUser> findAllTodoUsers() {
        
        return entityManager.createNamedQuery(TodoUser.FIND_ALL_TODO_USERS, TodoUser.class).getResultList();
    }


    public TodoUser findTodoUserById(Long id) {

        try {
            return entityManager.createNamedQuery(TodoUser.FIND_TODO_USER_BY_ID, TodoUser.class)
                    .setParameter("id", id).setParameter("email", sc.getUserPrincipal().getName()).getSingleResult();
        } catch (NonUniqueResultException | NoResultException e) {
            return null;
        }

    }

    public TodoUser findTodoUser(Long id) { //Todo- discard for above method
        return entityManager.find(TodoUser.class, id);
    }

    public Collection<TodoUser> findTodoUsersByName(String name) {

        return entityManager.createNamedQuery(TodoUser.FIND_TODO_BY_NAME, TodoUser.class)
                .setParameter("name", "%" + name + "%").getResultList(); //jo
    }


    public Collection<Todo> findAllTodos() {
        return entityManager.createNamedQuery(Todo.FIND_ALL_TODOS_BY_ONWER_EMAIL, Todo.class)
                .setParameter("email", sc.getUserPrincipal().getName()).getResultList();
    }

    public List countTodoUserByEmail(String email) {
        List resultList = entityManager.createNativeQuery("select count (id) from TodoUserTable where  exists (select id from TodoUserTable where email = ?)")
                .setParameter(1, email).getResultList();

        return resultList;
    }


    public List countTodoUser(Long id, String email) {
        return entityManager.createNativeQuery("select count (id) from TodoUserTable where  exists (select id from TodoUserTable where email = ?1 and id = ?2)")
                .setParameter(1, email).setParameter(2, id).getResultList();
    }
    
    public Todo findTodoById(Long id)
    {
        List <Todo> x = entityManager.createQuery("select t from Todo t where t.id = :id and t.todoOwner.email = :email", Todo.class).setParameter("id", id)
                .setParameter("email", sc.getUserPrincipal().getName()).getResultList();
        if (!x.isEmpty())
        {
            return x.get(0);
        }else
        {
            return null;
        }
    }
    
    public void markTodoAsCompleted(Long id)
    {
//        entityManager.createQuery("update Todo t set t.completed = true").executeUpdate(); goes around the persistence service
        Todo todo = findTodoById(id);
        if (todo != null)
        {
            todo.setCompleted(true);
            entityManager.merge(todo);
        }
    }
    
    public List<Todo> getAllTodos(boolean state)
    {
        return  entityManager.createQuery("select t from todo t where t.todoOwner.email = :email and t.completed = :state")
                .setParameter("email", sc.getUserPrincipal().getName()).setParameter("state", state)
                .getResultList();
    }
    
    public List<Todo> getTodosByLocalDate(LocalDate ld)
    {
        return entityManager.createQuery("select t from Todo t where t.todoOwner.email = :email and t.dueDate = :dueDate")
                .setParameter("dueDate", ld).setParameter("email",sc.getUserPrincipal().getName())
                .getResultList();
    }
    
    public void markTodoAsArchived(Long id)
    {
//        entityManager.createQuery("update Todo t set t.completed = true").executeUpdate(); goes around the persistence service
        Todo todo = findTodoById(id);
        if (todo != null)
        {
            todo.setArchived(true);
            entityManager.merge(todo);
        }
    }
    
    public boolean authenticateUser(String email, String plainTextPassword)
    {
        TodoUser todoUser = findTodoUserByEmail(email);
        
        if (todoUser != null)
        {
            return securityUtil.passwordsMatch(todoUser.getPassword(), todoUser.getSalt(), plainTextPassword);
        }
        return false;
    }
}

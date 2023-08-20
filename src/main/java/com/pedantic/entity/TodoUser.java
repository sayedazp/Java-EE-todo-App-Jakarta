package com.pedantic.entity;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.ArrayList;
import java.util.Collection;
@Entity
@Table(name = "TodoUserTable")

@NamedQuery(name =TodoUser.FIND_TODO_USER_BY_EMAIL, query = "select tU from TodoUser  tU where tU.email = :email")

@NamedQuery(name = TodoUser.FIND_ALL_TODO_USERS, query = "select todoUser from TodoUser  todoUser order by todoUser.fullName") //select * from TodoUserTable...
@NamedQuery(name = TodoUser.FIND_TODO_USER_BY_ID, query = "select t from TodoUser t where t.id = :id and t.email = :email")
@NamedQuery(name = TodoUser.FIND_TODO_BY_NAME, query = "select t from TodoUser t where t.fullName like :name ")
public class TodoUser extends AbstractEntity { //TodoUser - Database table
    //BY email, by id, fullNamed like jo

    public static final String FIND_TODO_USER_BY_EMAIL = "TodoUser.findByEmail";
    public static final String FIND_ALL_TODO_USERS = "TodoUser.findAll";
    public static final String FIND_TODO_USER_BY_ID = "TodoUser.findByIdAndEmail";
    public static final String FIND_TODO_BY_NAME = "TodoUser.findByName";

    @Column(length = 100)
    @NotEmpty(message = "An email must be set")
    @Email(message = "Email must be in the format user@somedomain.com")
    private String email; //varchar 255 user@domain.com

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Password cannot be empty")
    @Size(min = 8, message = "Password must be a min of 8")
//@Pattern(regexp = "", message = "Password must have at least one upper case, " +
//        "one lower case, a digit and must contain at least one of $%&#!")
    private String password; //#%AdhhddS87987fjvjof
    
    @JsonIgnore
    private String salt;

    @NotEmpty(message = "Name must be set")
    @Size(min = 2, max = 100, message = "Name must be a min of 2 and max of 100 characters")
    private String fullName;

//    @OneToMany
//    private final Collection<Todo> todos = new ArrayList<>();


    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getSalt()
    {
        return salt;
    }
    public void setSalt(String salt)
    {
        this.salt = salt;
    }
}

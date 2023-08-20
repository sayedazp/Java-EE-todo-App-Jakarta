/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.pedantic.service;

import jakarta.ejb.Stateless;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author sayedazp
 */
@Stateless
public class JDBC {
    public void connect()
    {
        try{
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:/home/sayedazp/Documents/music.db");
            System.err.println("connected succesfully");
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM songs");
            while(rs.next())
            {
                System.err.println(rs.getString("title"));
            }
            conn.close();
            statement.close();
          
        }catch(Exception e ){
            System.out.println(e);
        }
        finally{
        }
    }
}

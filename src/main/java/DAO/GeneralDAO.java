package DAO;

import java.sql.*;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import java.io.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Optional;
import java.util.regex.*;
import java.util.ArrayList;

public class GeneralDAO {

    static final String url = "jdbc:postgresql://localhost:5432/groupcommunication";
    static final String user = "postgres";
    static final String password = "postgres";

    public static ArrayList getGroupsAdmin(){
        ArrayList groups = new ArrayList();

        try{
            Connection conn = connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT name FROM groups");
            while(rs.next()){
                groups.add(rs.getString(1));
            }

        }catch(Exception e){
            System.out.println(e);
        }
        return groups;
    }

    public static ArrayList getGroups(String user){
        ArrayList groups = new ArrayList();

        try{
            Connection conn = connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT groups.name FROM groups, users, user_groups WHERE users.id = user_groups.user_id AND groups.id = user_groups.group_id AND users.username = 'jonasramos'");
            while(rs.next()){
                groups.add(rs.getString(1));
            }

        }catch(Exception e){
            System.out.println(e);
        }
        return groups;
    }

    public static boolean checkLogin(String data){
//        Pattern pattern = Pattern.compile("'(.*?)'");
//        Matcher matcher = pattern.matcher(data);
//
//        if (matcher.find())
//        {
//            System.out.println(matcher);
//        }

        try{
            Connection conn = connect();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT username FROM users WHERE username = 'jonasramos' AND senha = '123'");
            while(rs.next()){
                if(rs.getString(1) != ""){
                    return true;
                }
            }

        }catch(Exception e){
            System.out.println(e);
            return false;
        }
        return false;
    }

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

}
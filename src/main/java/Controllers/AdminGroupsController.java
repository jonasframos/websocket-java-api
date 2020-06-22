package Controllers;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import java.io.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Optional;
import java.util.ArrayList;

import DAO.GeneralDAO;

@ServerEndpoint("/adminGroups")
public class AdminGroupsController {

    @OnMessage
    public String getGroups(String username) {
        ArrayList response = GeneralDAO.getGroupsAdmin();

        String data = "";

        for(int i = 0; i < response.size(); i++){
            data += response.get(i) + " ";
        }

        System.out.println(data);
        return data;
    }
}
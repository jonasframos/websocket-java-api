package Controllers;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import java.io.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Optional;

import DAO.GeneralDAO;

@ServerEndpoint("/login")
public class LoginController {

    @OnMessage
    public boolean login(String data) {
        boolean response = GeneralDAO.checkLogin(data);

        return response;
    }

}

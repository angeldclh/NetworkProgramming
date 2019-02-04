/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * @ServerEndpoint gives the relative name for the end point This will be accessed via ws://localhost:8080/EchoChamber/echo Where "localhost" is the address of the host, "EchoChamber" is the name of the package and "echo" is the address to access this class from the server
 */
/**
 *
 * @author angel
 */
@ServerEndpoint("/echo/rooms/{roomid}/user/{nick}")
public class EchoServer {

    /**
     * @param session
     * @param roomid
     * @param nick
     * @OnOpen allows us to intercept the creation of a new session. The session class allows us to send data to the user. In the method onOpen, we'll let the user know that the handshake was successful.
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("roomid") final String roomid, @PathParam("nick") final String nick) {
        System.out.println(session.getId() + " has opened a connection");
        session.getUserProperties().put("roomid", roomid);
        session.getUserProperties().put("nick", nick);
        try {
            session.getBasicRemote().sendText("Connection Established, room " + roomid + ", user " + nick);
        } catch (IOException ex) {
        }
        SessionHandler.addSession(session);
    }

    /**
     * When a user sends a message to the server, this method will intercept the message and allow us to react to it. For now the message is read as a String.
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        SessionHandler.sendToAllConnectedSessionsInRoom(session.getUserProperties().get("roomid").toString(), message, false);
    }

    /**
     * The user closes the connection.
     *
     * Note: you can't send messages to the client from this method
     *
     * @param session
     */
    @OnClose
    public void onClose(Session session) {
        SessionHandler.removeSession(session);
        System.out.println("Session " + session.getId() + " has ended");
    }
}

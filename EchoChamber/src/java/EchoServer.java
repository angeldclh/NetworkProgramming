/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.util.ArrayList;

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
     * @throws java.io.IOException
     * @OnOpen allows us to intercept the creation of a new session. The session class allows us to send data to the user. In the method onOpen, we'll let the user know that the handshake was successful.
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("roomid") final String roomid, @PathParam("nick") final String nick) throws IOException {

        session.getUserProperties().put("roomid", roomid);
        session.getUserProperties().put("nick", nick);
        System.out.println(session.getId() + " has opened a connection");

        session.getBasicRemote().sendText("Connection Established, room " + roomid + ", user " + nick);

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
        String roomID = session.getUserProperties().get("roomid").toString();
        String user = session.getUserProperties().get("nick").toString();

        // Messages informing about changes on text input (user is / has stopped writing)
        if (message.startsWith("writing")) { //The sending session is writing
            ArrayList<String> p = SessionHandler.users.get(roomID);
            p.remove(user);
            p.add(0, user + " (writing)");
            //Send the updated user list
            SessionHandler.sendToAllConnectedSessionsInRoom(roomID, "Users in this room: " + SessionHandler.users.get(roomID).toString(), true);
        } else if (message.startsWith("not writing")) { //The sending session has stopped writing
            ArrayList<String> p = SessionHandler.users.get(roomID); // pointer
            p.remove(user + " (writing)");
            p.add(user);
            //Send the updated user list
            SessionHandler.sendToAllConnectedSessionsInRoom(roomID, "Users in this room: " + SessionHandler.users.get(roomID).toString(), true);
        } else {
            // Normal messages: just send them to the connected sessions
            SessionHandler.sendToAllConnectedSessionsInRoom(roomID, message, false);
        }
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

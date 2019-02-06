
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.websocket.Session;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author angel
 */
public class SessionHandler {

    private static final Set<Session> sessions = new HashSet<Session>();
    // "Fake database" containing lists of messages with their room ID as key. A real DB would be a way better solution
    private static final HashMap<String, ArrayList<String>> messages = new HashMap<String, ArrayList<String>>();
    //Users in each room
    private static final HashMap<String, ArrayList<String>> users = new HashMap<String, ArrayList<String>>();

    public static void addSession(Session session) {
        String roomID = (String) session.getUserProperties().get("roomid");
        String username = (String) session.getUserProperties().get("nick");

        //Add user to the list
        if (users.get(roomID) == null) { //First user in the room
            users.put(roomID, new ArrayList<>());
        }
        ArrayList<String> aux = users.get(roomID);
        aux.add(username);
        users.put(roomID, aux);

        //Add key with the room to hashmap if it doesn't exist       
        if (messages.get(roomID) == null) {
            messages.put(roomID, new ArrayList<>());
        } else {
            //Inform other users in the room about this event
            sendToAllConnectedSessionsInRoom(roomID, "INFO: " + username + " has joined the room", true);
        }

        //Actually join the room
        SessionHandler.sessions.add(session);
        //Send the updated user list
        sendToAllConnectedSessionsInRoom(roomID, "Users in this room: " + users.get(roomID).toString(), true);

        //Provide the complete history of messages since the room opening      
        messages.get(roomID).forEach((msg) -> {
            sendToSession(session, msg);
        });
    }

    public static void removeSession(Session session) {
        String roomID = (String) session.getUserProperties().get("roomid");
        String username = (String) session.getUserProperties().get("nick");

        SessionHandler.sessions.remove(session);
        users.get(roomID).remove(username);

        // Delete chat history of room if this was the last user
        if (users.get(roomID).isEmpty()) {
            messages.remove(roomID);
            users.remove(roomID);
        } else {
            // Inform other users in the room about this event
            sendToAllConnectedSessionsInRoom(roomID, "INFO: " + session.getUserProperties().get("nick") + " has left the room", true);
            //Send the updated user list
            sendToAllConnectedSessionsInRoom(roomID, "Users in this room: " + users.get(roomID).toString(), true);
        }

    }

    public static void sendToSession(Session session, String message) {
        System.out.println("Message from " + session.getId() + ": " + message);
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
        }
    }

    public static void sendToAllConnectedSessions(String message) {
        sessions.forEach((s) -> {
            sendToSession(s, message);
        });
    }

    public static void sendToAllConnectedSessionsInRoom(String roomID, String message, boolean infoMessage) {
        for (Session s : sessions) {
            if (s.getUserProperties().get("roomid").equals(roomID)) {
                sendToSession(s, message);
                // Insert message to "fake DB" if the message is not informative (INFO or Users in...)
                if (!infoMessage) {
                    addMessageToRoomHistory(roomID, message);
                }
            }
        }
    }

    // Inserts a message in the List that contains the history of the specified room
    private static void addMessageToRoomHistory(String roomID, String message) {
        ArrayList<String> aux = messages.get(roomID);
        aux.add(message);
        messages.put(roomID, aux);
    }

}

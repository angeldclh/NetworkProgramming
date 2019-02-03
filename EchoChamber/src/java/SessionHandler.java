
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

    public static void addSession(Session session) {
        SessionHandler.sessions.add(session);
        //Provide the complete history of messages since the room opening
        /*     String roomID = (String) session.getUserProperties().get("roomid");
        messages.get(roomID).forEach((msg) -> {
            sendToSession(session, msg);
        });*/
    }

    public static void removeSession(Session session) {
        SessionHandler.sessions.remove(session);

    }

    public static void sendToSession(Session session, String message) {
        System.out.println("Message from " + session.getId() + ": " + message);
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void sendToAllConnectedSessions(String message) {
        sessions.forEach((s) -> {
            sendToSession(s, message);
        });
    }

    public static void sendToAllConnectedSessionsInRoom(String roomID, String message) {
        for (Session s : sessions) {
            if (s.getUserProperties().get("roomid").equals(roomID)) {
                sendToSession(s, message);
                // Insert message to "fake DB"
                addMessageToRoomHistory(roomID, message);
            }
        }
    }

    // Inserts a message in the List containg the history of the specified room
    private static void addMessageToRoomHistory(String roomID, String message) {
        ArrayList<String> aux = messages.get(roomID);
        aux.add(message);
        messages.put(roomID, aux);
    }
}

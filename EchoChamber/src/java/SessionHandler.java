
import java.io.IOException;
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

    public static void addSession(Session session) {
        SessionHandler.sessions.add(session);
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
}

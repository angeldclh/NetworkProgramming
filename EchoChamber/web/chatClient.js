
var webSocket;
var messages = document.getElementById("messages");
var nick;

// Set default nick (userx) and room (1)
document.getElementById("nick").value = "user" + (Math.floor(Math.random() * 99) + 1);
document.getElementById("roomid").value = 1;

function openSocket() {
    // Ensures only one connection is open at a time
    if (webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED) {
        writeResponse("WebSocket is already opened.");
        return;
    }

    // Get the nick (it might have been changed by the user)
    nick = document.getElementById("nick").value.toString();

    // Get the room id
    var roomid = document.getElementById("roomid").value.toString();

    // Create a new instance of the websocket
    webSocket = new WebSocket("ws://localhost:8080/EchoChamber/echo/rooms/" + roomid + "/user/" + nick);

    /**
     * Binds functions to the listeners for the websocket.
     */
    webSocket.onopen = function (event) {
        // For reasons I can't determine, onopen gets called twice
        // and the first time event.data is undefined.
        // Leave a comment if you know the answer.
        if (event.data === undefined)
            return;

        writeResponse(event.data);
    };

    webSocket.onmessage = function (event) {
        var msg = event.data.toLocaleString();

        //If the message is the userlist, update it in the frontend, if not, show it
        if (msg.startsWith("Users in this room:")) {
            document.getElementById("users").innerHTML = msg;
        } else {
            writeResponse(msg);
        }
    };

    webSocket.onclose = function (event) {
        writeResponse("Connection closed");
        document.getElementById("users").innerHTML = "";
    };
}

/**
 * Sends the value of the text input to the server
 */
function send() {
    //Don't send empty messages
    if (document.getElementById("messageinput").value !== "") {
        var text = nick + ": " + document.getElementById("messageinput").value;
        webSocket.send(text);
        document.getElementById("messageinput").value = ""; //Clear message input
    }
}

function closeSocket() {
    webSocket.close();
}

function writeResponse(text) {
    messages.innerHTML += "<br/>" + text;
}

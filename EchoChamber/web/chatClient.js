
/* global WebSocket, isWriting */

var webSocket;
var messages = document.getElementById("messages");
var nick;
var msginput = document.getElementById("messageinput");
var writing = false;

// Handler for message input change (feature is writing)
msginput.oninput = isWriting;
msginput.onchange = stoppedWriting;

// Set default nick (userx) and room (1)
document.getElementById("nick").value = "user" + (Math.floor(Math.random() * 99) + 1);
document.getElementById("roomid").value = 1;

function openSocket() {
    // Ensures only one connection is open at a time
    if (webSocket !== undefined && webSocket.readyState !== WebSocket.CLOSED) {
        writeResponse("WebSocket is already opened.");
        return;
    }

    // Get the nick (it might have been changed by the user) and disable changes to it
    nick = document.getElementById("nick").value.toString();
    if (nick.startsWith("INFO") || nick.startsWith("Users") || nick.startsWith("writing") || nick.startsWith("not writing")) {
        writeResponse("Invalid nick. Please, choose another one.");
        return;
    }
    document.getElementById("nick").readOnly = true;

    // Get the room id and disable changes to it
    var roomid = document.getElementById("roomid").value.toString();
    document.getElementById("roomid").readOnly = true;

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

        //If the message is the userlist, update it, if not, show the message
        if (msg.startsWith("Users in this room:")) {
            document.getElementById("users").innerHTML = msg;
        } else {
            writeResponse(msg);
        }
    };

    webSocket.onclose = function (event) {
        writeResponse("Connection closed");
        document.getElementById("users").innerHTML = "";
        document.getElementById("nick").readOnly = false;
        document.getElementById("roomid").readOnly = false;
        document.getElementById("messageinput").value = ""; //Clear message input
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

function isWriting() { //TODO
    if (!writing) {// Notify only the 1st letter
        //messages.innerHTML += "<br/>" + "writing...";
        writing = true;
        webSocket.send("writing"); //Notify the server
    }
}

function stoppedWriting() {
    //messages.innerHTML += "<br/>" + "stopped writing...";
    writing = false;
    webSocket.send("not writing"); //Notify the server
}

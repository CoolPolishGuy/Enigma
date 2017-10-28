package com.loanlassalle.enigma.client.controllers;

import com.loanlassalle.enigma.client.models.Room;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Hashtable;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class contains automated tests to validate the client
 *
 * @author Tano Iannetta
 */
class ClientTest {

    private Client client = Client.getInstance();

    @BeforeEach
    void setUp() {
        try {
            client.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterEach
    void tearDown() {
        try {
            client.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void connect() {
        assertTrue(client.isConnected());
    }

    @Test
    void disconnect() {
        try {
            client.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertFalse(client.isConnected());
    }

    @Test
    void isConnected() {
        assertTrue(client.isConnected());
        try {
            client.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertFalse(client.isConnected());
    }

    @Test
    void receive() {
        try {
            client.send(Protocol.HELP);
            String message = client.receive();
            assertEquals(Protocol.HELP, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void send() {
        try {
            client.send(Protocol.HELP);
            String message = client.receive();
            assertTrue(Protocol.HELP.equals(message));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * we need to be the first and only client to interact
     * with the server to test this method
     * The sever needs to be clean either without any player in it
     */
    @Test
    void getRooms() {
        try {


            Hashtable<Integer, Room> roomList = client.getRooms();
            Room room = roomList.get(1);
            assertTrue(room.getIdRoom() >= 1);
            assertTrue(room.getPlayerList().size() == 0);
            assertEquals("vRoom_1", room.getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * we need to be the first  client and the only one called Hulk to interact
     * with the server to test this method
     * this method will only succeed on the first try
     */
    @Test
    void connectionToRoom() {


        try {
            boolean b = client.connectionToRoom("Hulk", 1);
            assertTrue(b);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * To test this method we send a message in the chat when we are not in a room
     * The sever will notice us that our message was not sent
     * That mean the send to the chat was understood by the server
     */
    @Test
    void sendToChat() {

        try {
            client.sendToChat("Test");
            String message = client.receive();
            if (message.equals(Protocol.CHAT)) {
                message = client.receive();
                assertTrue(message.equals(Protocol.NOT_SENT));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
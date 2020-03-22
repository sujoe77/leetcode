package com.pineapple.java.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketClientExample {

    public static final int SLEEP_INTERVAL = 5000;

    public void startClient(String host, int port) throws IOException, InterruptedException {
        SocketChannel channel = createChannel(host, port);

        sendMessages(getMessages(), channel);

        channel.close();
    }

    private SocketChannel createChannel(String host, int port) throws IOException {
        System.out.println("Client... started");
        return SocketChannel.open(new InetSocketAddress(host, port));
    }

    private String[] getMessages() {
        String threadName = Thread.currentThread().getName();

        // Send messages to server
        return new String[]{threadName + ": test1", threadName + ": test2", threadName + ": test3"};
    }

    private void sendMessages(String[] messages, SocketChannel channel) throws IOException, InterruptedException {
        for (String message : messages) {
            sendMessage(channel, message);
            Thread.sleep(SLEEP_INTERVAL);
        }
    }

    private void sendMessage(SocketChannel channel, String message) throws IOException {
        ByteBuffer buffer = createBuffer(message);
        channel.write(buffer);
        System.out.println(message);
        buffer.clear();
    }

    private ByteBuffer createBuffer(String message) {
        byte[] bytes = message.getBytes();
        return ByteBuffer.wrap(bytes);
    }
}


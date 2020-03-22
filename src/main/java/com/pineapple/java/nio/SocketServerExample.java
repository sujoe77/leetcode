package com.pineapple.java.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

public class SocketServerExample {
    public static final int PORT = 8090;
    public static final String HOST = "localhost";
    public static final int BUFFER_SIZE = 1024;

    private Selector selector;
    private Map<SocketChannel, List<byte[]>> dataMapper;
    private InetSocketAddress listenAddress;

    public static void main(String[] args) throws Exception {
        Runnable server = new Runnable() {
            @Override
            public void run() {
                try {
                    new SocketServerExample(HOST, PORT).startServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        };

        Runnable client = () -> {
            try {
                new SocketClientExample().startClient("localhost", 8090);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        };
        new Thread(server).start();
        new Thread(client, "client-A").start();
        new Thread(client, "client-B").start();
    }

    public SocketServerExample(String address, int port) throws IOException {
        listenAddress = new InetSocketAddress(address, port);
        dataMapper = new HashMap<>();
    }

    // create server channel	
    private void startServer() throws IOException {
        selector = Selector.open();
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        // retrieve server socket and bind to port
        serverChannel.socket().bind(listenAddress);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("Server started...");

        while (true) {
            // wait for events
            selector.select();

            //work on selected keys
            Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();

                // this is necessary to prevent the same key from coming up 
                // again the next time around.
                keys.remove();

                if (!key.isValid()) {
                    continue;
                }

                if (key.isAcceptable()) {
                    accept(key);
                } else if (key.isReadable()) {
                    read(key);
                }
            }
        }
    }

    //accept a connection made to this channel's socket
    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        Socket socket = channel.socket();
        SocketAddress remoteAddr = socket.getRemoteSocketAddress();
        System.out.println("Connected to: " + remoteAddr);

        // register channel with selector for further IO
        channel.register(selector, SelectionKey.OP_READ);
        dataMapper.put(channel, new ArrayList<>());
    }

    //read from the socket channel
    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        int numRead = channel.read(buffer);

        if (numRead == -1) {
            closeSocket(key, channel);
            return;
        }

        System.out.println("Got: " + readBuffer(buffer, numRead));
    }

    private void closeSocket(SelectionKey key, SocketChannel channel) throws IOException {
        SocketAddress remoteAddr = channel.socket().getRemoteSocketAddress();
        System.out.println("Connection closed by client: " + remoteAddr);
        channel.close();
        key.cancel();
        dataMapper.remove(channel);
        return;
    }

    private String readBuffer(ByteBuffer buffer, int numRead) {
        byte[] data = new byte[numRead];
        System.arraycopy(buffer.array(), 0, data, 0, numRead);
        return new String(data);
    }
}
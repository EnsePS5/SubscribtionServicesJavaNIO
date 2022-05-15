package tpo4_gk_s23161.tpo4_gk_s23161;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class ClientHandler {

    private static final String SERVER_TO_CONNECT_IP = "localhost";
    private static final int SERVER_TO_CONNECT_PORT = 9999;
    private static final Charset charset = Charset.forName("ISO-8859-2");

    private static CharBuffer charBuffer = null;

    private static SocketChannel client;
    private static ByteBuffer buffer;
    private static ClientHandler instance;

    public static ClientHandler start() {
        if (instance == null)
            instance = new ClientHandler();

        return instance;
    }

    public static void stop() throws IOException {
        instance.sendMessage("CLOSE:");
        client.close();
        buffer = null;
    }

    private ClientHandler() {
        try {
            client = SocketChannel.open(new InetSocketAddress(SERVER_TO_CONNECT_IP,SERVER_TO_CONNECT_PORT));
            buffer = ByteBuffer.allocateDirect(256);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String sendMessage(String msg) {
        buffer = ByteBuffer.wrap(msg.getBytes());
        String response = null;

        try {
            client.write(buffer);
            buffer.clear();
            buffer.flip();

            ByteBuffer outBuf = ByteBuffer.allocate(256);
            client.read(outBuf);
            response = new String(outBuf.array()).trim();
            System.out.println("response=" + response);

            buffer.clear();
            outBuf.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}

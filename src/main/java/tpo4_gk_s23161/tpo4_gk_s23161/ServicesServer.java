package tpo4_gk_s23161.tpo4_gk_s23161;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.*;


public class ServicesServer {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 9999;

    private static final Charset charset = Charset.forName("ISO-8859-2");

    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(SERVER_IP, SERVER_PORT));
        serverSocket.configureBlocking(false);
        serverSocket.register(selector, SelectionKey.OP_ACCEPT);
        ByteBuffer buffer = ByteBuffer.allocate(256);

        ArrayList<String> services = new ArrayList<>();
        Map <String,String> servicesToNotifications = new HashMap<>();

        while (true) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iter = selectedKeys.iterator();

            String newServiceToAdd;

            while (iter.hasNext()) {

                SelectionKey key = iter.next();

                if (key.isAcceptable()) {
                    register(selector, serverSocket);
                }

                if (key.isReadable()) {
                    newServiceToAdd = answerWithEcho(buffer, key ,services, servicesToNotifications);

                    if (!newServiceToAdd.isEmpty()) {
                        String[] serviceToDo = newServiceToAdd.split(":");

                        if (serviceToDo[0].equals("ADD"))
                            services.add(serviceToDo[1]);

                        else if (serviceToDo[0].equals("REMOVE"))
                            services.remove(serviceToDo[1]);

                        else if (serviceToDo[0].equals("NOTIFY")){
                            if(!servicesToNotifications.containsKey(serviceToDo[1])) {
                                servicesToNotifications.put(serviceToDo[1], serviceToDo[2]);
                            }else {
                                servicesToNotifications.replace(serviceToDo[1], serviceToDo[2]);
                            }
                        }
                    }

                }
                iter.remove();
            }
        }
    }

    private static String answerWithEcho(ByteBuffer buffer, SelectionKey key, ArrayList<String> services, Map<String,String> notifications)
            throws IOException {

        SocketChannel client = (SocketChannel) key.channel();
        client.read(buffer);

        ArrayList<String> commands = new ArrayList<>
                (List.of(new String(buffer.array(), buffer.arrayOffset(), buffer.arrayOffset()+buffer.position()).trim().split(":")));
        StringBuilder serviceResult = new StringBuilder();
        StringBuilder response = new StringBuilder();

        buffer.clear();

        System.out.println("komenda - " + commands);
        switch (commands.get(0)) {
            case "INIT", "UPDATE" -> {

                if (services.size() == 0)
                    response.append("NONE");

                for (String s : services)
                    response.append(s).append(":");

                if (commands.size() != 1) {
                    if (notifications.containsKey(commands.get(1)))
                        response.append(notifications.get(commands.get(1)));
                }

            }
            case "ADD" -> {

                if (!services.contains(commands.get(1))) {
                    serviceResult.append("ADD:").append(commands.get(1));
                    response.append("ADDED");
                }else
                    response.append("REJECTED");
            }
            case "REMOVE" -> {

                serviceResult.append("REMOVE:").append(commands.get(1));
                response.append("REMOVED");
            }
            case "NOTIFY" -> {

                serviceResult.append("NOTIFY:").append(commands.get(1)).append(":").append(commands.get(2));
                response.append("NOTIFIED");
            }
            case "GET_NOTIFICATION" -> {

                response.append(notifications.getOrDefault(commands.get(1), "NONE"));
            }
            case "CLOSE" -> {

                client.close();
                client.socket().close();
                return "CLOSED";
            }
        }
        System.out.println(response + " ser " + services);
        ByteBuffer outBuf = ByteBuffer.wrap(response.toString().getBytes());
        client.write(outBuf);

        return serviceResult.toString();
    }

    private static void register(Selector selector, ServerSocketChannel serverSocket)
            throws IOException {

        SocketChannel client = serverSocket.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
    }
}
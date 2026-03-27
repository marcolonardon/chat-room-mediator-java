package unaerp.br;

import unaerp.br.client.ChatClient;
import unaerp.br.server.ChatServer;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
            return;
        }

        switch (args[0]) {
            case "--server" -> {
                int port = args.length > 1 ? Integer.parseInt(args[1]) : 9090;
                ChatServer server = new ChatServer(port);
                server.start();
                Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
                server.awaitTermination();
            }
            case "--client" -> {
                if (args.length < 4) {
                    System.err.println("Uso: --client <host:port> <username> <sala>");
                    return;
                }
                String[] hostPort = args[1].split(":");
                String host = hostPort[0];
                int port = Integer.parseInt(hostPort[1]);
                String username = args[2];
                String room = args[3];

                ChatClient client = new ChatClient(host, port);
                Runtime.getRuntime().addShutdownHook(new Thread(client::close));
                client.connect(username, room);
            }
            default -> printUsage();
        }
    }

    private static void printUsage() {
        System.out.println("Uso:");
        System.out.println("  Servidor: --server [porta]              (padrão: 9090)");
        System.out.println("  Cliente:  --client <host:porta> <username> <sala>");
    }
}

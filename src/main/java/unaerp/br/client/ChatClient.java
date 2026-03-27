package unaerp.br.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import unaerp.br.proto.ChatMessage;
import unaerp.br.proto.ChatServiceGrpc;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ChatClient {

    private final ManagedChannel channel;
    private final ChatServiceGrpc.ChatServiceStub stub;
    private StreamObserver<ChatMessage> requestObserver;
    private final CountDownLatch doneLatch = new CountDownLatch(1);

    public ChatClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        stub = ChatServiceGrpc.newStub(channel);
    }

    public void connect(String username, String room) {
        requestObserver = stub.chat(new StreamObserver<>() {
            @Override
            public void onNext(ChatMessage message) {
                System.out.printf("[%s] %s: %s%n",
                        message.getRoom(), message.getUsername(), message.getContent());
            }

            @Override
            public void onError(Throwable t) {
                System.err.println("Erro na conexão: " + t.getMessage());
                doneLatch.countDown();
            }

            @Override
            public void onCompleted() {
                doneLatch.countDown();
            }
        });

        // First message registers username and room on the server
        sendMessage(username, room, "");
        System.out.printf("Conectado como '%s' na sala '%s'. Digite /exit para sair.%n", username, room);

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("/exit")) {
                break;
            }
            if (!line.isEmpty()) {
                sendMessage(username, room, line);
            }
        }

        close();
    }

    private void sendMessage(String username, String room, String content) {
        requestObserver.onNext(ChatMessage.newBuilder()
                .setUsername(username)
                .setRoom(room)
                .setContent(content)
                .build());
    }

    public void close() {
        requestObserver.onCompleted();
        try {
            doneLatch.await(5, TimeUnit.SECONDS);
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

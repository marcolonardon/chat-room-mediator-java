package unaerp.br.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import unaerp.br.mediator.ChatMediator;
import unaerp.br.mediator.ChatRoomMediator;
import unaerp.br.proto.ChatMessage;
import unaerp.br.proto.ChatServiceGrpc;
import unaerp.br.user.RemoteChatUser;

import java.io.IOException;

public class ChatServer {

    private final int port;
    private Server server;
    private final ChatMediator mediator = new ChatRoomMediator();

    public ChatServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        server = ServerBuilder.forPort(port)
                .addService(new ChatServiceImpl(mediator))
                .build()
                .start();
        System.out.printf("Servidor iniciado na porta %d.%n", port);
    }

    public void awaitTermination() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    static class ChatServiceImpl extends ChatServiceGrpc.ChatServiceImplBase {

        private final ChatMediator mediator;

        ChatServiceImpl(ChatMediator mediator) {
            this.mediator = mediator;
        }

        @Override
        public StreamObserver<ChatMessage> chat(StreamObserver<ChatMessage> responseObserver) {
            return new StreamObserver<>() {
                RemoteChatUser user;

                @Override
                public void onNext(ChatMessage message) {
                    if (user == null) {
                        user = new RemoteChatUser(
                                message.getUsername(),
                                message.getRoom(),
                                mediator,
                                responseObserver
                        );
                        mediator.addUser(user);
                    }
                    mediator.sendMessage(message.getContent(), user);
                }

                @Override
                public void onError(Throwable t) {
                    if (user != null) {
                        mediator.removeUser(user);
                    }
                }

                @Override
                public void onCompleted() {
                    if (user != null) {
                        mediator.removeUser(user);
                    }
                    responseObserver.onCompleted();
                }
            };
        }
    }
}

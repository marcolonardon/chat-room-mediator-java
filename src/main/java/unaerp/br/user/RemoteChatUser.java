package unaerp.br.user;

import io.grpc.stub.StreamObserver;
import unaerp.br.mediator.ChatMediator;
import unaerp.br.proto.ChatMessage;

public class RemoteChatUser extends ChatUser {

    private final StreamObserver<ChatMessage> responseObserver;

    public RemoteChatUser(String username, String room,
                          ChatMediator mediator,
                          StreamObserver<ChatMessage> responseObserver) {
        super(username, room, mediator);
        this.responseObserver = responseObserver;
    }

    @Override
    public void receive(String content, String fromUsername) {
        ChatMessage message = ChatMessage.newBuilder()
                .setUsername(fromUsername)
                .setRoom(room)
                .setContent(content)
                .build();
        responseObserver.onNext(message);
    }
}

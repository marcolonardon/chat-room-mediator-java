package unaerp.br.user;

import unaerp.br.mediator.ChatMediator;

public abstract class ChatUser {

    protected final String username;
    protected final String room;
    protected final ChatMediator mediator;

    protected ChatUser(String username, String room, ChatMediator mediator) {
        this.username = username;
        this.room = room;
        this.mediator = mediator;
    }

    public String getUsername() {
        return username;
    }

    public String getRoom() {
        return room;
    }

    public void send(String content) {
        mediator.sendMessage(content, this);
    }

    public abstract void receive(String content, String fromUsername);
}

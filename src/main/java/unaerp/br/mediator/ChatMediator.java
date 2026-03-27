package unaerp.br.mediator;

import unaerp.br.user.ChatUser;

public interface ChatMediator {

    void addUser(ChatUser user);

    void removeUser(ChatUser user);

    void sendMessage(String content, ChatUser sender);
}

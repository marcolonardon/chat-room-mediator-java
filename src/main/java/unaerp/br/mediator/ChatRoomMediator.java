package unaerp.br.mediator;

import unaerp.br.user.ChatUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatRoomMediator implements ChatMediator {

    private final Map<String, List<ChatUser>> rooms = new ConcurrentHashMap<>();

    @Override
    public void addUser(ChatUser user) {
        rooms.computeIfAbsent(user.getRoom(), k -> new CopyOnWriteArrayList<>()).add(user);
        System.out.printf("[%s] %s entrou na sala.%n", user.getRoom(), user.getUsername());
    }

    @Override
    public void removeUser(ChatUser user) {
        List<ChatUser> users = rooms.get(user.getRoom());
        if (users != null) {
            users.remove(user);
            System.out.printf("[%s] %s saiu da sala.%n", user.getRoom(), user.getUsername());
        }
    }

    @Override
    public void sendMessage(String content, ChatUser sender) {
        List<ChatUser> users = rooms.getOrDefault(sender.getRoom(), new ArrayList<>());
        for (ChatUser user : users) {
            if (!user.getUsername().equals(sender.getUsername())) {
                user.receive(content, sender.getUsername());
            }
        }
    }
}

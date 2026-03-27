<h1 align="center">💬 Chat Room Mediator</h1>

<p align='center'>
  <img alt='Java' src='https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white' />
  <img alt='gRPC' src='https://img.shields.io/badge/gRPC-4285F4?style=for-the-badge&logo=google&logoColor=white' />
  <img alt='Protocol Buffers' src='https://img.shields.io/badge/Protobuf-FF6F00?style=for-the-badge&logo=google&logoColor=white' />
  <img alt='Maven' src='https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white' />
</p>

## 💻 Sobre o Projeto

**Chat Room Mediator** é um chat de terminal distribuído construído em Java, utilizando **gRPC** para comunicação entre processos e o **padrão de projeto Mediator** para desacoplar os participantes da conversa.

Cada cliente se conecta a um servidor central que age como mediador — os usuários nunca se comunicam diretamente entre si, apenas através do `ChatRoomMediator`, que gerencia o roteamento das mensagens para os participantes corretos de cada sala.

## 🏗️ Arquitetura

```
unaerp.br/
├── Main.java                  # Entry point: modo servidor ou cliente
├── mediator/
│   ├── ChatMediator.java      # Interface do mediador
│   └── ChatRoomMediator.java  # Mediador concreto (broadcast por sala)
├── user/
│   ├── ChatUser.java          # Classe abstrata do participante
│   └── RemoteChatUser.java    # Participante remoto via stream gRPC
├── server/
│   └── ChatServer.java        # Servidor gRPC
└── client/
    └── ChatClient.java        # Cliente de terminal
```

### Fluxo de Mensagens

```
[Alice] ──send──▶ ChatClient ──gRPC stream──▶ ChatServer
                                                   │
                                          ChatRoomMediator
                                                   │
                              ┌────────────────────┘
                              ▼
                    [Bob] ◀── ChatClient ◀── gRPC stream
```

### ✨ Funcionalidades

- 🏠 **Salas independentes**: múltiplas salas simultâneas no mesmo servidor
- 👥 **Múltiplos clientes**: várias instâncias de cliente na mesma sala
- 📡 **Streaming bidirecional**: comunicação em tempo real via gRPC
- 🔌 **Desconexão limpa**: remoção automática do usuário ao sair com `/exit`

## 📦 Pré-requisitos

- **Java 23+**
- **Maven 3.8+**

## 🚀 Como Executar

### Build

```bash
mvn package -q
```

### Servidor

```bash
java --sun-misc-unsafe-memory-access=allow \
     -jar target/chat-room-mediator-java-1.0-SNAPSHOT.jar \
     --server 9090
```

### Cliente

```bash
java --sun-misc-unsafe-memory-access=allow \
     -jar target/chat-room-mediator-java-1.0-SNAPSHOT.jar \
     --client localhost:9090 <username> <sala>
```

### Exemplo com dois clientes

```bash
# Terminal 1 — servidor
java --sun-misc-unsafe-memory-access=allow -jar target/*.jar --server 9090

# Terminal 2 — Alice
java --sun-misc-unsafe-memory-access=allow -jar target/*.jar --client localhost:9090 Alice geral

# Terminal 3 — Bob
java --sun-misc-unsafe-memory-access=allow -jar target/*.jar --client localhost:9090 Bob geral
```

> **IntelliJ**: as configurações **Server** e **Client** já estão incluídas em `.idea/runConfigurations/` com todos os parâmetros JVM configurados.

## 🎨 Padrão Mediator

| Componente | Papel no padrão |
|---|---|
| `ChatMediator` | Interface Mediator |
| `ChatRoomMediator` | Mediator Concreto |
| `ChatUser` | Colleague abstrato |
| `RemoteChatUser` | Colleague Concreto |

## 📄 Licença

Este projeto está sob a licença MIT.

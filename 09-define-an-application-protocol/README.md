# Define an application protocol

This directory contains **one possible solution** to the
[Define an application protocol](https://github.com/heig-vd-dai-course/heig-vd-dai-course/tree/main/09-define-an-application-protocol)
practical content.

## SMS Protocol

### Section 1 - Overview

The SMS (Short Message Service) protocol is a communication protocol that allows
the sending of text messages (generally short) between users.

### Section 2 - Transport protocol

The SMS protocol is a text message transport protocol. It must use the TCP
(Transmission Control Protocol) to ensure the reliability of data transmission
and must also use port 1234.

The initial connection must be established by the client. The server can refuse
the connection if the maximum number of connections is reached. The maximum
number of connections per server is 5. Beyond this number, the server refuses
the connection.

Once the connection is established, the client can send a text message to the
server indicating the message recipient.

The server must verify that the recipient is connected and that the message does
not exceed 100 characters. If these conditions are met, the server sends the
message to the recipient. Otherwise, the server sends an error message to the
client who sent the message.

### Section 3 - Messages

The messages sent by the client and the server are text messages. The messages
sent by the client are in the following form:

#### Connexion

##### Request

```
CONNECT <name>
```

- `name`: the name of the client

##### Response

- `OK`: the connection has been successfully established
- `ERROR <code>`: an error occurred during the connection. The error code is an
  integer between 1 and 2 inclusive. The error codes are as follows:
  - 1: the maximum number of connections is reached
  - 2: the client's name is already in use

#### Sending a message

Le client envoie un message au serveur en indiquant le destinataire du message.
Le serveur est alors en charge de transmettre le message au destinataire.

##### Request

```
SEND <recipent> <message>
```

##### Response

- `OK`: the message has been successfully sent
- `ERROR <code>`: an error occurred while sending the message. The error code is
  an integer between 1 and 2 inclusive. The error codes are as follows:
  - 1: the recipient is not connected
  - 2: the message exceeds 100 characters

#### Receiving a message

The server sends a message to the recipient indicating the sender of the
message. The client is then responsible for displaying the received message.

##### Request

```
RECEIVE <message> <sender>
```

- `message`: the received message
- `sender`: the name of the message sender

##### Response

None.

#### List connected clients

##### Request

```
LIST
```

##### Response

- `CLIENTS <client1> <client2> <client3> ...`: the list of connected clients.
  The clients are separated by a space.

#### Disconnection

##### Request

```
QUIT
```

##### Response

None.

### Section 4 - Examples

#### Functional communication between a client and a server

![Functional communication between a client and a server](./images/example-1.png)

#### Communication between a client and a server with the maximum number of connections reached

![Communication between a client and a server with the maximum number of connections reached](./images/example-2.png)

#### Communication between a client and a server with a duplicate client name

![Communication between a client and a server with a duplicate client name](./images/example-3.png)

#### Communication between a client and a server with an unconnected recipient

![Communication between a client and a server with an unconnected recipient](./images/example-4.png)

#### Communication between a client and a server with a too long message

![Communication between a client and a server with a too long message](./images/example-5.png)

# Java UDP programming

This directory contains the solution to the
[Java UDP programming](https://github.com/heig-vd-dai-course/heig-vd-dai-course/tree/main/17-java-tcp-programming)
practical content.

## Run the emitters and receivers example

- What are the commands to start the unicast emitter and receiver?

  ```sh
  # Start the emitter
  java -jar target/java-udp-programming-1.0-SNAPSHOT.jar unicast-emitter -H 127.0.0.1 -p 9876

  # Start the receiver
  java -jar target/java-udp-programming-1.0-SNAPSHOT.jar unicast-receiver -p 9876
  ```

- What are the commands to start the broadcast emitter and receiver?

  ```sh
  # Start the emitter
  java -jar target/java-udp-programming-1.0-SNAPSHOT.jar broadcast-emitter -H 255.255.255.255 -p 9877

  # Start the receiver
  java -jar target/java-udp-programming-1.0-SNAPSHOT.jar broadcast-receiver -p 9877
  ```

- What are the commands to start the multicast emitter and receiver?

  > **Note**  
  > The `en0` interface is specific to my (Ludovic) machine running macOS. You
  > will have to replace it with the interface of your machine. You can find the
  > interfaces of your machine with the
  > `java -jar target/java-udp-programming-1.0-SNAPSHOT.jar list-network-interfaces`
  > command.

  ```sh
  # Start the emitter
  java -jar target/java-udp-programming-1.0-SNAPSHOT.jar multicast-emitter -H 239.1.1.1 -p 9878 -i en0

  # Start the receiver
  java -jar target/java-udp-programming-1.0-SNAPSHOT.jar multicast-receiver -i en0 -H 239.1.1.1 -p 9878
  ```

- What messages do the unicast receiver receives? Why?  
  The unicast receives the messages from the unicast emitter because they are on
  the same network and on their own port.
- What messages do the broadcast receiver receives? Why?  
  The broadcast receives the messages from the broadcast emitter because they
  are on the same network and on their own port.
- What messages do the multicast receiver receives? Why?  
  The multicast receives the messages from the multicast emitter because they
  are on the same network and on their own port.

## Dockerize the emitters and receivers example

- Take some time to understand this Dockerfile. Can you explain each line with
  your own words?

  ```dockerfile
  # The `FROM` instruction specifies the base image. You are extending the `eclipse-temurin:17` image.
  FROM eclipse-temurin:17

  # The `WORKDIR` instruction is a directory inside the container where the application will be copied.
  WORKDIR /app

  # The `COPY` instruction copies the application from the host to the container.
  COPY target/java-udp-programming-1.0-SNAPSHOT.jar /app/java-udp-programming-1.0-SNAPSHOT.jar

  # The `ENTRYPOINT` instruction specifies the command to run when the container starts.
  ENTRYPOINT ["java", "-jar", "java-udp-programming-1.0-SNAPSHOT.jar"]

  # The `CMD` instruction specifies the default arguments to the command.
  CMD ["--help"]
  ```

- Which command(s) did you use to build the Docker image?

  ```sh
  # Build the Docker image
  docker build -t java-udp-programming .
  ```

- Which command(s) did you use to run the Docker image?

  ```sh
  # Run the Docker image
  docker run --rm -it java-udp-programming --help
  ```

- Which command(s) did you use to publish the Docker image?

  > **Note**  
  > The following commands require you to be logged in to GitHub Container
  > Registry. The name of the image must be updated to match your GitHub
  > username.

  ```sh
  # Rename the image for GitHub Container Registry
  docker tag java-udp-programming docker pull ghcr.io/heig-vd-dai-course/java-udp-programming

  # Publish the image on GitHub Container Registry
  docker push ghcr.io/heig-vd-dai-course/java-udp-programming
  ```

- Which command(s) did you use to run the Docker Compose file?

  ```sh
  # Start the unicast emitter and receivers
  docker compose up unicast-emitter unicast-receiver-1 unicast-receiver-2

  # Start the broadcast emitter and receivers
  docker compose up broadcast-emitter broadcast-receiver-1 broadcast-receiver-2

  # Start the multicast emitter and receivers
  docker compose up multicast-emitter multicast-receiver-1 multicast-receiver-2 multicast-receiver-3

  # Start all the emitters and receivers
  docker compose up
  ```

- What messages do the unicast receivers receive? Why?  
  When all the emitters and receivers are started, the unicast receiver 1
  receives messages from the unicast emitter because the unicast emitter emits
  message to a specific host (`unicast-receiver-1`). The other unicast receiver
  2 does not receive any messages because the unicast emitter does not emit
  messages to its host.

  The unicast receivers also receive the messages from the broadcast emitters as
  they are on the same network and uses the same port.

- What messages do the broadcast receivers receive? Why?  
  When all the emitters and receivers are started, the broadcast receivers
  receive the messages from the broadcast emitter because they are on the same
  network and each container uses the same port.
- What messages do the multicast receivers receive? Why?  
  When all the emitters and receivers are started, the multicast receivers 1 and
  2 receive the messages from the multicast emitter because the multicast
  emitter emits message to a specific multicast address (`239.1.1.1`). Both
  `multicast-receiver-1` and `multicast-receiver-2` listen to this specific
  multicast address. `multicast-receiver-3` listens to the multicast address
  `239.1.1.2`.

  The multicast receivers also receive the messages from the broadcast emitters
  as they are on the same network and uses the same port.

- What are the differences between the outputs of the emitters and receivers
  when running them with Docker Compose and when running them manually?  
  Without using Docker Compose, it was not possible to run multiple emitters
  using the same port. With Docker Compose, it is possible to run multiple
  containers using the same port. This is because each container has its own IP,
  simulating a different machine on the network.

  As all the emitters and receivers are on the same network using the same port,
  they can all communicate with each other. This is not possible when running
  them manually.

  The broadcast emitter emits messages to the broadcast address
  `255.255.255.255`, thus flooding the network with messages. When running the
  other receivers, they all receive the messages from the broadcast emitter.

## Isolate broadcast emitters and receivers to their own network

- How and why did the network helped for the broadcast emitters and receivers?  
  As the broadcast emitter was flooding the network with messages, the other
  emitters and receivers were receiving the messages from the broadcast emitter.

  Isolating the broadcast emitters and receivers to their own network helped
  because the other emitters and receivers were not receiving the messages from
  the broadcast emitter anymore.

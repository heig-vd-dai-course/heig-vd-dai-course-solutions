# Java TCP programming

This directory contains the solution to the
[Java TCP programming](https://github.com/heig-vd-dai-course/heig-vd-dai-course/tree/main/13-java-tcp-programming)
practical content.

- [Discussion on implementations](#discussion-on-implementations)
  - [`TcpServerSimpleTextualExample`](#tcpserversimpletextualexample)
  - [`TcpServerSingleThreadTextualExample`](#tcpserversinglethreadtextualexample)
  - [`TcpServerMultiThreadTextualExample`](#tcpservermultithreadtextualexample)
  - [`TcpServerCachedThreadPoolTextualExample`](#tcpservercachedthreadpooltextualexample)
  - [`TcpServerFixedThreadPoolTextualExample`](#tcpserverfixedthreadpooltextualexample)
  - [`TcpServerVirtualThreadTextualExample`](#tcpservervirtualthreadtextualexample)
- [When to use each solution?](#when-to-use-each-solution)
- [Why does the `TcpServerSingleThreadTextualExample` not work as expected?](#why-does-the-tcpserversinglethreadtextualexample-not-work-as-expected)

## Discussion on implementations

### `TcpServerSimpleTextualExample`

This simple demonstration can only handle one client at a time. It is not very
useful but it is a good starting point to understand how the TCP protocol works.

### `TcpServerSingleThreadTextualExample`

This example is no more complex than the previous example as it can still only
handle one client at a time.

However, it uses the
[`newSingleThreadExecutor`](<https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/Executors.html#newSingleThreadExecutor()>)
method from the
[`Executors`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/Executors.html)
class.

This method returns an
[`ExecutorService`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/ExecutorService.html)
that uses a single worker thread operating off an unbounded queue.

It will easily allow to update the server to handle multiple clients at the same
time in the next examples.

### `TcpServerMultiThreadTextualExample`

This example can handle multiple clients with the help of
[Threads](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Thread.html).

Threads will be created infinitely to handle each client connection. This is not
a good solution as it will quickly consume all the resources of the server.

Threads are not cheap to create and use. They consume a lot of memory and CPU
time. It is better to use a thread pool to handle multiple clients.

### `TcpServerCachedThreadPoolTextualExample`

This example is very similar to the previous one but it uses the
[`newCachedThreadPool`](<https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/Executors.html#newCachedThreadPool()>)
method from the
[`Executors`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/Executors.html)
class.

This method returns an
[`ExecutorService`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/ExecutorService.html)
that creates new threads as needed, but will reuse previously constructed
threads when they are available.

It is a slightly better solution than the previous one but it is still not
optimal.

### `TcpServerFixedThreadPoolTextualExample`

This example is very similar to the previous one but it uses the
[`newFixedThreadPool`](<https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/Executors.html#newFixedThreadPool(int)>)
method from the
[`Executors`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/Executors.html)
class.

This method returns an
[`ExecutorService`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/ExecutorService.html)
that uses a fixed number of threads operating off a shared unbounded queue.

This solution allows a fine tuning of the number of threads used to handle the
clients. It is one of the best solutions to handle multiple clients at the same
time.

The difficulty is to find the right number of threads to use. Too few threads
will not allow to handle all the clients at the same time. Too many threads will
consume too much resources.

### `TcpServerVirtualThreadTextualExample`

This example uses the
[`newVirtualThreadExecutor`](<https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Executors.html#newVirtualThreadPerTaskExecutor()>)
method from the
[`Executors`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Executors.html)
class.

This method returns an
[`ExecutorService`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ExecutorService.html)
that creates new virtual threads as needed. Virtual threads are lightweight
threads that are scheduled by the Java virtual machine (JVM) rather than by the
operating system. This is only available since Java 21.

This solution is very similar to the previous one but it uses virtual threads
instead of native threads. It is one of the best solutions to handle multiple
clients at the same time as it is very lightweight.

## When to use each solution?

This question is a tricky one. It depends on the context and the use case.

The `TcpServerSimpleTextualExample` and `TcpServerSingleThreadTextualExample`
solutions are only useful to understand how the TCP protocol works. It is not
useful in a real world application.

The `TcpServerMultiThreadTextualExample` solution is a good solution if you have
a small number of clients to handle.

The `TcpServerCachedThreadPoolTextualExample` solution is a good solution if you
have a large number of clients to handle. It is less resource intensive than the
third solution but it is still not optimal.

The `TcpServerFixedThreadPoolTextualExample` and
`TcpServerVirtualThreadTextualExample` solutions are good solutions if you have
a large number of clients to handle and want to have a good control on
resources. They are the best solution to handle multiple clients at the same
time.

A rule of thumb is to use the `TcpServerFixedThreadPoolTextualExample` solution
if you are using Java 17 or less and the `TcpServerVirtualThreadTextualExample`
solution if you are using Java 21 or more.

## Why does the `TcpServerSingleThreadTextualExample` not work as expected?

This is because we use a Unicode character with an `US_ASCII` encoding.

This is why encoding and decoding charsets are important. If you use the wrong
encoding, you will not be able to decode the message correctly.

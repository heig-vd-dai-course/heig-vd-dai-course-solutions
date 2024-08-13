# Java IOs

This directory contains the solution to the
[Java IOs](https://github.com/heig-vd-dai-course/heig-vd-dai-course/tree/main/05-java-ios)
practical content.

## Benchmark results

### 1B

```sh
# Write a file with 1 byte
java -jar target/java-ios-1.0-SNAPSHOT.jar --implementation <IMPLEMENTATION> <FILENAME> write --size 1

# Read a file with 1 byte
java -jar target/java-ios-1.0-SNAPSHOT.jar --implementation <IMPLEMENTATION> <FILENAME> read
```

The following table shows the benchmark results for each stream type:

| Implementation  | Write (ns) | Read (ns) |
| --------------- | ---------: | --------: |
| BINARY          |   78806112 |  74519231 |
| BUFFERED_BINARY |   78026604 |  74540120 |
| Difference (%)  |     -0.99% |    0.028% |

| Implementation | Write (ns) | Read (ns) |
| -------------- | ---------: | --------: |
| TEXT           |   75153424 |  76508225 |
| BUFFERED_TEXT  |   78793047 |  77198235 |
| Difference (%) |      4.84% |     0.90% |

### 1KiB (= 1 \* 1024 = 1024 bytes)

```sh
# Write a file with 1KiB
java -jar target/java-ios-1.0-SNAPSHOT.jar --implementation <IMPLEMENTATION> <FILENAME> write --size 1024

# Read a file with 1KiB
java -jar target/java-ios-1.0-SNAPSHOT.jar --implementation <IMPLEMENTATION> <FILENAME> read
```

The following table shows the benchmark results for each stream type:

| Implementation  | Write (ns) | Read (ns) |
| --------------- | ---------: | --------: |
| BINARY          |   85902493 |  76335280 |
| BUFFERED_BINARY |   78404967 |  75777921 |
| Difference (%)  |     -8.73% |    -0.73% |

| Implementation | Write (ns) | Read (ns) |
| -------------- | ---------: | --------: |
| TEXT           |   82945996 |  78292204 |
| BUFFERED_TEXT  |   79184845 |  80430971 |
| Difference (%) |     -4.53% |     2.73% |

### 1MiB (= 1 _ 1024 _ 1024 = 1048576 bytes)

```sh
# Write a file with 1MiB
java -jar target/java-ios-1.0-SNAPSHOT.jar --implementation <IMPLEMENTATION> <FILENAME> write --size 1048576

# Read a file with 1MiB
java -jar target/java-ios-1.0-SNAPSHOT.jar --implementation <IMPLEMENTATION> <FILENAME> read
```

The following table shows the benchmark results for each stream type:

| Implementation  | Write (ns) | Read (ns) |
| --------------- | ---------: | --------: |
| BINARY          | 4078170944 | 593106126 |
| BUFFERED_BINARY |   91791850 |  89982995 |
| Difference (%)  |    -97.75% |   -84.84% |

| Implementation | Write (ns) | Read (ns) |
| -------------- | ---------: | --------: |
| TEXT           |  137818727 | 111980391 |
| BUFFERED_TEXT  |   94707961 |  93402523 |
| Difference (%) |    -31.21% |   -16.59% |

### 5MiB (= 5 _ 1024 _ 1024 = 5242880 bytes)

```sh
# Write a file with 5MiB
java -jar target/java-ios-1.0-SNAPSHOT.jar --implementation <IMPLEMENTATION> <FILENAME> write --size 5242880

# Read a file with 5MiB
java -jar target/java-ios-1.0-SNAPSHOT.jar --implementation <IMPLEMENTATION> <FILENAME> read
```

The following table shows the benchmark results for each stream type:

| Implementation  |  Write (ns) |  Read (ns) |
| --------------- | ----------: | ---------: |
| BINARY          | 21130569783 | 2683356509 |
| BUFFERED_BINARY |   111697528 |  106649073 |
| Difference (%)  |     -99.47% |    -96.03% |

| Implementation | Write (ns) | Read (ns) |
| -------------- | ---------: | --------: |
| TEXT           |  299200948 | 202032725 |
| BUFFERED_TEXT  |  123621367 | 109222378 |
| Difference (%) |    -58.67% |   -45.94% |

## Answers to the questions

### Which type of stream is the most efficient for each use case?

- For binary data, the most efficient stream is the `BufferedInputStream` and
  `BufferedOutputStream`.
- For text data, the most efficient stream is the `BufferedReader` and
  `BufferedWriter`.

### Why is it more efficient than the other types of streans?

- The `BufferedInputStream` and `BufferedOutputStream` use a buffer to read and
  write data. This buffer reduces the number of system calls, which is more
  efficient.

### What is the difference between binary data and text data?

The binary data is a sequence of bytes, while the text data is a sequence of
characters.

When dealing with text data, we need to consider the character encoding. The
character encoding is a mapping between characters and bytes. The most common
character encoding is UTF-8.

### What is a character encoding?

A character encoding is a mapping between characters and bytes. The character
encoding defines how characters are represented as bytes.

### Why is this methodology important?

> This methodology is important because it provides a structured and systematic
> approach to solving problens or conducting research, ensuring consistency,
> reliability, and the ability to replicate results.
>
> [apirakas](https://github.com/apirakas), in GitHub Discussion
> <https://github.com/orgs/heig-vd-dai-course/discussions/83>

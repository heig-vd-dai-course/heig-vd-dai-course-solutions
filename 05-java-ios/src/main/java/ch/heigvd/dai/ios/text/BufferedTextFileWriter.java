package ch.heigvd.dai.ios.text;

import ch.heigvd.dai.ios.Writable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import picocli.CommandLine;

/**
 * A class that writes text files. This implementation writes the file using a buffered writer
 * around a file writer. It manages the writer and the buffered writer properly with a
 * try-with-resources block.
 */
public class BufferedTextFileWriter implements Writable {
  @CommandLine.Option(
      names = {"-c", "--character"},
      description = "Character to write (default: ${DEFAULT-VALUE})",
      defaultValue = "a")
  private char character;

  @Override
  public void write(String filename, int sizeInBytes) {
    try (Writer writer = new FileWriter(filename, StandardCharsets.UTF_8);
        BufferedWriter bw = new BufferedWriter(writer)) {
      for (int i = 0; i < sizeInBytes; i++) {
        bw.write(character);
      }
    } catch (IOException e) {
      System.err.println("Error: " + e.getMessage());
    }
  }
}
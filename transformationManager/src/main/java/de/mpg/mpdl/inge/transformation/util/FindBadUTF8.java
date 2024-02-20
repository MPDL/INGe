package de.mpg.mpdl.inge.transformation.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;

public class FindBadUTF8 {
  private static final String URL =
      "C:\\Users\\haarlae1\\Documents\\Pubman\\Import files\\TestdatenBMC\\Testdaten Markus\\1752-1947-5-391.xml";

  private FindBadUTF8() {}

  public static void main(String[] argv) throws IOException {
    try (InputStream inStream = new FileInputStream(URL)) {
      CharsetDecoder d = StandardCharsets.UTF_8.newDecoder();
      CharBuffer out = CharBuffer.allocate(1);
      ByteBuffer in = ByteBuffer.allocate(10);
      in.clear();
      long offset = 0L;

      while (true) {
        int read = inStream.read();

        if (-1 != read) {
          in.put((byte) read);
        }

        out.clear();
        in.flip();
        CoderResult cr = d.decode(in, out, (-1 == read));

        if (cr.isError()) {
          if (-1 != read) {
            System.out.println("Error at offset " + offset + ": " + cr);
          } else {
            System.out.println("Error at end-of-file: " + cr);
          }
          return;
        }

        if (cr.isUnderflow()) {
          in.position(in.limit());
          in.limit(in.capacity());
        } else {
          in.clear();
        }

        if (-1 == read) {
          break;
        }

        offset += 1L;
      }

      System.out.println("OK");
    }
  }
}

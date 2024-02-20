package de.mpg.mpdl.inge.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ModelHelper {

  private ModelHelper() {}

  public static <T extends Serializable> T makeClone(T object) throws IOException, ClassNotFoundException {
    ByteArrayOutputStream outputStream = null;
    ByteArrayInputStream inputStream = null;

    try {
      outputStream = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(outputStream);
      out.writeObject(object);

      inputStream = new ByteArrayInputStream(outputStream.toByteArray());
      ObjectInputStream in = new ObjectInputStream(inputStream);
      T copied = (T) in.readObject();

      return copied;
    } finally {
      if (null != outputStream) {
        outputStream.close();
      }
      if (null != inputStream) {
        inputStream.close();
      }
    }
  }
}

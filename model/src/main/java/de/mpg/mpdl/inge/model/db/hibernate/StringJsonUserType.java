package de.mpg.mpdl.inge.model.db.hibernate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import com.fasterxml.jackson.databind.JavaType;

import de.mpg.mpdl.inge.model.json.util.JsonObjectMapperFactory;

public abstract class StringJsonUserType implements UserType {

  // final Class<ReturnType> typeParameterClass;

  final JavaType typeReference;

  public StringJsonUserType(JavaType javaType) {
    // this.typeParameterClass = typeParameterClass;
    this.typeReference = javaType;
  }

  @Override
  public int[] sqlTypes() {
    return new int[] {Types.JAVA_OBJECT};
  }

  @Override
  public Class<?> returnedClass() {
    return typeReference.getRawClass();
  }

  @Override
  public boolean equals(Object x, Object y) throws HibernateException {
    if (x == null) {

      return y == null;
    }

    return x.equals(y);
  }

  @Override
  public int hashCode(Object x) throws HibernateException {
    return x.hashCode();
  }

  @Override
  public Object nullSafeGet(ResultSet rs, String[] names, SharedSessionContractImplementor session, Object owner)
      throws HibernateException, SQLException {
    final String cellContent = rs.getString(names[0]);
    if (cellContent == null) {
      return null;
    }
    try {
      // long start = System.currentTimeMillis();
      Object retVal = JsonObjectMapperFactory.getObjectMapper().readerFor(typeReference).readValue(cellContent.getBytes("UTF-8"));

      // System.out.println("Conversion of metadata took " + (System.currentTimeMillis() - start));
      return retVal;
    } catch (final Exception ex) {
      throw new RuntimeException("Failed to convert String to : " + typeReference.toString() + " " + ex.getMessage(), ex);
    }
  }

  @Override
  public void nullSafeSet(PreparedStatement ps, Object value, int idx, SharedSessionContractImplementor session)
      throws HibernateException, SQLException {
    if (value == null) {
      ps.setNull(idx, Types.OTHER);
      return;
    }
    try {
      final StringWriter w = new StringWriter();
      JsonObjectMapperFactory.getObjectMapper().writerFor(typeReference).writeValue(w, value);
      ps.setObject(idx, w.toString(), Types.OTHER);
    } catch (final Exception ex) {
      throw new RuntimeException("Failed to convert Invoice to String: " + ex.getMessage(), ex);
    }

  }

  @Override
  public Object deepCopy(Object value) throws HibernateException {
    try {
      // use serialization to create a deep copy
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(value);
      oos.flush();
      oos.close();
      bos.close();

      ByteArrayInputStream bais = new ByteArrayInputStream(bos.toByteArray());
      return new ObjectInputStream(bais).readObject();
    } catch (ClassNotFoundException | IOException ex) {
      throw new HibernateException(ex);
    }
  }

  @Override
  public boolean isMutable() {
    return true;
  }

  @Override
  public Serializable disassemble(Object value) throws HibernateException {
    return (Serializable) this.deepCopy(value);
  }

  @Override
  public Object assemble(Serializable cached, Object owner) throws HibernateException {
    return this.deepCopy(cached);
  }

  @Override
  public Object replace(Object original, Object target, Object owner) throws HibernateException {
    return this.deepCopy(original);
  }

}

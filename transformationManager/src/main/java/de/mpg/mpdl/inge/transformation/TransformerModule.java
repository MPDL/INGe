package de.mpg.mpdl.inge.transformation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(TransformerModules.class)
public @interface TransformerModule {

  TransformerFactory.FORMAT sourceFormat();

  TransformerFactory.FORMAT targetFormat();

}

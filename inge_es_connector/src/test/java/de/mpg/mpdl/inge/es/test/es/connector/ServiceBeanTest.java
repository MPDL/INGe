package de.mpg.mpdl.inge.es.test.es.connector;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import de.mpg.mpdl.inge.es.IngeESConnectorConfiguration;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ContextConfiguration(classes = IngeESConnectorConfiguration.class)
// @ActiveProfiles("dev")
public @interface ServiceBeanTest {

}

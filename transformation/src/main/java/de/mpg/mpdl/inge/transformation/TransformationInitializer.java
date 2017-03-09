package de.mpg.mpdl.inge.transformation;

import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.reflections.Reflections;

import de.mpg.mpdl.inge.transformation.Transformation.TransformationModule;

public class TransformationInitializer {
  private static final Logger logger = Logger.getLogger(TransformationInitializer.class);

  private Set<Class<?>> transformationClasses = new HashSet<Class<?>>();

  public void initializeTransformationModules(boolean local) throws RuntimeException {
    this.initializeTransformationModules();
  }

  /**
   * Searches for all classes which implement the transformation module.
   * 
   * @throws RuntimeException
   */
  public void initializeTransformationModules() throws RuntimeException {
    try {
      this.transformationClasses =
          new Reflections("de.mpg.mpdl.inge.transformation")
              .getTypesAnnotatedWith(TransformationModule.class);
    } catch (Exception e) {
      logger.error("An error occurred during the allocation of transformation classes.", e);
      throw new RuntimeException(e);
    }
  }

  public Set<Class<?>> getTransformationClasses() {
    return this.transformationClasses;
  }

  public void setTransformationClasses(Set<Class<?>> transformationClasses) {
    this.transformationClasses = transformationClasses;
  }
}

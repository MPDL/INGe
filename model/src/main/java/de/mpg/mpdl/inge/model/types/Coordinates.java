package de.mpg.mpdl.inge.model.types;

import java.io.Serializable;

/**
 * Simple implementation of KLM coordinates.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
@SuppressWarnings("serial")
public class Coordinates implements Serializable {
  private double latitude;
  private double longitude;
  private double altitude;

  // private boolean altitudeSet;

  public Coordinates() {}

  /**
   * Constructor for 3D coordinates.
   *
   * @param latitude
   * @param longitute
   * @param altitude
   */
  public Coordinates(double latitude, double longitute, double altitude) {
    this.latitude = latitude;
    this.longitude = longitute;
    this.altitude = altitude;
    // this.altitudeSet = true;
  }

  /**
   * Constructor for 2D coordinates.
   *
   * @param latitude
   * @param longitute
   */
  public Coordinates(double latitude, double longitute) {
    this.latitude = latitude;
    this.longitude = longitute;
    this.altitude = 0;
    // this.altitudeSet = false;
  }

  public Coordinates(String coordinates) {
    if (null != coordinates && !coordinates.isEmpty()) {
      String[] coordinatesArray = coordinates.split(",");
      if (2 == coordinatesArray.length) {
        this.latitude = Double.parseDouble(coordinatesArray[0]);
        this.longitude = Double.parseDouble(coordinatesArray[1]);
        // this.altitudeSet = false;
      } else if (3 == coordinatesArray.length) {
        this.latitude = Double.parseDouble(coordinatesArray[0]);
        this.longitude = Double.parseDouble(coordinatesArray[1]);
        this.altitude = Double.parseDouble(coordinatesArray[2]);
        // this.altitudeSet = true;
      } else {
        throw new NumberFormatException("The coordinates are not in the right format");
      }
    }
  }

  // public String toString() {
  // if (altitudeSet) {
  // return latitude + "," + longitude + "," + altitude;
  // } else {
  // return latitude + "," + longitude;
  // }
  // }

  public double getLatitude() {
    return this.latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return this.longitude;
  }

  public void setLongitude(double longitute) {
    this.longitude = longitute;
  }

  public double getAltitude() {
    return this.altitude;
  }

  public void setAltitude(double altitude) {
    this.altitude = altitude;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(this.altitude);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(this.latitude);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(this.longitude);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if (null == obj)
      return false;

    if (getClass() != obj.getClass())
      return false;

    Coordinates other = (Coordinates) obj;

    if (Double.doubleToLongBits(this.altitude) != Double.doubleToLongBits(other.altitude))
      return false;

    if (Double.doubleToLongBits(this.latitude) != Double.doubleToLongBits(other.latitude))
      return false;

    if (Double.doubleToLongBits(this.longitude) != Double.doubleToLongBits(other.longitude))
      return false;

    return true;
  }

}

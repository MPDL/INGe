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

  public Coordinates(String coordinates) throws Exception {
    if (coordinates != null && !"".equals(coordinates)) {
      String[] coordinatesArray = coordinates.split(",");
      if (coordinatesArray.length == 2) {
        this.latitude = Double.parseDouble(coordinatesArray[0]);
        this.longitude = Double.parseDouble(coordinatesArray[1]);
        // this.altitudeSet = false;
      } else if (coordinatesArray.length == 3) {
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
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitute) {
    this.longitude = longitute;
  }

  public double getAltitude() {
    return altitude;
  }

  public void setAltitude(double altitude) {
    this.altitude = altitude;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(altitude);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(latitude);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(longitude);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;

    if (obj == null)
      return false;

    if (getClass() != obj.getClass())
      return false;

    Coordinates other = (Coordinates) obj;

    if (Double.doubleToLongBits(altitude) != Double.doubleToLongBits(other.altitude))
      return false;

    if (Double.doubleToLongBits(latitude) != Double.doubleToLongBits(other.latitude))
      return false;

    if (Double.doubleToLongBits(longitude) != Double.doubleToLongBits(other.longitude))
      return false;

    return true;
  }

}

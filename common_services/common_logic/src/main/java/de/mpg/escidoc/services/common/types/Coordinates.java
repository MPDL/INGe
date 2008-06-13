package de.mpg.escidoc.services.common.types;


/**
 * Simple implementation of KLM coordinates.
 *
 * @author franke (initial creation)
 * @author $Author$ (last modification)
 * @version $Revision$ $LastChangedDate$
 *
 */
public class Coordinates
{
    private double latitude;
    private double longitute;
    private double altitude;
    
    private boolean altitudeSet;
    
    /**
     * Constructor for 3D coordinates.
     * 
     * @param latitude
     * @param longitute
     * @param altitude
     */
    public Coordinates(double latitude, double longitute, double altitude)
    {
        this.latitude = latitude;
        this.longitute = longitute;
        this.altitude = altitude;
        this.altitudeSet = true;
    }

    /**
     * Constructor for 2D coordinates.
     * 
     * @param latitude
     * @param longitute
     */
    public Coordinates(double latitude, double longitute)
    {
        this.latitude = latitude;
        this.longitute = longitute;
        this.altitude = 0;
        this.altitudeSet = false;
    }

    public Coordinates(String coordinates) throws Exception
    {
        if (coordinates != null)
        {
            String[] coordinatesArray = coordinates.split(",");
            if (coordinatesArray.length == 2)
            {
                this.latitude = Double.parseDouble(coordinatesArray[0]);
                this.longitute = Double.parseDouble(coordinatesArray[1]);
                this.altitudeSet = false;
            }
            else if (coordinatesArray.length == 3)
            {
                this.latitude = Double.parseDouble(coordinatesArray[0]);
                this.longitute = Double.parseDouble(coordinatesArray[1]);
                this.altitude = Double.parseDouble(coordinatesArray[2]);
                this.altitudeSet = true;
            }
            else
            {
                throw new NumberFormatException("The coordinates are not in the right format");
            }
        }
    }
    
    public String toString()
    {
        if (altitudeSet)
        {
            return latitude + "," + longitute + "," + altitude;
        }
        else
        {
            return latitude + "," + longitute;
        }
    }
    
    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLongitute()
    {
        return longitute;
    }

    public void setLongitute(double longitute)
    {
        this.longitute = longitute;
    }

    public double getAltitude()
    {
        return altitude;
    }

    public void setAltitude(double altitude)
    {
        this.altitude = altitude;
    }
    
    
}

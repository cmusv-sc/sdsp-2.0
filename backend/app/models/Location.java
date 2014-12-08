package models;

public class Location {

  public static Location INVALID = new Location(0, 0, 0, "");

  private double longitude; // degrees
  private double latitude;
  private double altitude;
  private String representation;

  public Location(double longitude, double latitude, double altitude, String representation) {
    this.longitude = longitude;
    this.latitude = latitude;
    this.altitude = altitude;
    this.representation = representation;
  }
  
  public boolean equals(Location loc) {
    return (this.longitude == loc.getLongitude())
        && (this.latitude == loc.getLatitude())
        && (this.altitude == loc.getAltitude())
        && (this.representation.equals(loc.getRepresentation()));
  }

  public double getLongitude() {
    return this.longitude;
  }

  public double getLatitude() {
    return this.latitude;
  }

  public double getAltitude() {
    return this.altitude;
  }
  public String getRepresentation() {
    return this.representation;
  }
}

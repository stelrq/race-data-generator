package model;

/**
 * 
 * @author Myles Haynes
 */
public enum SpeedClass {

  FAST(0),
  MEDIUM_FAST(-.010),
  MEDIUM_SLOW(-.020),
  SLOW(-.030);


  private double offset;

  SpeedClass(double offset) {
    this.offset = offset;
  }

  public double getOffset() {
    return offset;
  }
}

package cellsociety.model;

public class Cell {
  private int state;


  public Cell(int state) {
    this.state = state;
  }

  public void setState(int state) {
    this.state = state;
  }

  public int getState() {
    return state;
  }

}

package cellsociety.model;

public class Cell {
  private int state;
  private int nextState;

  public Cell(int state) {
    this.state = state;
    this.nextState = state;
  }

  public void setState(int state) {
    this.state = state;
    this.nextState = state;
  }

  public void updateState() {
    this.state = this.nextState;
  }

  public void setNextState(int state) {
    this.nextState = state;
  }

  public int getNextState() {
    return nextState;
  }

  public int getState() {
    return state;
  }

  public Cell copy() {
    Cell copy = new Cell(this.state);
    copy.setNextState(this.nextState);
    return copy;
  }
}

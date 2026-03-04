package cellsociety.model.games;

class WaTorShark {

  private int energy;
  private int life = 0;

  WaTorShark(int energy) {
    this.energy = energy;
  }

  boolean isDead() {
    return energy <= 0;
  }

  int reProd() {
    return life;
  }

  void reset() {
    life = 0;
  }

  void moved(int eat) {
    energy += eat;
    life++;
  }
}

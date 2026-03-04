package cellsociety.model.games;

class WaTorFish {

  private int life = 0;

  int reProd() {
    return life;
  }

  void reset() {
    life = 0;
  }

  void moved() {
    life++;
  }
}

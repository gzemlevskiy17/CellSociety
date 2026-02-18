package cellsociety.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class WaTorTest {
    Grid defaultGrid = new Grid(2, 2);
    Game defaultGame = new WaTor(defaultGrid, 3.0, 6.0, 10.0, 3.0);

    @Test
    void Step_SharkWithOneFish() {

        defaultGrid.getCell(1, 1).setState(2);
        defaultGrid.getCell(2, 1).setState(1);

        defaultGame.step();

        assertEquals(2, defaultGrid.getCell(2, 1).getState());
    }

    @Test
    void Step_Shark_BecomesEmpty() {
        defaultGrid.getCell(1, 1).setState(2);
        for (int i = 0; i < 8; i++) {
            defaultGame.step();
        }
        int total = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                total += defaultGrid.getCell(1, 1).getState();
            }
        }

        assertEquals(0, total);
    }

    @Test
    void Step_Shark_Moves() {
        defaultGrid.getCell(1, 1).setState(2);
        defaultGame.step();
        int total = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (i != 1 && j != 1) {
                    total += defaultGrid.getCell(i, j).getState();
                }
            }
        }

        assertEquals(2, total);
    }

    @Test
    void Step_FishMoves() {
        defaultGrid.getCell(1, 1).setState(1);
        defaultGame.step();
        int total = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (i != 1 && j != 1) {
                    total += defaultGrid.getCell(i, j).getState();
                }
            }
        }

        assertEquals(1, total);
    }

    @Test
    void Step_FishReproduce() {
        defaultGrid.getCell(1, 1).setState(1);
        for (int i = 0; i < 4; i++) {
            defaultGame.step();
        }
        int total = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (defaultGrid.getCell(i, j).getState() == 1) {
                    total += defaultGrid.getCell(i, j).getState();
                }
            }
        }

        assertEquals(2, total);
    }

    @Test
    void Step_SharkReproduce() {
        defaultGrid.getCell(0, 0).setState(1);
        defaultGrid.getCell(0, 1).setState(1);
        defaultGrid.getCell(1, 0).setState(1);
        defaultGrid.getCell(1, 1).setState(2);
        for (int i = 0; i < 11; i++) {
            defaultGame.step();
        }
        int total = 0;
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                if (defaultGrid.getCell(i, j).getState() == 2) {
                    total += defaultGrid.getCell(i, j).getState();
                }
            }
        }

        assertEquals(4, total);
    }
}

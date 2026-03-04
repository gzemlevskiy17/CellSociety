package cellsociety.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.Test;

class CellTest {

    @Test
    void testConstructorSetsStateAndNextState() {
        Cell cell = new Cell(3);
        assertEquals(3, cell.getState());
        assertEquals(3, cell.getNextState());
    }

    @Test
    void testSetStateSetsCurrentAndNext() {
        Cell cell = new Cell(0);
        cell.setState(5);
        assertEquals(5, cell.getState());
        assertEquals(5, cell.getNextState());
    }

    @Test
    void testSetNextStateOnlyChangesNextState() {
        Cell cell = new Cell(1);
        cell.setNextState(2);
        assertEquals(1, cell.getState());
        assertEquals(2, cell.getNextState());
    }

    @Test
    void testUpdateStateAppliesPendingNextState() {
        Cell cell = new Cell(0);
        cell.setNextState(7);
        cell.updateState();
        assertEquals(7, cell.getState());
    }

    @Test
    void testUpdateStateDoesNotChangeNextState() {
        Cell cell = new Cell(0);
        cell.setNextState(4);
        cell.updateState();
        assertEquals(4, cell.getNextState());
    }

    @Test
    void testCopyHasSameStateValues() {
        Cell original = new Cell(2);
        original.setNextState(5);
        Cell copy = original.copy();
        assertEquals(original.getState(), copy.getState());
        assertEquals(original.getNextState(), copy.getNextState());
    }

    @Test
    void testCopyIsIndependentOfOriginal() {
        Cell original = new Cell(1);
        Cell copy = original.copy();
        assertNotSame(original, copy);
        original.setState(9);
        assertEquals(1, copy.getState());
    }

    @Test
    void testMultipleStepsAccumulate() {
        Cell cell = new Cell(0);
        cell.setNextState(1);
        cell.updateState();
        cell.setNextState(2);
        cell.updateState();
        assertEquals(2, cell.getState());
    }
}

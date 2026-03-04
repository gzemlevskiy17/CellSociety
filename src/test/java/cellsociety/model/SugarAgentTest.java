package cellsociety.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cellsociety.model.games.SugarAgent;
import org.junit.jupiter.api.Test;

class SugarAgentTest {

    @Test
    void testInitialPositionAndSugar() {
        SugarAgent agent = new SugarAgent(2, 3, 4, 1, 10);
        assertEquals(2, agent.getRow());
        assertEquals(3, agent.getCol());
        assertEquals(10, agent.getSugar());
    }

    @Test
    void testInitiallyAlive() {
        SugarAgent agent = new SugarAgent(0, 0, 1, 1, 5);
        assertTrue(agent.isAlive());
    }

    @Test
    void testAddSugarIncreasesReserve() {
        SugarAgent agent = new SugarAgent(0, 0, 1, 1, 10);
        agent.addSugar(5);
        assertEquals(15, agent.getSugar());
    }

    @Test
    void testMetabolizeReducesSugar() {
        SugarAgent agent = new SugarAgent(0, 0, 1, 3, 10);
        agent.metabolize();
        assertEquals(7, agent.getSugar());
        assertTrue(agent.isAlive());
    }

    @Test
    void testMetabolizeKillsAgentWhenSugarReachesZero() {
        SugarAgent agent = new SugarAgent(0, 0, 1, 5, 5);
        agent.metabolize();
        assertEquals(0, agent.getSugar());
        assertFalse(agent.isAlive());
    }

    @Test
    void testMetabolizeKillsAgentWhenSugarGoesNegative() {
        SugarAgent agent = new SugarAgent(0, 0, 1, 10, 3);
        agent.metabolize();
        assertTrue(agent.getSugar() < 0);
        assertFalse(agent.isAlive());
    }

    @Test
    void testAddSugarThenMetabolizeKeepsAlive() {
        SugarAgent agent = new SugarAgent(0, 0, 1, 2, 1);
        agent.addSugar(10);
        agent.metabolize();
        assertTrue(agent.isAlive());
        assertEquals(9, agent.getSugar());
    }

    @Test
    void testSetPositionUpdatesRowAndCol() {
        SugarAgent agent = new SugarAgent(0, 0, 1, 1, 5);
        agent.setPosition(4, 7);
        assertEquals(4, agent.getRow());
        assertEquals(7, agent.getCol());
    }

    @Test
    void testVisionAndMetabolismAreFixed() {
        SugarAgent agent = new SugarAgent(0, 0, 3, 2, 5);
        assertEquals(3, agent.getVision());
        assertEquals(2, agent.getSugarMetabolism());
    }
}

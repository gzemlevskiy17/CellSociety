package cellsociety.model.topology;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cellsociety.model.ParseResult;
import cellsociety.model.XMLReader;
import java.io.File;
import org.junit.jupiter.api.Test;

/**
 * Tests that {@link XMLReader} correctly parses topology tags
 * ({@code <neighborhood>}, {@code <edge>}, {@code <shape>}) from the
 * {@code <grid>} element and validates them against the allowed values.
 *
 * @author Michael Li
 */
class XMLReaderTopologyTest {

  // ─── Default values when tags are absent ─────────────────────────────────────

  @Test
  void defaultNeighborhoodIsMoore() throws Exception {
    File file = new File("data/GameOfLife/GameOfLifeSimple.xml");
    ParseResult result = XMLReader.parse(file);
    assertEquals("Moore", result.getNeighborhood());
  }

  @Test
  void defaultEdgeIsBounded() throws Exception {
    File file = new File("data/GameOfLife/GameOfLifeSimple.xml");
    ParseResult result = XMLReader.parse(file);
    assertEquals("Bounded", result.getEdge());
  }

  @Test
  void defaultShapeIsRectangle() throws Exception {
    File file = new File("data/GameOfLife/GameOfLifeSimple.xml");
    ParseResult result = XMLReader.parse(file);
    assertEquals("Rectangle", result.getShape());
  }

  // ─── Valid topology tags parsed correctly ─────────────────────────────────────

  @Test
  void parsesToroidalEdge() throws Exception {
    File file = new File("data/Topology/ToroidalEdge1.xml");
    ParseResult result = XMLReader.parse(file);
    assertEquals("Toroidal", result.getEdge());
  }

  @Test
  void parsesMirrorEdge() throws Exception {
    File file = new File("data/Topology/MirrorEdge1.xml");
    ParseResult result = XMLReader.parse(file);
    assertEquals("Mirror", result.getEdge());
  }

  @Test
  void parsesVonNeumannNeighborhood() throws Exception {
    File file = new File("data/Topology/VonNeumannNeighborhood1.xml");
    ParseResult result = XMLReader.parse(file);
    assertEquals("VonNeumann", result.getNeighborhood());
  }

  @Test
  void parsesExtendedMooreNeighborhood() throws Exception {
    File file = new File("data/Topology/ExtendedMooreNeighborhood1.xml");
    ParseResult result = XMLReader.parse(file);
    assertEquals("ExtendedMoore", result.getNeighborhood());
  }

  @Test
  void parsesHexagonalShape() throws Exception {
    File file = new File("data/Topology/HexagonalShape1.xml");
    ParseResult result = XMLReader.parse(file);
    assertEquals("Hexagonal", result.getShape());
    assertEquals("Hexagonal", result.getNeighborhood());
  }

  @Test
  void parsesTriangularShape() throws Exception {
    File file = new File("data/Topology/TriangularShape1.xml");
    ParseResult result = XMLReader.parse(file);
    assertEquals("Triangular", result.getShape());
    assertEquals("Triangular", result.getNeighborhood());
  }

  @Test
  void parsesHexagonalNeighborhood() throws Exception {
    File file = new File("data/Topology/HexagonalNeighborhood1.xml");
    ParseResult result = XMLReader.parse(file);
    assertEquals("Hexagonal", result.getNeighborhood());
  }

  @Test
  void parsesTriangularNeighborhood() throws Exception {
    File file = new File("data/Topology/TriangularNeighborhood1.xml");
    ParseResult result = XMLReader.parse(file);
    assertEquals("Triangular", result.getNeighborhood());
  }

  // ─── Invalid topology tags throw ─────────────────────────────────────────────

  @Test
  void invalidNeighborhoodThrows() {
    File file = new File("data/Error/ErrorInvalidNeighborhood.xml");
    IllegalArgumentException ex = assertThrows(
        IllegalArgumentException.class, () -> XMLReader.parse(file));
    assertTrue(ex.getMessage().contains("Invalid neighborhood"));
  }

  @Test
  void invalidEdgeThrows() {
    File file = new File("data/Error/ErrorInvalidEdge.xml");
    IllegalArgumentException ex = assertThrows(
        IllegalArgumentException.class, () -> XMLReader.parse(file));
    assertTrue(ex.getMessage().contains("Invalid edge"));
  }
}

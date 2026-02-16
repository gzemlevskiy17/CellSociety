package cellsociety.model;

import cellsociety.ParseResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Reads and writes simulation configuration XML files.
 *
 * <p>Supports two ways of specifying initial cell states:</p>
 * <ul>
 *   <li><b>Explicit:</b> a {@code <cells>} block with one {@code <row>} per grid row,
 *       containing space-separated state integers.</li>
 *   <li><b>Algorithmic:</b> a {@code <stateDistribution>} block that specifies state
 *       counts or proportions; cells are then randomly assigned to match.</li>
 * </ul>
 *
 * <p><b>Assumptions:</b> The XML file is well-formed and follows the expected schema.
 * Grid dimensions must be present (as {@code <rows>/<columns>} or {@code <width>/<height>}).
 * If neither {@code <cells>} nor {@code <stateDistribution>} is provided, {@code cells}
 * will be null in the resulting {@link ParseResult}.</p>
 *
 * <p><b>Dependencies:</b> {@link ParseResult}, {@code javax.xml.parsers},
 * {@code javax.xml.transform}, JavaFX {@link Stage} (for save dialog).</p>
 *
 * <p><b>Example usage:</b></p>
 * <pre>{@code
 * File file = new File("data/GameOfLifeSimple.xml");
 * ParseResult result = XMLParser.parse(file);
 * // result now contains grid dimensions, cell states, parameters, colors, etc.
 * }</pre>
 *
 * @author Michael Li
 */
public class XMLParser {

  /**
   * Parses a simulation XML file into a {@link ParseResult}.
   *
   * @param file the XML configuration file to parse
   * @return a {@link ParseResult} containing all extracted simulation data
   * @throws ParserConfigurationException if the XML parser cannot be configured
   * @throws SAXException if the XML is malformed
   * @throws IOException if the file cannot be read
   */
  public static ParseResult parse(File file)
      throws ParserConfigurationException, SAXException, IOException {
    Document xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder()
        .parse(file);
    Element root = xmlDocument.getDocumentElement();

    // 1. Root Level Metadata
    String gameType = getTextValue(root, "type");
    String title = getTextValue(root, "title");
    String author = getTextValue(root, "author");
    String description = getTextValue(root, "description");

    // 2. Section Elements
    Element gridEl = getFirstElement(root, "grid");
    Element paramEl = getFirstElement(root, "parameters");

    // 3. Grid Dimensions (fallback to root if not in <grid>)
    Element dimensionSource = root;
    if (gridEl != null) {
      dimensionSource = gridEl;
    }
    int[] dimensions = parseDimensions(dimensionSource);
    int rows = dimensions[0];
    int cols = dimensions[1];

    // 4. Detailed Data
    int[][] cells = null;
    if (gridEl != null) {
      cells = parseCells(gridEl, rows, cols);
      if (cells == null) {
        cells = parseStateDistribution(gridEl, rows, cols);
      }
    }

    double[] params = new double[] { 0.0, 0.0 };
    double speed = 1.0;
    double threshold = 0.0;
    if (paramEl != null) {
      params = parseParameters(paramEl);
      speed = parseDouble(getTextValue(paramEl, "speed"), 1.0);
      threshold = parseDouble(getTextValue(paramEl, "threshold"), 0.0);
    }
    Map<Integer, String> colorMap = parseColors(root); // Search root for all <state> tags

    return new ParseResult(rows, cols, gameType, title, author, description, cells, params[0], params[1], speed, threshold, colorMap);
  }

  /**
   * Opens a save dialog and saves the ParseResult to the chosen XML file.
   *
   * @param result the ParseResult to save
   * @param stage  the owner stage for the FileChooser dialog
   */
  public static void save(ParseResult result, Stage stage)
      throws ParserConfigurationException, TransformerException {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Save Simulation As");
    File dataDir = new File(
        Paths.get(".").toAbsolutePath().normalize().resolve("cellsociety_team01/data").toString());
    if (dataDir.exists()) {
      fileChooser.setInitialDirectory(dataDir);
    }
    fileChooser.getExtensionFilters().add(new ExtensionFilter("XML Files", "*.xml"));
    fileChooser.setInitialFileName("simulation.xml");
    File outputFile = fileChooser.showSaveDialog(stage);
    if (outputFile == null) {
      return;
    }

    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

    Element root = doc.createElement("simulation");
    doc.appendChild(root);

    appendTextElement(doc, root, "type", result.getGameType());
    appendTextElement(doc, root, "title", result.getTitle());
    appendTextElement(doc, root, "author", result.getAuthor());
    appendTextElement(doc, root, "description", result.getDescription());

    Element gridEl = doc.createElement("grid");
    root.appendChild(gridEl);
    appendTextElement(doc, gridEl, "rows", String.valueOf(result.getRows()));
    appendTextElement(doc, gridEl, "columns", String.valueOf(result.getCols()));

    if (result.getCells() != null) {
      Element cellsEl = doc.createElement("cells");
      gridEl.appendChild(cellsEl);
      for (int[] row : result.getCells()) {
        StringBuilder sb = new StringBuilder();
        for (int c = 0; c < row.length; c++) {
          if (c > 0) {
            sb.append(" ");
          }
          sb.append(row[c]);
        }
        appendTextElement(doc, cellsEl, "row", sb.toString());
      }
    }

    Element paramEl = doc.createElement("parameters");
    root.appendChild(paramEl);
    if (result.getProbCatch() != 0.0) {
      appendTextElement(doc, paramEl, "probCatch", String.valueOf(result.getProbCatch()));
    }
    if (result.getProbGrow() != 0.0) {
      appendTextElement(doc, paramEl, "probGrow", String.valueOf(result.getProbGrow()));
    }

    if (result.getColorMap() != null && !result.getColorMap().isEmpty()) {
      Element statesEl = doc.createElement("states");
      paramEl.appendChild(statesEl);
      for (Map.Entry<Integer, String> entry : result.getColorMap().entrySet()) {
        Element stateEl = doc.createElement("state");
        stateEl.setAttribute("id", String.valueOf(entry.getKey()));
        stateEl.setAttribute("color", entry.getValue());
        statesEl.appendChild(stateEl);
      }
    }

    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
    transformer.transform(new DOMSource(doc), new StreamResult(outputFile));
  }

  private static void appendTextElement(Document doc, Element parent, String tagName,
      String textContent) {
    if (textContent == null || textContent.isEmpty()) {
      return;
    }
    Element el = doc.createElement(tagName);
    el.setTextContent(textContent);
    parent.appendChild(el);
  }

  private static Element getFirstElement(Element parent, String tagName) {
    NodeList nodeList = parent.getElementsByTagName(tagName);
    return (nodeList.getLength() > 0) ? (Element) nodeList.item(0) : null;
  }

  private static int[] parseDimensions(Element container) {
    String rowsText = getTextValue(container, "rows");
    String colsText = getTextValue(container, "columns");
    String widthText = getTextValue(container, "width");
    String heightText = getTextValue(container, "height");

    try {
      if (!rowsText.isEmpty() && !colsText.isEmpty()) {
        return new int[] { Integer.parseInt(rowsText.trim()), Integer.parseInt(colsText.trim()) };
      } else if (!widthText.isEmpty() && !heightText.isEmpty()) {
        return new int[] { Integer.parseInt(widthText.trim()), Integer.parseInt(heightText.trim()) };
      }
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid numeric value for dimensions in XML", e);
    }
    throw new IllegalArgumentException("Missing grid dimensions in XML");
  }

  private static int[][] parseCells(Element gridEl, int rows, int cols) {
    Element cellsEl = getFirstElement(gridEl, "cells");
    if (cellsEl == null) {
      return null;
    }

    NodeList rowNodes = cellsEl.getElementsByTagName("row");
    if (rowNodes.getLength() == 0) {
      return null;
    }

    int[][] cells = new int[rows][cols];
    for (int r = 0; r < Math.min(rowNodes.getLength(), rows); r++) {
      parseRowContent(rowNodes.item(r).getTextContent().trim(), cells[r], cols);
    }
    return cells;
  }

  private static int[][] parseStateDistribution(Element gridEl, int rows, int cols) {
    Element distEl = getFirstElement(gridEl, "stateDistribution");
    if (distEl == null) {
      return null;
    }

    int totalCells = rows * cols;
    String type = distEl.getAttribute("type");
    NodeList stateNodes = distEl.getElementsByTagName("state");

    // Build a flat list of cell states, then shuffle to randomize
    List<Integer> flatCells = new ArrayList<>(Collections.nCopies(totalCells, 0));

    int assigned = 0;
    for (int i = 0; i < stateNodes.getLength(); i++) {
      Element stateEl = (Element) stateNodes.item(i);
      int stateId = Integer.parseInt(stateEl.getAttribute("id"));

      int count;
      if ("proportion".equals(type)) {
        double proportion = Double.parseDouble(stateEl.getAttribute("proportion"));
        count = (int) Math.round(proportion * totalCells);
      } else {
        count = Integer.parseInt(stateEl.getAttribute("count"));
      }

      count = Math.min(count, totalCells - assigned);
      for (int j = 0; j < count; j++) {
        flatCells.set(assigned + j, stateId);
      }
      assigned += count;
    }

    Collections.shuffle(flatCells);

    // Convert flat list back to 2D array
    int[][] cells = new int[rows][cols];
    for (int i = 0; i < totalCells; i++) {
      cells[i / cols][i % cols] = flatCells.get(i);
    }
    return cells;
  }

  private static void parseRowContent(String rowText, int[] rowArray, int cols) {
    if (rowText.isEmpty()) {
      return;
    }
    String[] parts = rowText.split("\\s+");
    for (int c = 0; c < Math.min(parts.length, cols); c++) {
      try {
        rowArray[c] = Integer.parseInt(parts[c]);
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException("Invalid cell state in XML", ex);
      }
    }
  }

  private static double[] parseParameters(Element paramEl) {
    double probCatch = parseDouble(getTextValue(paramEl, "probCatch"), 0.0);
    double probGrow = parseDouble(getTextValue(paramEl, "probGrow"), 0.0);
    return new double[] { probCatch, probGrow };
  }

  private static Map<Integer, String> parseColors(Element container) {
    Map<Integer, String> colorMap = new HashMap<>();
    NodeList stateNodes = container.getElementsByTagName("state");
    for (int i = 0; i < stateNodes.getLength(); i++) {
      Element stateEl = (Element) stateNodes.item(i);
      String idText = stateEl.getAttribute("id");
      String colorText = stateEl.getAttribute("color");
      if (!idText.isEmpty() && !colorText.isEmpty()) {
        try {
          colorMap.put(Integer.parseInt(idText), colorText);
        } catch (NumberFormatException ignored) {
        }
      }
    }
    return colorMap;
  }

  private static double parseDouble(String value, double defaultValue) {
    if (value.isEmpty()) {
      return defaultValue;
    }
    try {
      return Double.parseDouble(value.trim());
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  private static String getTextValue(Element e, String tagName) {
    NodeList nodeList = e.getElementsByTagName(tagName);
    return (nodeList.getLength() > 0) ? nodeList.item(0).getTextContent().trim() : "";
  }
}
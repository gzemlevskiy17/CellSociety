package cellsociety.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Reads simulation configuration XML files into {@link ParseResult} objects.
 *
 * @author Michael Li
 */
public class XMLReader {

  private static final List<String> VALID_TYPES = Arrays.asList(
      "GameOfLife", "ForestFire", "Segregation", "Percolation", "WaTor", "FallingSand");

  public static ParseResult parse(File file)
      throws ParserConfigurationException, IOException {
    if (!file.exists()) {
      throw new IllegalArgumentException("File not found: " + file.getName());
    }
    if (file.length() == 0) {
      throw new IllegalArgumentException("File is empty: " + file.getName());
    }

    Document xmlDocument;
    try {
      xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
    } catch (SAXException e) {
      throw new IllegalArgumentException("File is not valid XML: " + file.getName(), e);
    }
    Element root = xmlDocument.getDocumentElement();

    // 1. Root Level Metadata
    String gameType = getTextValue(root, "type");
    String title = getTextValue(root, "title");
    String author = getTextValue(root, "author");
    String description = getTextValue(root, "description");

    validateRequiredField(gameType, "type");
    validateRequiredField(title, "title");
    validateRequiredField(author, "author");
    validateRequiredField(description, "description");
    validateSimulationType(gameType);

    // 2. Section Elements
    Element gridEl = getFirstElement(root, "grid");
    Element paramEl = getFirstElement(root, "parameters");

    // 3. Grid Dimensions (fallback to root if not in <grid>)
    Element dimensionSource = root;
    if (gridEl != null) {
      dimensionSource = gridEl;
    }
    int[] dims = parseDimensions(dimensionSource);
    if (dims[0] < 1 || dims[1] < 1) {
      throw new IllegalArgumentException("Grid dimensions must be positive, got: " + dims[0] + "x" + dims[1]);
    }

    // 4. Detailed Data
    int[][] cells = null;
    if (gridEl != null) {
      cells = parseCells(gridEl, dims[0], dims[1]);
      if (cells == null) {
        cells = parseStateDistribution(gridEl, dims[0], dims[1]);
      }
    }

    Map<String, String> params = null;
    if (paramEl != null) {
      params = parseParameters(paramEl);
    } else {
      params = new HashMap<String, String>();
      params.put("speed", "1");
    }
    Element colorEl = getFirstElement(paramEl, "states");
    Map<Integer, String> colorMap = parseColors(colorEl);

    if (cells != null && !colorMap.isEmpty()) {
      validateCellStates(cells, colorMap.keySet());
    }

    String rulestring = "";
    if (paramEl != null) {
      rulestring = getTextValue(paramEl, "rulestring");
    }

    return new ParseResult(dims[0], dims[1], gameType, title, author, description, cells, params, colorMap);
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

    if (rowNodes.getLength() > rows) {
      throw new IllegalArgumentException(
          "Too many rows in cell data: found " + rowNodes.getLength() + " but grid has " + rows + " rows");
    }
    int[][] cells = new int[rows][cols];
    for (int r = 0; r < rowNodes.getLength(); r++) {
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
    if (parts.length > cols) {
      throw new IllegalArgumentException(
          "Too many columns in row data: found " + parts.length + " but grid has " + cols + " columns");
    }
    for (int c = 0; c < parts.length; c++) {
      try {
        rowArray[c] = Integer.parseInt(parts[c]);
      } catch (NumberFormatException ex) {
        throw new IllegalArgumentException("Invalid cell state in XML", ex);
      }
    }
  }

  private static Map<String, String> parseParameters(Element paramEl) {
    Map<String, String> holding = new HashMap<String, String>();
    NodeList nodes = paramEl.getChildNodes();
    for (int i = 0; i < nodes.getLength(); i++) {
      if (!nodes.item(i).getNodeName().equals("states") && !nodes.item(i).getNodeName().equals("#text")) {
        String value = getTextValue(paramEl, nodes.item(i).getNodeName());
        holding.put(nodes.item(i).getNodeName(), value);
      }
    }
    if (!holding.containsKey("speed") || parseDouble(holding.get("speed"), 0.0) <= 0.0) {
      holding.put("speed", "1");
    }
    return holding;
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

  private static void validateRequiredField(String value, String fieldName) {
    if (value == null || value.isEmpty()) {
      throw new IllegalArgumentException("Missing required field: " + fieldName);
    }
  }

  private static void validateSimulationType(String gameType) {
    if (!VALID_TYPES.contains(gameType)) {
      throw new IllegalArgumentException("Invalid simulation type: " + gameType);
    }
  }

  private static void validateCellStates(int[][] cells, Set<Integer> validStates) {
    for (int i = 0; i < cells.length; i++) {
      for (int j = 0; j < cells[i].length; j++) {
        if (!validStates.contains(cells[i][j])) {
          throw new IllegalArgumentException(
              "Invalid cell state " + cells[i][j] + " at (" + i + ", " + j
                  + "). Valid states: " + validStates);
        }
      }
    }
  }

  private static Element getFirstElement(Element parent, String tagName) {
    NodeList nodeList = parent.getElementsByTagName(tagName);
    if (nodeList.getLength() > 0) {
      return (Element) nodeList.item(0);
    }
    return null;
  }

  private static String getTextValue(Element e, String tagName) {
    NodeList nodeList = e.getElementsByTagName(tagName);
    if (nodeList.getLength() > 0) {
      return nodeList.item(0).getTextContent().trim();
    }
    return "";
  }
}

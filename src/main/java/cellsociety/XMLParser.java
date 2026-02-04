package cellsociety;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Simple XML parser that reads basic simulation configuration.
 */
public class XMLParser {

    public static ParseResult parse(InputStream inputStream)
            throws ParserConfigurationException, SAXException, IOException {
        Document xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        Element root = xmlDocument.getDocumentElement();

        // 1. Root Level Metadata
        String gameType = getTextValue(root, "type");

        // 2. Section Elements
        Element gridEl = getFirstElement(root, "grid");
        Element paramEl = getFirstElement(root, "parameters");

        // 3. Grid Dimensions (fallback to root if not in <grid>)
        int[] dimensions = parseDimensions(gridEl != null ? gridEl : root);
        int rows = dimensions[0];
        int cols = dimensions[1];

        // 4. Detailed Data
        int[][] cells = (gridEl != null) ? parseCells(gridEl, rows, cols) : null;
        double[] params = (paramEl != null) ? parseParameters(paramEl) : new double[] { 0.0, 0.0 };
        Map<Integer, String> colorMap = parseColors(root); // Search root for all <state> tags

        return new ParseResult(rows, cols, gameType, cells, params[0], params[1], colorMap);
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
        if (cellsEl == null)
            return null;

        NodeList rowNodes = cellsEl.getElementsByTagName("row");
        if (rowNodes.getLength() == 0)
            return null;

        int[][] cells = new int[rows][cols];
        for (int r = 0; r < Math.min(rowNodes.getLength(), rows); r++) {
            parseRowContent(rowNodes.item(r).getTextContent().trim(), cells[r], cols);
        }
        return cells;
    }

    private static void parseRowContent(String rowText, int[] rowArray, int cols) {
        if (rowText.isEmpty())
            return;
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
        if (value.isEmpty())
            return defaultValue;
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

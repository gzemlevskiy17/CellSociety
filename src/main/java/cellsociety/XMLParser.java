package cellsociety;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Simple XML parser that reads basic simulation configuration.
 * Expected elements: <width>, <height>, optional <game>
 */
public class XMLParser {

    public static ParseResult parse(InputStream inputStream)
            throws ParserConfigurationException, SAXException, IOException {
        Document xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream);
        Element root = xmlDocument.getDocumentElement();

        String gameType = getTextValue(root, "type");
        int[] dimensions = parseDimensions(root);
        int rows = dimensions[0];
        int cols = dimensions[1];

        int[][] cells = parseGrid(root, rows, cols);
        double[] params = parseParameters(root);
        java.util.Map<Integer, String> colorMap = parseColors(root);

        return new ParseResult(rows, cols, gameType, cells, params[0], params[1], colorMap);
    }

    private static int[] parseDimensions(Element root) {
        String rowsText = getTextValue(root, "rows");
        String colsText = getTextValue(root, "columns");
        String widthText = getTextValue(root, "width");
        String heightText = getTextValue(root, "height");

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

    private static int[][] parseGrid(Element root, int rows, int cols) {
        NodeList gridNodes = root.getElementsByTagName("grid");
        if (gridNodes.getLength() == 0)
            return null;

        Element gridEl = (Element) gridNodes.item(0);
        NodeList cellsNodes = gridEl.getElementsByTagName("cells");
        if (cellsNodes.getLength() == 0)
            return null;

        Element cellsEl = (Element) cellsNodes.item(0);
        NodeList rowNodes = cellsEl.getElementsByTagName("row");
        if (rowNodes.getLength() == 0)
            return null;

        int[][] cells = new int[rows][cols];
        for (int r = 0; r < Math.min(rowNodes.getLength(), rows); r++) {
            parseRow(rowNodes.item(r).getTextContent().trim(), cells[r], cols);
        }
        return cells;
    }

    private static void parseRow(String rowText, int[] rowArray, int cols) {
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

    private static double[] parseParameters(Element root) {
        double probCatch = 0.0;
        double probGrow = 0.0;
        NodeList paramNodes = root.getElementsByTagName("parameters");
        if (paramNodes.getLength() > 0) {
            Element paramEl = (Element) paramNodes.item(0);
            probCatch = parseDouble(getTextValue(paramEl, "probCatch"), 0.0);
            probGrow = parseDouble(getTextValue(paramEl, "probGrow"), 0.0);
        }
        return new double[] { probCatch, probGrow };
    }

    private static java.util.Map<Integer, String> parseColors(Element root) {
        java.util.Map<Integer, String> colorMap = new java.util.HashMap<>();
        NodeList stateNodes = root.getElementsByTagName("state");
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
        return (nodeList.getLength() > 0) ? nodeList.item(0).getTextContent() : "";
    }
}
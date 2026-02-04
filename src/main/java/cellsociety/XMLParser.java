package cellsociety;

import java.io.File;
import java.io.IOException;
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

    public static ParseResult parse(File xmlFile) throws ParserConfigurationException, SAXException, IOException {
        Document xmlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlFile);
        Element root = xmlDocument.getDocumentElement();

        String gameType = getTextValue(root, "type");

        String rowsText = getTextValue(root, "rows");
        String colsText = getTextValue(root, "columns");
        String widthText = getTextValue(root, "width");
        String heightText = getTextValue(root, "height");

        int rows = -1;
        int cols = -1;

        if (!rowsText.isEmpty() && !colsText.isEmpty()) {
            try {
                rows = Integer.parseInt(rowsText.trim());
                cols = Integer.parseInt(colsText.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid numeric value in XML", e);
            }
        } else if (!widthText.isEmpty() && !heightText.isEmpty()) {
            try {
                rows = Integer.parseInt(widthText.trim());
                cols = Integer.parseInt(heightText.trim());
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid numeric value in XML", e);
            }
        } else {
            throw new IllegalArgumentException("Missing grid dimensions in XML");
        }

        int[][] cells = null;
        NodeList gridNodes = root.getElementsByTagName("grid");
        if (gridNodes.getLength() > 0) {
            Element gridEl = (Element) gridNodes.item(0);
            NodeList cellsNodes = gridEl.getElementsByTagName("cells");
            if (cellsNodes.getLength() > 0) {
                Element cellsEl = (Element) cellsNodes.item(0);
                NodeList rowNodes = cellsEl.getElementsByTagName("row");
                if (rowNodes.getLength() > 0) {
                    cells = new int[rows][cols];
                    for (int r = 0; r < Math.min(rowNodes.getLength(), rows); r++) {
                        String rowText = rowNodes.item(r).getTextContent().trim();
                        if (rowText.isEmpty())
                            continue;
                        String[] parts = rowText.split("\\s+");
                        for (int c = 0; c < Math.min(parts.length, cols); c++) {
                            try {
                                cells[r][c] = Integer.parseInt(parts[c]);
                            } catch (NumberFormatException ex) {
                                throw new IllegalArgumentException("Invalid cell state in XML", ex);
                            }
                        }
                    }
                }
            }
        }

        // Parse parameters for game-specific settings
        double probCatch = 0.0; // defaults
        double probGrow = 0.0;

        NodeList paramNodes = root.getElementsByTagName("parameters");
        if (paramNodes.getLength() > 0) {
            Element paramEl = (Element) paramNodes.item(0);
            String probCatchText = getTextValue(paramEl, "probCatch");
            String probGrowText = getTextValue(paramEl, "probGrow");

            if (!probCatchText.isEmpty()) {
                probCatch = Double.parseDouble(probCatchText.trim());
            }
            if (!probGrowText.isEmpty()) {
                probGrow = Double.parseDouble(probGrowText.trim());
            }
        }

        return new ParseResult(rows, cols, gameType, cells, probCatch, probGrow);
    }

    private static String getTextValue(Element e, String tagName) {
        NodeList nodeList = e.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        } else {
            return "";
        }
    }
}

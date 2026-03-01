package cellsociety.model;

import java.io.File;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Writes simulation configuration data to XML files.
 *
 * <p>
 * This class is purely responsible for serializing a {@link ParseResult} to a
 * given file. File selection (e.g., via a save dialog) must be handled by the
 * caller in the controller layer.
 * </p>
 *
 * @author Michael Li
 */
public class XMLWriter {

  /**
   * Saves the given {@link ParseResult} to the specified output file as XML.
   *
   * @param result     the simulation configuration to save
   * @param outputFile the file to write to; must not be null
   * @throws ParserConfigurationException if the XML document builder cannot be created
   * @throws TransformerException         if the XML transformation fails
   */
  public static void save(ParseResult result, File outputFile)
      throws ParserConfigurationException, TransformerException {
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

    if (result.getParams() != null && !result.getParams().isEmpty()) {
      for (Map.Entry<String, String> entry : result.getParams().entrySet()) {
        appendTextElement(doc, paramEl, entry.getKey(), entry.getValue());
      }
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
}

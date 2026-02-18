package cellsociety.model;

import java.io.File;
import java.nio.file.Paths;
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

/**
 * Writes simulation configuration data to XML files.
 *
 * @author Michael Li
 */
public class XMLWriter {

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
}

package cellsociety;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cellsociety.controller.GameEngine;
import cellsociety.model.XMLParser;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

class XMLParserTest {

    @Test
    void parseValidSimpleXml() throws Exception {
        try (InputStream is = new FileInputStream("data/Simple.xml")) {
            ParseResult res = XMLParser.parse(is);
            assertEquals(25, res.getRows());
            assertEquals(20, res.getCols());
        }
    }

    @Test
    void parseInvalidXmlThrows() throws ParserConfigurationException, SAXException, IOException {
        try (InputStream is = new FileInputStream("data/BadData.xml")) {
            assertThrows(IllegalArgumentException.class, () -> XMLParser.parse(is));
        } catch (IOException e) {
            // If the file itself is missing, that's a different error, but for BadData.xml
            // we expect it to exist
            throw e;
        }
    }

    @Test
    void testParseActualFile() throws Exception {
        try (InputStream is = GameEngine.class.getResourceAsStream("/data/GameOfLifeSimple.xml")) {
            ParseResult result = XMLParser.parse(is);

            assertEquals("GameOfLife", result.getGameType());
            assertEquals(3, result.getRows());
            assertEquals(3, result.getCols());

            // Verify the specific cell at (1,1) is 1 (ALIVE)
            int[][] cells = result.getCells();
            assertEquals(1, cells[1][1]);
            assertEquals(0, cells[0][0]);

            // Verify colors are parsed
            java.util.Map<Integer, String> colors = result.getColorMap();
            assertEquals("#FFFFFF", colors.get(0));
            assertEquals("#2ECC71", colors.get(1));
        }
    }
}

package cellsociety;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import java.io.IOException;

class XMLParserTest {

    @Test
    void parseValidSimpleXml() throws Exception {
        File file = new File("data/Simple.xml");
        ParseResult res = XMLParser.parse(file);
        assertEquals(25, res.getRows());
        assertEquals(20, res.getCols());
    }

    @Test
    void parseInvalidXmlThrows() throws ParserConfigurationException, SAXException, IOException {
        File file = new File("data/BadData.xml");
        assertThrows(IllegalArgumentException.class, () -> XMLParser.parse(file));
    }
    
    @Test
    void testParseActualFile() throws Exception {
        File file = new File("data/GameOfLifeSimple.xml");
        ParseResult result = XMLParser.parse(file);

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


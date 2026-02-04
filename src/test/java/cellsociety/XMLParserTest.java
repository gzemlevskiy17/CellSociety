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
}

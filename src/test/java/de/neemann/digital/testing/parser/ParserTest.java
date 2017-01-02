package de.neemann.digital.testing.parser;

import de.neemann.digital.testing.TestingDataException;
import de.neemann.digital.testing.Value;
import junit.framework.TestCase;

import java.io.IOException;


/**
 * Created by Helmut.Neemann on 02.12.2016.
 */
public class ParserTest extends TestCase {

    public void testOk() throws TestingDataException, IOException, ParserException {
        Parser td = new Parser("A B\n0 1\n1 0\nX x").parse();
        assertEquals(2, td.getNames().size());

        assertEquals(3, td.getLines().size());


        assertEquals(0, td.getLines().get(0)[0].getValue());
        assertEquals(Value.Type.NORMAL, td.getLines().get(0)[0].getType());

        assertEquals(1, td.getLines().get(0)[1].getValue());
        assertEquals(Value.Type.NORMAL, td.getLines().get(0)[1].getType());

        assertEquals(1, td.getLines().get(1)[0].getValue());
        assertEquals(Value.Type.NORMAL, td.getLines().get(1)[0].getType());

        assertEquals(0, td.getLines().get(1)[1].getValue());
        assertEquals(Value.Type.NORMAL, td.getLines().get(1)[1].getType());

        assertEquals(Value.Type.DONTCARE, td.getLines().get(2)[0].getType());
        assertEquals(Value.Type.DONTCARE, td.getLines().get(2)[1].getType());
    }

    public void testHex() throws TestingDataException, IOException, ParserException {
        Parser td = new Parser("A B\n0 0xff").parse();
        assertEquals(2, td.getNames().size());

        assertEquals(1, td.getLines().size());

        assertEquals(0, td.getLines().get(0)[0].getValue());
        assertEquals(Value.Type.NORMAL, td.getLines().get(0)[0].getType());

        assertEquals(255, td.getLines().get(0)[1].getValue());
        assertEquals(Value.Type.NORMAL, td.getLines().get(0)[1].getType());
    }

    public void testMissingValue() throws IOException, ParserException {
        try {
            new Parser("A B\n0 0\n1").parse();
            assertTrue(false);
        } catch (ParserException e) {
            assertTrue(true);
        }
    }

    public void testInvalidValue() throws IOException, ParserException {
        try {
            new Parser("A B\n0 0\n1 u").parse();
            assertTrue(false);
        } catch (ParserException e) {
            assertTrue(true);
        }
    }

    public void testClock() throws Exception {
        Parser td = new Parser("A B\nC 1\nC 0").parse();
        assertEquals(2, td.getNames().size());
        assertEquals(2, td.getLines().size());

        assertEquals(Value.Type.CLOCK, td.getLines().get(0)[0].getType());
        assertEquals(1, td.getLines().get(0)[1].getValue());
        assertEquals(Value.Type.CLOCK, td.getLines().get(1)[0].getType());
        assertEquals(0, td.getLines().get(1)[1].getValue());
    }

    public void testFor() throws IOException, ParserException {
        Parser td = new Parser("A B\nfor(10) C (n*2)\n").parse();
        assertEquals(2, td.getNames().size());
        assertEquals(10, td.getLines().size());

        for (int i = 0; i < 10; i++) {
            assertEquals(Value.Type.CLOCK, td.getLines().get(i)[0].getType());
            assertEquals(i * 2, td.getLines().get(i)[1].getValue());
        }
    }

    public void testForBits() throws IOException, ParserException {
        Parser td = new Parser("A B C D \nfor(8) X bits(3,n)\n").parse();
        assertEquals(4, td.getNames().size());
        assertEquals(8, td.getLines().size());

        for (int i = 0; i < 8; i++) {
            assertEquals(Value.Type.DONTCARE, td.getLines().get(i)[0].getType());
            assertEquals((i >> 2) & 1, td.getLines().get(i)[1].getValue());
            assertEquals((i >> 1) & 1, td.getLines().get(i)[2].getValue());
            assertEquals(i & 1, td.getLines().get(i)[3].getValue());
        }
    }

    public void testComment() throws TestingDataException, IOException, ParserException {
        Parser td = new Parser("#test\nA B\n1 1").parse();
        assertEquals(2, td.getNames().size());
        assertEquals(1, td.getLines().size());
    }

    public void testHeader() throws TestingDataException, IOException, ParserException {
        Parser td = new Parser("A   B     C  D\n1 1 1 1").parse();
        assertEquals(4, td.getNames().size());
        assertEquals(1, td.getLines().size());
    }

    public void testEmptyLines() throws TestingDataException, IOException, ParserException {
        Parser td = new Parser("A_i B_i C_i-1 C_i S_i\n" +
                " 0   0   0     0   0\n" +
                " 0   0   1     0   1\n" +
                " 0   1   0     0   1\n\n" +
                " 0   1   1     1   0\n" +
                " 1   0   0     0   1\n\n" +
                " 1   0   1     1   0\n" +
                " 1   1   0     1   0\n" +
                " 1   1   1     1   1\n").parse();
        assertEquals(5, td.getNames().size());
        assertEquals(8, td.getLines().size());

        assertEquals("A_i", td.getNames().get(0));
        assertEquals("B_i", td.getNames().get(1));
        assertEquals("C_i-1", td.getNames().get(2));
    }

    public void testBUG1() throws IOException, ParserException {
        Parser td = new Parser("C_i-1 A B    C   S\n" +
                "for(1<<16) 0 (n>>8) (n&255) ((n>>8)*(n&255)) 0").parse();
        assertEquals(5, td.getNames().size());
        assertEquals(1<<16, td.getLines().size());

    }

}
package com.uic.ahegde5;

import com.uic.ahegde5.instrumentation.parser.InstrumentationParser;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertNotNull;

public class InstrumentationParserTest {

    @Test
    public void testParserExecution(){
        InstrumentationParser instrumentationParser = new InstrumentationParser("D:\\test", "D:\\backup\\adarsh_hegde_hw1");
        instrumentationParser.execute();
    }

    @Test
    public void testParsing(){
        InstrumentationParser instrumentationParser = new InstrumentationParser("D:\\test", "D:\\backup\\adarsh_hegde_hw1");
        try {
            CompilationUnit compilationUnit = instrumentationParser.parse("D:\\test");
            assertNotNull(compilationUnit);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFileCreation(){
        InstrumentationParser instrumentationParser = new InstrumentationParser("D:\\test", "D:\\backup\\adarsh_hegde_hw1");
        File file = new File("D:\\backup\\adarsh_hegde_hw1");
        assertNotNull(file);
    }


}

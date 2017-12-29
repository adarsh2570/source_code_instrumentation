package com.uic.ahegde5.instrumentation.parser;

import com.uic.ahegde5.instrumentation.utility.JavaFileListing;
import com.uic.ahegde5.instrumentation.utility.FIleUtility;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.TextEdit;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class InstrumentationParser {

    private String directoryPath;

    private String backupPath;

    public InstrumentationParser() {
    }

    public InstrumentationParser(String directoryPath, String backupPath) {

        this.directoryPath = directoryPath;
        this.backupPath = backupPath;
    }


    public CompilationUnit parse(String filePath) throws ExecutionException {
        ASTParser parser = ASTParser.newParser(AST.JLS8);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        Map options = JavaCore.getOptions();
        parser.setCompilerOptions(options);

        String unitName = filePath.substring(filePath.lastIndexOf("\\") + 1,filePath.length());
        parser.setUnitName(unitName);
        String[] sources = { directoryPath };
       // Document document = new Document("D:\\IntellijWorkspace\\JavaProblems\\src");
        String[] classpath = {};
        parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);
        try {
            parser.setSource(FIleUtility.readFileToString(filePath).toCharArray());
        } catch (IOException e) {
            System.out.println(e.getStackTrace().toString());
        }
        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
        return cu;
    }


    public String getDirectoryPath() {
        return directoryPath;
    }


    public void execute(){

        try {
            List<String> filePathList = JavaFileListing.listFilesInDirectory(directoryPath, backupPath);
            for(String filePath : filePathList) {
                InstrumentationVisitor instrumentationVisitor = new InstrumentationVisitor();
                System.out.println("File loaded : " + filePath);

                //Adding the instrumentation code here
                CompilationUnit compilationUnit = parse(filePath);
                compilationUnit.recordModifications();
                instrumentationVisitor.setCompilationUnit(compilationUnit);
                instrumentationVisitor.setFilePath(filePath);
                instrumentationVisitor.setSourceDocument(new Document(FIleUtility.readFileToString(instrumentationVisitor.getFilePath())));
                ASTRewrite astRewrite = ASTRewrite.create(compilationUnit.getAST());
                instrumentationVisitor.setAstRewrite(astRewrite);

                compilationUnit.accept(instrumentationVisitor);

                TextEdit edits = astRewrite.rewriteAST(instrumentationVisitor.getSourceDocument(), null);
                edits.apply(instrumentationVisitor.getSourceDocument());
                FileUtils.write(new File(filePath), instrumentationVisitor.getSourceDocument().get());

                //Adding imports to the file
                CompilationUnit compilationUnitForImport = parse(filePath);
                compilationUnitForImport.recordModifications();
                instrumentationVisitor.setFilePath(filePath);
                instrumentationVisitor.setCompilationUnit(compilationUnitForImport);
                instrumentationVisitor.setSourceDocument(new Document(FIleUtility.readFileToString(instrumentationVisitor.getFilePath())));
                instrumentationVisitor.addImport("com.uic.ahegde5.instrumentation.utility.TemplateClass");
                instrumentationVisitor.addImport("com.uic.ahegde5.instrumentation.utility.PairClass");
                try {
                    TextEdit textEdit = compilationUnitForImport.rewrite(instrumentationVisitor.getSourceDocument(),null);
                    textEdit.apply(instrumentationVisitor.getSourceDocument());
                    FileUtils.write(new File(filePath),instrumentationVisitor.getSourceDocument().get());
                } catch (BadLocationException e) {
                    System.out.println("Error while adding import statement " + filePath);
                } catch (IOException e) {
                    System.out.println("Error while adding import statement " + filePath);
                }

            }
            System.out.println(filePathList.size() + " Files parsed and instrumented");
        } catch (ExecutionException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        } catch (BadLocationException e) {
            System.out.println(e);
        }
    }
}

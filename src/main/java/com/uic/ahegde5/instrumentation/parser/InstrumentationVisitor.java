package com.uic.ahegde5.instrumentation.parser;


import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jface.text.Document;

import java.util.*;

public class InstrumentationVisitor extends ASTVisitor {

    List<String> methods = new ArrayList<>();

    List<SimpleName> simpleNames = new ArrayList<>();

    private CompilationUnit compilationUnit;

    private String filePath;

    private Document sourceDocument;

    private ASTRewrite astRewrite;

    private List<String> ignoredSimpleNames = Arrays.asList(new String[]{"print", "length","println", "comparison", "negated", "start", "end","buffer","errorIfNoSemiColon","semiColonRequired","strategy","wrappedFactory","result","messagePrefix","useOwner","parameterizedTypeArguments","newCodePoint","result"});

    private String className;

    public InstrumentationVisitor() {
    }

    public InstrumentationVisitor(CompilationUnit compilationUnit, String filePath, List<SimpleName> simpleNames, Document sourceDocument, ASTRewrite astRewrite) {
        this.compilationUnit = compilationUnit;
        this.filePath = filePath;
        this.simpleNames = simpleNames;
        this.sourceDocument = sourceDocument;
        this.astRewrite = astRewrite;
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    public void setCompilationUnit(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    public String getFilePath() {
        return filePath;
    }

    public List<SimpleName> getSimpleNames() {
        return simpleNames;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Document getSourceDocument() {
        return sourceDocument;
    }

    public void setSourceDocument(Document sourceDocument) {
        this.sourceDocument = sourceDocument;
    }

    public ASTRewrite getAstRewrite() {
        return astRewrite;
    }

    public void setAstRewrite(ASTRewrite astRewrite) {
        this.astRewrite = astRewrite;
    }

    @Override
    public boolean visit(MethodDeclaration method) {
        className = getClassName();
        methods.add(method.getName().getIdentifier());
        return super.visit(method);

    }

    @Override
    public boolean visit(MethodInvocation method) {
        methods.add(method.getName().getIdentifier());
        return super.visit(method);
    }

    private String getClassName() {
        return filePath.substring(filePath.lastIndexOf("\\")+1,filePath.lastIndexOf("."));
    }

    @Override
    public boolean visit(VariableDeclarationStatement variable) {
        visitStatement(variable);
        return super.visit(variable);
    }

    @Override
    public boolean visit(IfStatement ifStatement) {
        visitStatement(ifStatement);
        return super.visit(ifStatement);
    }

    @Override
    public boolean visit(WhileStatement whileStatement) {

        visitStatement(whileStatement);
        return super.visit(whileStatement);
    }

    @Override
    public boolean visit(ForStatement forStatement) {
        visitStatement(forStatement);
        return super.visit(forStatement);
    }

    @Override
    public boolean visit(EnhancedForStatement enhancedForStatement) {
        visitStatement(enhancedForStatement);
        return super.visit(enhancedForStatement);
    }

    @Override
    public boolean visit(ReturnStatement returnStatement) {
        visitStatement(returnStatement);
        return super.visit(returnStatement);
    }

    /*@Override
    public boolean visit(SwitchCase switchCase) {
        visitStatement(switchCase);
        return super.visit(switchCase);
    }*/

    @Override
    public boolean visit(SwitchStatement switchStatement) {
        visitStatement(switchStatement);
        return super.visit(switchStatement);
    }

    @Override
    public boolean visit(TypeDeclarationStatement typeDeclarationStatement) {

        System.out.println("Type declaration statement >>" + typeDeclarationStatement.getDeclaration().toString());
        return super.visit(typeDeclarationStatement);
    }

    @Override
    public boolean visit(ExpressionStatement expressionStatement) {
        //System.out.println("Expression statement >> " + expressionStatement.toString());
        //visitStatement(expressionStatement);
        return super.visit(expressionStatement);
    }

    public void visitStatement(Statement statement) {
        AST ast = compilationUnit.getAST();

        TextElement textElement = ast.newTextElement();
        int lineNo = compilationUnit.getLineNumber(statement.getStartPosition());
        String type = null;
        Block block = null;
        if (statement instanceof IfStatement) {
            IfStatement ifStatement = (IfStatement) statement;
            Statement thenStatement = ifStatement.getThenStatement();
            if(!(thenStatement instanceof ReturnStatement))
                block = (Block) ifStatement.getThenStatement();
            type = "If";
        } else if (statement instanceof WhileStatement) {
            WhileStatement whileStatement = (WhileStatement) statement;
            block = (Block) whileStatement.getBody();
            type = "While";
        } else if (statement instanceof ForStatement) {
            ForStatement forStatement = (ForStatement) statement;
            block = (Block) forStatement.getBody();
            type = "For";
        } else if (statement instanceof EnhancedForStatement) {
            EnhancedForStatement enhancedForStatement = (EnhancedForStatement) statement;
            block = (Block) enhancedForStatement.getBody();
            type = "Enhanced For";
        } else if (statement instanceof ReturnStatement) {
            type = "Return";
        } else if (statement instanceof SwitchStatement) {
            type = "Switch";
        } /*else if (statement instanceof SwitchCase) {
            type = "Switch Case";
        }*/ else if (statement instanceof VariableDeclarationStatement) {
            List<VariableDeclarationFragment> fragments = ((VariableDeclarationStatement) statement).fragments();
            /*for(VariableDeclarationFragment fragment : fragments){
                System.out.println("Line " + lineNo + "Variable declaration >>" + fragment.getName() + " value "+ fragment.getAST().hasResolvedBindings());
            }*/

            type = "Assign";
        } /*else if (statement instanceof ExpressionStatement) {
            type = "Expression";
        }*/
        String s = "TemplateClass.instrum(" + lineNo + ", \"" + type + "\"";

        statement.accept(new ASTVisitor() {
            @Override
            public boolean visit(SimpleName simpleName) {
                if(!ignoredSimpleNames.contains(simpleName.getIdentifier()) && !Character.isUpperCase(simpleName.getIdentifier().charAt(0)))
                    simpleNames.add(simpleName);
                return super.visit(simpleName);
            }
        });
        Set<String> simpleNameSet = new HashSet<>();
        //MethodDeclaration parentMethod = getParentMethod(statement.getParent());
        for (SimpleName simpleName : simpleNames) {
            if(simpleName.getParent().getNodeType() == ASTNode.METHOD_INVOCATION || simpleName.getParent().getNodeType() == ASTNode.METHOD_DECLARATION){
                continue;
            }
            int lineNoOfElement = compilationUnit.getLineNumber(simpleName.getStartPosition());

            if (lineNoOfElement == lineNo && !simpleNameSet.contains(simpleName.getIdentifier()) && !methods.contains(simpleName.getIdentifier()) && !simpleName.getIdentifier().equals(className)) {
                simpleNameSet.add(simpleName.getIdentifier());
                s += ",new PairClass(\"" + className + "." + simpleName.getFullyQualifiedName() + "\",TemplateClass.valueOf(" + simpleName.getIdentifier() + "))";
            }
        }
        s += ");";
        textElement.setText(s);


        ListRewrite listRewrite = null;

        if(type.equalsIgnoreCase("If") || type.equalsIgnoreCase("While") || type.equalsIgnoreCase("For") || type.equalsIgnoreCase("Enhanced For")){

            textElement.setText(s);
            if(null != block) {
                listRewrite = astRewrite.getListRewrite(block, Block.STATEMENTS_PROPERTY);
                listRewrite.insertFirst(textElement, null);
            } else {
                listRewrite = astRewrite.getListRewrite(statement.getParent(), Block.STATEMENTS_PROPERTY);
                listRewrite.insertBefore(textElement, statement,null);
            }

        } else{
            ASTNode parentNode = statement.getParent();
            if(parentNode.getNodeType() == ASTNode.SWITCH_STATEMENT){
                listRewrite = astRewrite.getListRewrite(parentNode, SwitchStatement.STATEMENTS_PROPERTY);
            } else {
                listRewrite = astRewrite.getListRewrite(parentNode, Block.STATEMENTS_PROPERTY);
            }

            if(type.equals("Assign")){
                listRewrite.insertAfter(textElement, statement, null);

            } else {
                //System.out.println(parentNode);
                listRewrite.insertBefore(textElement, statement, null);
            }
        }


    }

    /*private MethodDeclaration getParentMethod(ASTNode node) {
        if(node instanceof MethodDeclaration){
            return (MethodDeclaration) node;
        }
        return getParentMethod(node.getParent());

    }*/

    public void addImport(String importStatement){

        AST ast = compilationUnit.getAST();
        ImportDeclaration importDeclaration = ast.newImportDeclaration();
        importDeclaration.setName(ast.newName(importStatement));
        //List imports = compilationUnit.imports();
        //imports.add(importDeclaration);
        compilationUnit.imports().add(importDeclaration);


    }


    public List<String> getMethods() {
        return methods;
    }

}

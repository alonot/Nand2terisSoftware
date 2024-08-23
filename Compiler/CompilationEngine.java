import java.io.File;


public class CompilationEngine {
    private String nextToken;
    private JackTokenizer tokenizer;
    private String className,functionName;
    private String currentIdentifier;
    private SymbolTable classSymTable;
    private SymbolTable subSysTable;
    private VMWriter vmWriter;
    private String segment,type;
    private int if_count,while_count;

    private String op="+-*/|=";
    private String functionType;

    public CompilationEngine(File output_file,JackTokenizer Jtokenizer) throws Exception{
        // try{
            className=functionName=segment=type=nextToken=functionType="";
            tokenizer=Jtokenizer;
            if_count=while_count=0;
            classSymTable=new SymbolTable();
            subSysTable=new SymbolTable();
            vmWriter=new VMWriter(output_file);
            System.out.println("Class name \t : "+output_file.getName());
            complileClass();
            vmWriter.close();
        // }catch (Exception e){
        //     System.err.println(e.getStackTrace());
        //     System.out.println(e.getMessage());
        // }   
    }

    private void getnextLine() throws Exception{
        nextToken=tokenizer.getCode();
        // System.out.println(nextToken);
    }

    private String getTokenValue(){
        return nextToken.substring(nextToken.indexOf('>')+2,nextToken.lastIndexOf('<')-1);
    }

    private String getTokenName(){
        return nextToken.substring(1,nextToken.indexOf('>')).trim();
    }

    private boolean checkToken(String syntax,String type) throws Exception{
        String token=getTokenValue();
        if(token.equals(syntax) && getTokenName().equals(type)){
            getnextLine();
            return true;
        }
        else{ 
            System.out.println(syntax+":"+type+":>"+token+":"+getTokenName()+":");
            throw new Exception("Syntax Error");}
    }


    private void checkIdentifier(){
        
        String tokenType=getTokenName();
        try {
            if(tokenType.equals("identifier")){
                    currentIdentifier=getTokenValue();
                    getnextLine();
                }
        else{ 
            throw new Exception("Syntax Error");}
        } catch (Exception e) {
            System.err.println(e.getStackTrace());
            System.out.println(e.getMessage());
        }
    }



    private void complileClass() throws Exception{
        getnextLine();
        checkToken("class","keyword");
        checkIdentifier();
        className=currentIdentifier;
        checkToken("{","symbol");
        while (getTokenName().equals("keyword") && ( getTokenValue().equals("static") || getTokenValue().equals("field"))) {
            complileClassVarDec();

        }
        while (getTokenName().equals("keyword") && ("constructor,function,method".contains(getTokenValue()))) {
            compileSubroutine();
        }
        checkToken("}","symbol");
    }
    
    private void complileClassVarDec()throws Exception{
        segment=getTokenValue();
        getnextLine();
        String tokenName=getTokenName(),tokenValue=getTokenValue();
        if((tokenName.equals("keyword") && ("int,char,boolean".contains(tokenValue)))){
            type=tokenValue;
            getnextLine();
        }else{
            checkIdentifier();
            type=currentIdentifier;
        }
        checkIdentifier();
        classSymTable.define(currentIdentifier, type, segment);
        while (getTokenName().equals("symbol") && getTokenValue().equals(",")) {
            getnextLine();
            checkIdentifier();
            classSymTable.define(currentIdentifier, type, segment);
        }
        checkToken(";","symbol");
    }
    
    private void compileSubroutine() throws Exception{
        subSysTable.reset();
        functionType=getTokenValue();
        getnextLine();
        if(getTokenName().equals("keyword") && ("int,char,boolean,void".contains(getTokenValue()))){
            getnextLine();
        }else{
            checkIdentifier();
        }
        checkIdentifier();
        functionName=currentIdentifier;
        checkToken("(", "symbol");
        compileParameterList();
        checkToken(")", "symbol");
        compileSubroutineBody();
    }
    
    private void compileParameterList() throws Exception{

        if("keyword,identifier".contains(getTokenName())){
            do{ 
                if(("int,char,boolean".contains(getTokenValue()))){
                    type=getTokenValue();
                    getnextLine();
                }else{
                    checkIdentifier();
                    type=currentIdentifier;
                }
                checkIdentifier();
                subSysTable.define(currentIdentifier, type, "arg");
                if(getTokenName().equals("symbol") && getTokenValue().equals(","))
                    getnextLine();
                else
                    break;
            }while(true);
        }
    }
   
    private void compileSubroutineBody() throws Exception{
        checkToken("{", "symbol");
        while(getTokenName().equals("keyword") && getTokenValue().equals("var"))
            compileVarDec();
        vmWriter.writeFunction(className+"."+functionName, subSysTable.varCount("local"));
        if(functionType.equals("method")){
            vmWriter.writePush("argument", 0);
            vmWriter.writePop("pointer", 0);
            subSysTable.define("this", className, "arg");
        }else if(functionType.equals("constructor")){
            vmWriter.writePush("constant", classSymTable.varCount("this"));
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop("pointer", 0);
            subSysTable.define("this", className, "arg");
        }
        compileStatements();
        checkToken("}", "symbol");
    }
   
    private void compileVarDec() throws Exception{
        segment="var";
        getnextLine();//
        String tokenName=getTokenName(),tokenValue=getTokenValue();
        if((tokenName.equals("keyword") && ("int,char,boolean".contains(tokenValue)))){
            type=tokenValue;
            getnextLine();
        }else{
            checkIdentifier();
            type=currentIdentifier;
        }
        checkIdentifier();
        subSysTable.define(currentIdentifier, type, segment);
        while (getTokenName().equals("symbol") && getTokenValue().equals(",")) {
            getnextLine();
            checkIdentifier();
            subSysTable.define(currentIdentifier, type, segment);
        }
        checkToken(";","symbol");
    }
    
    private void compileStatements()throws Exception{
        while(getTokenName().equals("keyword") && ("let,if,while,do,return".contains(getTokenValue()))){
            switch (getTokenValue()) {
                case "let":
                    compileLet();break;
                case "if":
                    compileIf();break;
                case "while":
                    compileWhile();break;
                case "do":
                    compileDo();break;
                case "return":
                    compileReturn();break;
                default:
                    getnextLine();
                    break;
            }
        }
    }

    private void compileLet()throws Exception{
        String var="",segment="";int index=0;boolean isArray=false;
        getnextLine();
        checkIdentifier();
        var=currentIdentifier;
        if(subSysTable.typeOf(var)!= null){
            segment=subSysTable.kindOf(var);
            index=subSysTable.indexOf(var);
        }else if(classSymTable.typeOf(var)!=null){
            segment=classSymTable.kindOf(var);
            index=classSymTable.indexOf(var);
        }else{
            throw new Exception("Variable not defined.");
        }
        if(getTokenName().equals("symbol") && getTokenValue().equals("[")){
            getnextLine();
            isArray=true;
            compileExpression();
            checkToken("]", "symbol");
        }
        if(isArray){
            vmWriter.writePush(segment, index);
            vmWriter.writeArithmetic("+");
            vmWriter.writePop("temp", 0);
        }
        checkToken("=", "symbol");
        compileExpression();
        checkToken(";", "symbol");
        if(isArray){
            vmWriter.writePush("temp", 0);
            vmWriter.writePop("pointer", 1);
            vmWriter.writePop("that", 0);
        }else{
            vmWriter.writePop(segment, index);
        }
    }
    
    private void compileIf()throws Exception{
        int currentIfCount=if_count++;
        getnextLine();
        checkToken("(", "symbol");
        compileExpression();
        checkToken(")", "symbol");
        vmWriter.writeIf("TRUE_$"+(currentIfCount));
        vmWriter.writeGoto("FALSE_$"+(currentIfCount));
        vmWriter.writeLabel("TRUE_$"+(currentIfCount));
        checkToken("{", "symbol");
        compileStatements();
        checkToken("}", "symbol");
        vmWriter.writeGoto("CONTINUE_$"+(currentIfCount));
        vmWriter.writeLabel("FALSE_$"+(currentIfCount));
        if(getTokenName().equals("keyword") && getTokenValue().equals("else")){
            getnextLine();
            checkToken("{", "symbol");
            compileStatements();
            checkToken("}", "symbol");    
        }
        vmWriter.writeLabel("CONTINUE_$"+(currentIfCount));
    }
   
    private void compileWhile()throws Exception{
        int currentwhlCount=while_count++;
        getnextLine();
        vmWriter.writeLabel("WHILE_$"+currentwhlCount);
        checkToken("(", "symbol");
        compileExpression();
        checkToken(")", "symbol");
        vmWriter.writeIf("WHILETRUE_$"+currentwhlCount);
        vmWriter.writeGoto("WHILEEND_$"+currentwhlCount);
        vmWriter.writeLabel("WHILETRUE_$"+currentwhlCount);
        checkToken("{", "symbol");
        compileStatements();
        checkToken("}", "symbol");
        vmWriter.writeGoto("WHILE_$"+currentwhlCount);
        vmWriter.writeLabel("WHILEEND_$"+(currentwhlCount));
    }
    
    private void compileDo()throws Exception{
        getnextLine();
        String var="",varType="",funcName="";

        // Subroutine Call
        checkIdentifier();
        var=currentIdentifier;
        if(subSysTable.typeOf(var)!= null){
            vmWriter.writePush(subSysTable.kindOf(var), subSysTable.indexOf(var));
            varType=subSysTable.typeOf(var);
        }else if(classSymTable.typeOf(var)!=null){
            vmWriter.writePush(classSymTable.kindOf(var),classSymTable.indexOf(var));
            varType=classSymTable.typeOf(var);
        }
        if(getTokenValue().equals(".")){
            getnextLine();
            checkIdentifier();
            funcName=currentIdentifier;
        }

        checkToken("(", "symbol");
        int nArgs=compileExpressionList();
        checkToken(")", "symbol");
        
        if(funcName.equals("")){
            vmWriter.writePush("pointer", 0);
            vmWriter.writeCall(subSysTable.typeOf("this")+"."+var, nArgs+1);
        }else{
            if(!varType.equals("")){
                vmWriter.writeCall(varType+"."+funcName, nArgs+1);
            }else{
                vmWriter.writeCall(var+"."+funcName, nArgs);
            }
        }
        // Ends

        checkToken(";", "symbol");
        vmWriter.writePop("temp", 0);
    }
    
    private void compileReturn()throws Exception{
        getnextLine();
        if(!(getTokenValue().equals(";")&& getTokenName().equals("symbol"))){
            compileExpression();
        }else{
            vmWriter.writePush("constant", 0);
        }
        vmWriter.writeReturn();
        checkToken(";", "symbol");   
    }
    
    private void compileExpression()throws Exception{
        String operator="";
        compileTerm();
        while(op.contains(getTokenValue()) || getTokenValue().equals("&lt;") || getTokenValue().equals("&gt;") || getTokenValue().equals("&amp;")){
            operator=getTokenValue();
            getnextLine();
            compileTerm();
            vmWriter.writeArithmetic(operator);
        }
    }
    
    private void compileTerm()throws Exception{
        String var="",segment="",typeOf="",funcName="";int index=0;
        switch (getTokenName()) {
            case "integerConstant":int value=Integer.parseInt(getTokenValue());
                vmWriter.writePush("constant",Math.abs(value));
                if(value<0)
                    vmWriter.writeArithmetic("neg");
                getnextLine();break;
            case "stringConstant":
            String theString=getTokenValue();
            vmWriter.writePush("constant", theString.length());
            vmWriter.writeCall("String.new", 1);
            for (int i=0;i<theString.length();i++) {
                vmWriter.writePush("constant", (int)(theString.charAt(i)));
                vmWriter.writeCall("String.appendChar", 2);
            }
            getnextLine();
                break;
            case "keyword":
                if("true,false,null,this".contains(getTokenValue())){
                    switch (getTokenValue()) {
                        case "true":vmWriter.writePush("constant", 1);vmWriter.writeArithmetic("neg");break;
                        case "null":
                        case "false":vmWriter.writePush("constant",0);break;
                        case "this":vmWriter.writePush("pointer", 0);break;
                        default:
                            break;
                    }
                    getnextLine();
                }break;
            case "identifier":
                checkIdentifier();
                var=currentIdentifier;
                if(subSysTable.typeOf(var)!= null){
                    segment=subSysTable.kindOf(var);
                    index=subSysTable.indexOf(var);
                    typeOf=subSysTable.typeOf(var);
                }else if(classSymTable.typeOf(var)!=null){
                    segment=classSymTable.kindOf(var);
                    index=classSymTable.indexOf(var);
                    typeOf=classSymTable.typeOf(var);
                }
                // System.out.println(var+">"+segment+"t"+index);
                if(getTokenValue().equals("[")){
                    getnextLine();
                    compileExpression();
                    checkToken("]", "symbol");
                    vmWriter.writePush(segment, index);
                    vmWriter.writeArithmetic("+");
                    vmWriter.writePop("pointer", 1);
                    vmWriter.writePush("that", 0);
                }else{
                    if(getTokenValue().equals(".")){
                        getnextLine();
                        checkIdentifier();
                        funcName=currentIdentifier;
                    }
                    if(getTokenValue().equals("(")){
                        checkToken("(", "symbol");
                        int nArgs=compileExpressionList();
                        checkToken(")", "symbol");

                        if(funcName.equals("")){
                            vmWriter.writePush("argument", 0);
                            vmWriter.writeCall(subSysTable.typeOf("this")+"."+var, nArgs+1);
                        }else{
                            if(!typeOf.equals("")){
                                vmWriter.writePush(segment, index);
                                vmWriter.writeCall(typeOf+"."+funcName, nArgs+1);
                            }else{
                                vmWriter.writeCall(var+"."+funcName, nArgs);
                            }
                        }
                    }else if(!segment.equals("")){
                        vmWriter.writePush(segment, index);
                    }else{
                        throw new Exception("Variable/function not found");
                    }
                }
                break;
            case "symbol":
                String operator="";
                switch (getTokenValue()) {
                    case "-":
                    case "~":
                        operator=getTokenValue();
                        getnextLine();
                        compileTerm();
                        if(operator.equals("-")){
                            vmWriter.writeArithmetic("neg");
                        }else{
                            vmWriter.writeArithmetic("not");
                        }
                        break;
                    case "(":
                        getnextLine();
                        compileExpression();
                        checkToken(")", "symbol");
                    default:
                        break;
                }
                break;
            default:
                throw new Exception("Unhandled Exception.");
        }
    }
    
    private int compileExpressionList()throws Exception{
        int noExp=0;
        if(!(getTokenName().equals("symbol") && getTokenValue().equals(")"))){
            compileExpression();
            noExp++;
            while (getTokenName().equals("symbol") && getTokenValue().equals(",")) {
                noExp++;
                getnextLine();
                compileExpression();
            }
        }
        return noExp;
    }

}

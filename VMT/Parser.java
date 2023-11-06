package VMT;

import java.io.File;
import java.util.Scanner;

public class Parser {
    private File file;
    private WriteCode coder;
    private Scanner Sc;
    private String nextcommand;

    enum commandTypes{
        C_ARITHMETIC,
        C_PUSH,
        C_POP,
        C_LABEL,
        C_GOTO,
        C_IF,
        C_FUNCTION,
        C_RETURN,
        C_CALL
    };
    public Parser(File ifile,WriteCode coderr){
        file=ifile;
        coder=coderr;
        coder.setFileName(ifile.getName().substring(0, ifile.getName().indexOf(".")));
        try{
            nextcommand="";
        Sc=new Scanner(file);}
        catch(Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private boolean hasMoreLines(){
        return Sc.hasNextLine();
    }

    private void advance(){
        while(hasMoreLines()){
            String newLine=Sc.nextLine().trim();
            if(newLine.isEmpty() || newLine.charAt(0)=='/' ){
                continue;
            }
            else{
                newLine=newLine+"/";
                nextcommand=newLine.substring(0, newLine.indexOf("/")).trim();break;
            }
        }

    }

    private commandTypes commandType(){
        if(nextcommand.startsWith("push")){
            return commandTypes.C_PUSH;
        }else if (nextcommand.startsWith("pop")){
            return commandTypes.C_POP;
        }else if (nextcommand.startsWith("label")){
            return commandTypes.C_LABEL;
        }else if (nextcommand.startsWith("call")){
            return commandTypes.C_CALL;
        }else if (nextcommand.startsWith("function")){
            return commandTypes.C_FUNCTION;
        }else if (nextcommand.startsWith("if")){
            return commandTypes.C_IF;
        }else if (nextcommand.startsWith("goto")){
            return commandTypes.C_GOTO;
        }else if (nextcommand.startsWith("return")){
            return commandTypes.C_RETURN;
        }else{
            return commandTypes.C_ARITHMETIC;
        }

    }


    private String getarg1(){
        int space1=nextcommand.indexOf(' ');
        int space2=nextcommand.indexOf(' ',space1+1);
        if(space2!=-1){
            return nextcommand.substring(space1+1,space2 );
        }else if(space1!=-1){
            return nextcommand.substring(space1+1);
        }
        return nextcommand;
    }

    private int getarg2(){
        return Integer.parseInt(nextcommand.substring(nextcommand.lastIndexOf(' ')).trim());
    }


    String getCode(){
        String code="";
        while(hasMoreLines()){
            advance();
            System.out.println(nextcommand);
            commandTypes comType=commandType();
            String arg1="";
            if(comType!=commandTypes.C_RETURN)
                arg1=getarg1();
            int arg2=-999;
            switch(comType){
                case C_PUSH:case C_POP:case C_FUNCTION:case C_CALL:
                arg2=getarg2();break;
                default:break;
            }
            switch(comType){
                case C_POP:case C_PUSH:
                    coder.writePushPop(comType, arg1, arg2);break;

                case C_ARITHMETIC:
                    coder.writeArithmetic(arg1);break;

                case C_CALL:
                    coder.writeCall(arg1, arg2);break;

                case C_FUNCTION:
                    coder.writeFunction(arg1, arg2);break;

                case C_GOTO:
                    coder.writeGoto(arg1);break;

                case C_IF:
                    coder.writeif(arg1);break;

                case C_LABEL:
                    coder.writeLabel(arg1);break;

                case C_RETURN:
                    coder.writeReturn();break;

                default:
            }
        }
        
        code+="(END)\n@END\n0;JMP\n";
        return code;
    }

}

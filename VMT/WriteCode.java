package VMT;

import VMT.Parser.commandTypes;
import java.io.File;
import java.io.FileWriter;


public class WriteCode {
    private String fileName="";
    private FileWriter outputFile;
    private int count=0;
    private String function_Name="";
    private int call_count=0;

    WriteCode(File output_file){
        try{
            outputFile=new FileWriter(output_file);
            String output="";

            //To write Bootstrap code
            output+=("@256\nD=A\n@SP\nM=D\n");

            output+=String.format("@%s\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n","Sys.init$ret");
            output+=String.format("@%s\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n","LCL");
            output+=String.format("@%s\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n","ARG");
            output+=String.format("@%s\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n","THIS");
            output+=String.format("@%s\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n","THAT");
            output+=String.format("@SP\nD=M\n@%d\nD=D-A\n@ARG\nM=D\n",(5));
            output+="@SP\nD=M\n@LCL\nM=D\n";
            output+="@Sys.init\n0;JMP\n";
            output+="(Sys.init$ret)\n";

            outputFile.write(output);


        }catch (Exception e){
            System.out.println(e.getMessage());
        }


        
    }

    public void setFileName(String name){
        this.fileName=name;
    } 

    public void close(){
        try{outputFile.close();}
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
   

    public void writePushPop(commandTypes command,String segment,int index){
        String output="";String seg="";

        switch (segment) {
            case "local" -> seg = "LCL";
            case "argument" -> seg = "ARG";
            case "this" -> seg = "THIS";
            case "that" -> seg = "THAT";
            case "pointer" -> {
                seg = (index == 0) ? "THIS" : "THAT";
                index = 0;
            }
            case "constant" -> seg = segment;
            case "static" -> seg = fileName + "." + index;
            case "temp" -> seg = (5 + (int) (index)) + "";
            default -> seg = "";
        }
        if(!seg.isEmpty()){
            switch(command){
                case C_PUSH:
                    output = switch (segment) {
                        case "constant" -> String.format("@%d\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n", index);
                        case "temp", "pointer", "static" -> String.format("@%s\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n", seg);
                        default -> String.format("@%s\nD=M\n@%d\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n", seg, index);
                    };
                break;
                case C_POP:
                    output = switch (segment) {
                        case "temp", "pointer", "static" -> String.format("@SP\nAM=M-1\nD=M\n@%s\nM=D\n", seg);
                        default ->
                                String.format("@%s\nD=M\n@%d\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n", seg, index);
                    };
                    default:
            }
        }
        try{
        outputFile.write(output);}
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void writeArithmetic(String command){
        String output="",operator="",condop="";
        block:{
        switch(command){
            case "add":operator="+";break;
            case "sub":operator="-";break;
            case "and":operator="&";break;
            case "or":operator="|";break;
            case "not": output="@SP\nAM=M-1\nM=!M\n@SP\nM=M+1\n";break block;
            case "neg": output="@SP\nAM=M-1\nM=-M\n@SP\nM=M+1\n";break block;
            case "eq":condop="EQ";break;
            case "gt":condop="GT";break;
            case "lt":condop="LT";break;
            default:
        }
        if(!condop.isEmpty()){
            output=String.format("@SP\nAM=M-1\nD=M\n@SP\nAM=M-1\nD=M-D\n@TRUE%d\n"
            +"D;J%s\n@SP\nA=M\nM=0\n@CONTINUE%d\n0;JMP\n(TRUE%d)\n@SP\nA=M\nM=-1\n"
            +"(CONTINUE%d)\n@SP\nM=M+1\n",count,condop,count,count,count);
            count++;
        }else{
        output=String.format("@SP\nAM=M-1\nD=M\n@SP\nAM=M-1\nD=M%sD\n@SP\nA=M\nM=D\n@SP\nM=M+1\n", operator);
        }
        }
        try{
        outputFile.write(output);}
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
   
    public void writeGoto(String label){
        String output="";

        output=String.format("@%s$%s\n0;JMP\n",function_Name,label );

        try{
        outputFile.write(output);}
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void writeLabel(String label){
        String output="";
        System.out.println(label);
        //04912566321
        output=String.format("(%s$%s)\n",function_Name,label);

        try{
        outputFile.write(output);}
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void writeif(String label){
        String output="";

        output=String.format("@SP\nAM=M-1\nD=M\n@%s$%s\nD;JNE\n",function_Name,label );

        try{
        outputFile.write(output);}
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void writeFunction(String functionName,int nVar){
        StringBuilder output= new StringBuilder();
        System.out.println(functionName);
        function_Name=functionName;

        output = new StringBuilder(String.format("(%s)\n", this.function_Name));
        output.append("@SP\nM=M+1\nA=M-1\nM=0\n".repeat(Math.max(0, nVar)));

        try{
        outputFile.write(output.toString());}
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void writeCall(String functionName,int nArgs){
        String output="";

        output=String.format("@%s\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n",this.function_Name+"$ret."+call_count);
        output+=String.format("@%s\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n","LCL");
        output+=String.format("@%s\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n","ARG");
        output+=String.format("@%s\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n","THIS");
        output+=String.format("@%s\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n","THAT");
        output+=String.format("@SP\nD=M\n@%d\nD=D-A\n@ARG\nM=D\n",(nArgs+5));
        output+="@SP\nD=M\n@LCL\nM=D\n";
        output+=String.format("@%s\n0;JMP\n",functionName);
        output+=String.format("(%s$ret.%s)\n",this.function_Name,call_count);
    

        call_count++;
        try{
        outputFile.write(output);}
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void writeReturn(){
        String output="";

        output+="@LCL\nD=M\n@5\nA=D-A\nD=M\n@R13\nM=D\n";
        output+="@ARG\nD=M\n@R14\nM=D\n@SP\nAM=M-1\nD=M\n@R14\nA=M\nM=D\n";
        output+="@ARG\nD=M\n@SP\nM=D+1\n";
        output+="@LCL\nA=M-1\nD=M\n@THAT\nM=D\n";
        output+="@LCL\nD=M\n@2\nA=D-A\nD=M\n@THIS\nM=D\n";
        output+="@LCL\nD=M\n@3\nA=D-A\nD=M\n@ARG\nM=D\n";
        output+="@LCL\nD=M\n@4\nA=D-A\nD=M\n@LCL\nM=D\n";
        output+="@R13\nA=M\n0;JMP\n";

        call_count=0;

        try{
        outputFile.write(output);}
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

}

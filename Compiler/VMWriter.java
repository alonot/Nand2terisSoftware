import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class VMWriter {

    FileWriter outFile;
    public VMWriter(File out_file) throws IOException{
        outFile=new FileWriter(out_file);
    }

    public void writePush(String segment,int index) throws IOException{
        outFile.write("push "+segment+" "+index+"\n");
    }
    public void writePop(String segment, int index) throws IOException{
        outFile.write("pop "+segment+" "+index+"\n");
    }
    public void writeArithmetic (String command) throws IOException{
        String comm="";
        switch (command) {
            case "&lt;":
            case "&gt;":comm=command.substring(1,command.length()-1);
                break;
            case "&amp;":comm="and";
            break;
            case "+":comm="add";break;
            case "-":comm="sub";break;
            case "|":comm="or";break;
            case "=":comm="eq";break;
            case "*":writeCall("Math.multiply", 2);return;
            case "/":writeCall("Math.divide", 2);return;
            default:comm=command;
        }
        outFile.write(comm+"\n");
    }
    public void writeLabel(String label) throws IOException{
        outFile.write("label "+label+"\n");
    }
    public void writeGoto(String label) throws IOException{
        outFile.write("goto "+label+"\n");
    }
    public void writeIf(String label) throws IOException{
        outFile.write("if-goto "+label+"\n");
    }
    public void writeCall(String name, int nArgs) throws IOException{
        outFile.write("call "+name+" "+nArgs+"\n");
    }
    public void writeFunction(String name, int nVars) throws IOException{
        outFile.write("function "+name+" "+nVars+"\n");
    }
    public void writeReturn() throws IOException{
        outFile.write("return\n");
    }
    public void close() throws IOException{
        outFile.close();
    }
    
}

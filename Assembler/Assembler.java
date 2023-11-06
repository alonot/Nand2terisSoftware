package Assembler;

import java.io.File;
import java.io.FileWriter;
public class Assembler {
    public static void main(String[] args) {
        System.out.println("Namaste");
        try {
            File file=new File(args[0]); 
            Parser parser=new Parser(file);
            String output=parser.getinstruction();
            FileWriter fw=new FileWriter(file.getPath().substring(0,file.getPath().indexOf(file.getName()))+file.getName().substring(0,file.getName().indexOf("."))+".hack");
            fw.write(output);
            fw.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } 
        
    }
}

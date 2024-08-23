import java.io.File;
import java.io.FileWriter;

public class Assembler {
    public static void main(String[] args) {
//        System.out.println("Namaste");
        try {
            File file=new File(args[0]);
            AssemblyParser parser=new AssemblyParser(file);
            String output=parser.getinstruction();
            String outputFileName = file.getAbsolutePath().replace(".asm",".hack");
            FileWriter fw=new FileWriter(outputFileName);
            fw.write(output);
            fw.close();
            System.out.println("asm compiled to -> " + outputFileName);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        } 
        
    }
}

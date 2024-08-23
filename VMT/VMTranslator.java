

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;

public class VMTranslator {
    
    public static void main(String[] args) throws FileNotFoundException{
        try {
            String input=args[0];
            ArrayList<File> files=new ArrayList<>();
            WriteCode coder;
            String outputname;
            if(input.contains(".vm")){
                files.add(new File(input));
                outputname = input.replace(".vm", ".asm");
                coder=new WriteCode(new File(outputname));
            }else{
                // files
                File filesDir=new File(input);
                for (File file : Objects.requireNonNull(filesDir.listFiles())) {
                    if(file.getPath().contains(".vm"))
                        files.add(file);
                }
                outputname=filesDir.getName();
                outputname = String.valueOf(Paths.get(input,outputname + ".asm").toAbsolutePath());
                coder=new WriteCode(new File(outputname));
            }
            for (File input_file: files) {
                Parser parser=new Parser(input_file,coder);
                parser.getCode();
            }
            coder.close();
            System.out.println("All files successfully assembled to "+outputname);
         } catch (IndexOutOfBoundsException e) {
            System.out.println("Please provide a file or a directory");

        }
        catch (Exception e) {
            System.err.println(e.getStackTrace());
            System.out.println(e.getMessage());
        }
    }
    
}

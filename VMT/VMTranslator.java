package VMT;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class VMTranslator {
    
    public static void main(String[] args) {
        try {
            String input=args[0];
            ArrayList<File> files=new ArrayList<>();
            WriteCode coder;
            if(input.contains(".vm")){
                files.add(new File(input));
                coder=new WriteCode(new File(input.replace(".vm", ".asm")));
            }else{
                // files
                File filesDir=new File(input);
                for (File file : Objects.requireNonNull(filesDir.listFiles())) {
                    if(file.getPath().contains(".vm"))
                        files.add(file);
                }
                String outputname="";
                if(input.lastIndexOf("/")==-1)
                    outputname=input;
                else
                    outputname=input.substring(input.lastIndexOf("/")+1).strip();
                    
                coder=new WriteCode(new File(input+"/"+outputname+".asm"));
            }
            for (File input_file: files) {
                Parser parser=new VMT.Parser(input_file,coder);
                parser.getCode();
            }
            coder.close();
         } catch (Exception e) {
             System.out.println(e.getMessage());
         }
    }
    
}

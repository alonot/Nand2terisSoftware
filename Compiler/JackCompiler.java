import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class JackCompiler {

    public static void main(String[] args) throws Exception {
       try {

            String input=args[0];
            ArrayList<File> files=new ArrayList<>();
            CompilationEngine engine;
            if(input.contains(".jack")){
                files.add(new File(input));
                String outFileName=input.replace(".jack", ".xml");
            }else{
                // files
                try{
                File filesDir=new File(input);
                for (File file : filesDir.listFiles()) {
                    if(file.getPath().contains(".jack") && (!file.getName().startsWith(".")))
                        files.add(file);
                }
            }catch(NullPointerException e){
                throw new FileNotFoundException();
            }
            }
            for (File input_file: files) {
                String outputFileName=input_file.getAbsolutePath().substring(0,input_file.getAbsolutePath().length()-5);
                JackTokenizer jackTokenizer=new JackTokenizer(input_file);
                engine=new CompilationEngine(new File(outputFileName+".vm"),jackTokenizer);
            }
            
            
        }catch (IndexOutOfBoundsException e) {
           System.out.println("Please provide a file or a directory");

       } catch (FileNotFoundException e) {
            System.err.println("Invalid Path given");
        }
        catch (Exception e) {
            System.err.println(e.getStackTrace());
            System.out.println(e.getMessage());
        }
    }

}

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class JackTokenizer {
    private File file;
    private Scanner Sc;
    private String nextToken;
    private String currentLine;

    ArrayList<String> keywords=new ArrayList<>(List.of("class","constructor","function","method","field","static","var","int","char","boolean",
    "void","true","false","null","this","let","do","if","else","while","return"));

    // ArrayList<Character> symbol=new ArrayList<>(List.of('{','}','(',')','[',']','.',',',';','+','-','*','/','&','|','<','>','~','='));
    String symbol="[]{}().,;+-*/&|~=<>";
    enum CommandType{
        KEYWORD,
        SYMBOL,
        IDENTIFIER,
        INT_CONST,
        STRING_CONST
    }

    public JackTokenizer(File ifile){
        file=ifile;
        try{
            nextToken="";
            currentLine="";
            System.out.println(file.getAbsolutePath());
            Sc=new Scanner(file);
        }
        catch(Exception e) {
            System.err.println(e);
            System.out.println(e.getMessage());
        }
    }


    private boolean hasMoreTokens(){
        if(currentLine.isEmpty())
            return Sc.hasNextLine();
        return true;
    }

    private void advance(){
        boolean multi_comments=false;
        while(hasMoreTokens()){
            if(currentLine.isEmpty()){
                currentLine=Sc.nextLine().trim();
                if(currentLine.isEmpty())
                    continue;
            }
            if(multi_comments){
                if(!currentLine.contains("*/"))
                    currentLine="";
                else{
                    currentLine=currentLine.substring(currentLine.indexOf("*/")+2);
                    multi_comments=false;
                }
                continue;
            }else{
                String result=readNextToken();
                if(result.equals("/*")){
                    multi_comments=true;
                    continue;
                }else if(result.equals("//")){
                    currentLine="";
                    continue;
                }else{
                    nextToken=result;
                    break;
                }
            }
            
        }

    }

    private String readNextToken(){
        String token="";
        boolean insideQuotes=false;
        int i=0;
        for(i =0;i<currentLine.length();i++){
            char ch=currentLine.charAt(i);
            if(Character.isLetterOrDigit(ch) || ch=='"' || insideQuotes){
                token+=ch;
                if(ch=='"')
                    insideQuotes=!insideQuotes;
            }else{
                if(token.isEmpty() && ch != ' '){
                    if(ch=='/' && i<currentLine.length()-1){
                        char next=currentLine.charAt(i+1);
                        if(next =='/' || next=='*'){
                            token=ch+""+next;
                        }else{
                            token=ch+"";
                        }
                    }else{
                        token=ch+"";
                    }
                    i++;
                }
                break;
            }
        }
        currentLine=currentLine.substring(i).trim();
        return token;
    }

    // private void advance(){
    //     boolean multi_comments=false;
    //     while(hasMoreLines()){
    //         String newLine=Sc.nextLine().trim();
    //         if(newLine.isEmpty() || newLine.startsWith("//")){
    //             multi_comments=false;
    //             continue;
    //         }
    //         else if (newLine.startsWith("/*")){
    //             multi_comments=true;
    //         }
    //         else if(newLine.endsWith("*/")){
    //             multi_comments=false;
    //         }
    //         else if(!multi_comments){
    //             newLine=newLine+"/";
    //             nextToken=newLine.substring(0, newLine.indexOf("/")).trim();break;
    //         }
    //     }

    // }

    private CommandType commandType(){
        if(nextToken.startsWith("\"")){
            return CommandType.STRING_CONST;
        }else if(keywords.contains(nextToken)){
            return CommandType.KEYWORD;
        }else if(symbol.contains(nextToken)){
            return CommandType.SYMBOL;
        }
        try {
            Integer.parseInt(nextToken);
            return CommandType.INT_CONST;
        } catch (Exception e) {
            return CommandType.IDENTIFIER;
        }
    }

    private String getKeyword(){
        return nextToken.trim();
    }

    private String getSymbol(){
        if(nextToken.equals("<"))
            return "&lt;";
        else if(nextToken.equals(">"))
            return "&gt;";
        else if(nextToken.equals("\""))
            return "&quot;";
        else if(nextToken.equals("&"))
            return "&amp;";
        return symbol.charAt(symbol.indexOf(nextToken))+"";
    }

    private String getIdentifier(){
        return nextToken.trim();
    }

    private int getIntVal(){
        return Integer.parseInt(nextToken);
    }

    private String getStringVal(){
        return nextToken.substring(1, nextToken.length()-1);
    }




    public String getCode() throws IOException{
        if(hasMoreTokens()){
            advance();
            CommandType comType=commandType();
            // System.out.println(nextToken+"\t"+comType);
            String outString="";
            switch(comType){
                case KEYWORD:
                outString="<keyword> "+getKeyword()+" </keyword>";
                break;
                case SYMBOL:
                outString="<symbol> "+getSymbol()+" </symbol>";
                break;
                case IDENTIFIER:
                outString="<identifier> "+getIdentifier()+" </identifier>";
                break;
                case INT_CONST:
                outString="<integerConstant> "+getIntVal()+" </integerConstant>";
                break;
                case STRING_CONST:
                outString="<stringConstant> "+getStringVal()+" </stringConstant>";
                break;
                default:
            }
            return outString;
        }else{
            return "";
        }
    }

}

package Assembler;

import java.io.File;
import java.util.Scanner;

public class Parser {

    private File file;
    private Scanner Sc;
    private Symbol_table symbol_table;
    private Code code_module;
    private int symbol_mem=16;
    private int instruction_line=0;

    public Parser(File in_file){
        file=in_file;
        try {
            Sc=new Scanner(file);
            symbol_table=new Symbol_table();
            code_module=new Code();
        } catch (Exception e) {
            System.out.println(e);
        }
    }


    private boolean hasnextLine(){
        return Sc.hasNextLine();
    }

    private String getSymbol(String address,char type){
        address+="/";
        
        if(type == 'A'){
            String xxx=address.substring(1,address.indexOf("/")).trim();
            if(xxx.matches(".*[A-Za-z]+.*")){
                    if(symbol_table.contains(xxx)){
                        return symbol_table.getaddress(xxx);}
                    else{
                        symbol_table.addEntry(xxx, ""+symbol_mem++);
                        return symbol_table.getaddress(xxx);
                    }
            }
            else{
                return xxx;
            }
        }
        else{
            String xxx=address.substring(1,address.indexOf(")")).trim();
            symbol_table.addEntry(xxx, ""+instruction_line);
                return symbol_table.getaddress(xxx);
        }
    }

    private String advanceLine(){
        while(hasnextLine()){
            String new_line=Sc.nextLine();
            new_line=new_line.trim();
            if(new_line.startsWith("//") || new_line.equals("")){
                continue;
            }else{
                return new_line;
            }
        }
        return "";
    }
    

    private char instruction_type(String instruct){
        if(instruct.charAt(0)=='@'){
                return 'A';
        }else if (instruct.charAt(0)=='('){
            return 'L';
        }else
            return 'C';
    }

    private String getComp(String instruct){
        instruct+="/";
        if(instruct.indexOf('=')!=-1 && instruct.indexOf(';')!=-1){
            return instruct.substring(instruct.indexOf('=')+1, instruct.indexOf(';')).trim();
        }else if(instruct.indexOf('=')!=-1){
            return instruct.substring(instruct.indexOf('=')+1,instruct.indexOf("/")).trim();
        }else if(instruct.indexOf(';')!=-1){
            return instruct.substring(0,instruct.indexOf(';'));
        }
        return "";
    }

    private String getDest(String instruct){
        if(instruct.indexOf('=')!=-1){
            return instruct.substring(0, instruct.indexOf('='));
        }
        return "";
    }

    private String getJump(String instruct){
        instruct+="/";
        if(instruct.indexOf(';')!=-1){
            return instruct.substring(instruct.indexOf(';')+1,instruct.indexOf("/")).trim();
        }
        return "";
    }

    private String put_symbol(){
        String instructions="";
         while(hasnextLine()){
            String nextLine=advanceLine();
            if(nextLine==""){
                break;
            }
            if(instruction_type(nextLine)=='A'){
                instruction_line++;
                instructions+=nextLine+"\n";
            }else if(instruction_type(nextLine)=='L'){
                getSymbol(nextLine, 'L');
            }else{
                instruction_line++;
                instructions+=nextLine+"\n";
            }            
            
        }
        return instructions;
    }

    public String getinstruction(){

        String instructions=put_symbol();

        Sc=new Scanner(instructions);
        instructions="";

        while(hasnextLine()){
            String nextLine=advanceLine();
            if(nextLine==""){
                break;
            }
            if(instruction_type(nextLine)=='C'){
                instructions+="111"+code_module.comp(getComp(nextLine))+code_module.dest(getDest(nextLine))+code_module.jump(getJump(nextLine));
                instructions+="\n";
            }else if(instruction_type(nextLine)=='A'){
                instructions+=code_module.getbinary(getSymbol(nextLine, 'A'));
                instructions+="\n";
            }
            
        }
        return instructions;
    }

    
}

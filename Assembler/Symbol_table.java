package Assembler;
import java.util.HashMap;

public class Symbol_table {
    private HashMap<String,String> symbol_table;
    Symbol_table(){
        symbol_table=new HashMap<>();
        predefine_symbols();
    }

    public void addEntry(String key,String value){
        symbol_table.put(key, value);
    }

    public String getaddress(String key){
        return symbol_table.get(key);
    }

    public boolean contains(String key){
        return symbol_table.containsKey(key);
    }

    public void predefine_symbols(){
        symbol_table.put("R0","0");
        symbol_table.put("R1","1");
        symbol_table.put("R2","2");
        symbol_table.put("R3","3");
        symbol_table.put("R4","4");
        symbol_table.put("R5","5");
        symbol_table.put("R6","6");
        symbol_table.put("R7","7");
        symbol_table.put("R8","8");
        symbol_table.put("R9","9");
        symbol_table.put("R10","10");
        symbol_table.put("R11","11");
        symbol_table.put("R12","12");
        symbol_table.put("R13","13");
        symbol_table.put("R14","14");
        symbol_table.put("R15","15");
        symbol_table.put("SCREEN","16384");
        symbol_table.put("KBD","24576");
        symbol_table.put("SP","0");
        symbol_table.put("LCL","1");
        symbol_table.put("ARG","2");
        symbol_table.put("THIS","3");
        symbol_table.put("THAT","4");
    }
}

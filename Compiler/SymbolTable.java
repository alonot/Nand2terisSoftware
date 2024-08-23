import java.util.HashMap;

public class SymbolTable {
    private HashMap<String,String> typecolmn;
    private HashMap<String,String> kindcolnm;
    private HashMap<String,Integer> indexcolnm;
    private int staticIndex,argIndex,fieldIndex,localIndex;
    public SymbolTable(){
        typecolmn=new HashMap<>();
        kindcolnm= new HashMap<>();
        indexcolnm= new HashMap<>();
        staticIndex=argIndex=fieldIndex=localIndex=0;
    } 

    public void reset(){
        staticIndex=argIndex=fieldIndex=localIndex=0;
        typecolmn.clear();
        kindcolnm.clear();
        indexcolnm.clear();
    }

    public void define(String name,String type,String kind){
        typecolmn.put(name, type);
        switch (kind) {
            case "static":
                indexcolnm.put(name, staticIndex++);
                kindcolnm.put(name, kind);
                break;
            case "var":indexcolnm.put(name, localIndex++);
                kindcolnm.put(name, "local");
                break;
            case "arg":indexcolnm.put(name, argIndex++);
                kindcolnm.put(name, "argument");
                break;
            case "field":indexcolnm.put(name, fieldIndex++);
                kindcolnm.put(name, "this");
                break;
            default:kindcolnm.put(name, kind);
                break;
        }
    }

    public int varCount(String kind){
        int varcount=0;
        for (String k : kindcolnm.values()) {
            if(k.equals(kind))
                varcount++;
        }
        return varcount;
    }

    public String kindOf(String name){
        // System.out.println(kindcolnm.get("x"));
        return kindcolnm.get(name);
    }

    public String typeOf(String name){
        return typecolmn.get(name);
    }
    public int indexOf(String name){
        if(indexcolnm.get(name) != null)
            return indexcolnm.get(name);
        return -1;
    }
}

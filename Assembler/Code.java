package Assembler;

public class Code {

    public String getbinary(String value){
        binary_val="";
        binOf(Integer.valueOf(value));
        String binary=String.format("%1$16s",binary_val).replace(' ', '0');
        if(value.equals("189"))
            System.out.println(binary);
        return binary;
    }
    private String binary_val="";

    private void binOf(int n){
        if(n!=0){
            binOf(n/2);
            binary_val+=(n%2)+"";
        }
    }
    
    public String dest(String idest){
        switch(idest){
            case "M":return "001";
            case "D":return "010";
            case "MD":
            case "DM":return "011";
            case "A":return "100";
            case "AM":
            case "MA":return "101";
            case "AD":
            case "DA":return "110";
            case "ADM":
            case "AMD":
            case "DAM":
            case "DMA":
            case "MAD":
            case "MDA":return "111";
            default: return "000";
        }
    }

    public String comp(String icomp){
        String fcomp="";
        switch(icomp){
            case "0":fcomp ="101010";break;
            case "1":fcomp ="111111";break;
            case "-1":fcomp ="111010";break;
            case "D":fcomp ="001100";break;
            case "A":
            case "M":fcomp ="110000";break;
            case "!D":fcomp ="001101";break;
            case "!A":
            case "!M":fcomp ="110001";break;
            case "-D":fcomp ="001111";break;
            case "-A":
            case "-M":fcomp ="110011";break;
            case "D+1":fcomp ="011111";break;
            case "A+1":
            case "M+1":fcomp ="110111";break;
            case "D-1":fcomp ="001110";break;
            case "A-1":
            case "M-1":fcomp ="110010";break;
            case "D+A":
            case "A+D":
            case "M+D":
            case "D+M":fcomp ="000010";break;
            case "D-A":
            case "D-M":fcomp ="010011";break;
            case "A-D":
            case "M-D":fcomp ="000111";break;
            case "D&A":
            case "A&D":
            case "A&M":
            case "D&M":fcomp ="000000";break;
            case "D|A":
            case "A|D":
            case "M|D":
            case "D|M":fcomp ="010101";break;
            default: fcomp ="000000";
        }
        if(icomp.indexOf("M")!=-1)
            return "1"+fcomp;
        else
            return "0"+fcomp;
    }

    public String jump(String ijump){
        switch(ijump){
            case "JGT":return "001";
            case "JEQ":return "010";
            case "JGE":return "011";
            case "JLT":return "100";
            case "JNE":return "101";
            case "JLE":return "110";
            case "JMP":return "111";
            default: return "000";
        }
    }
}

// PLease put a value less than 15 in temp 0 i.e RAM[5]

@Main.Main
0;JMP


(Main.count)
//push argument 0
@ARG
A=M
D=M
@SP
M=M+1
A=M-1
M=D

// push constant 1
@1
D=A
@SP
M=M+1
A=M-1
M=D

// add
@SP
AM=M-1
D=M
@SP
AM=M-1
D=D+M
@SP
M=M+1
A=M-1
M=D

@5  //temp =retAdd
D=A
@LCL
A=M-D
D=M
@R13
M=D

@SP // ARG 0= pop SP
AM=M-1
D=M
@ARG
A=M
M=D

@ARG // SP= ARG+1
D=M+1
@SP
M=D

@1 // THAT = LCL-1
D=A
@LCL
A=M-D
D=M
@THAT
M=D

@2 // THIS = LCL-2
D=A
@LCL
A=M-D
D=M
@THIS
M=D

@3 // ARG = LCL-3
D=A
@LCL
A=M-D
D=M
@ARG
M=D

@4 // LCL = LCL-4
D=A
@LCL
A=M-D
D=M
@LCL
M=D

@R13
A=M
0;JMP

// return



(Main.Main)
//2 locals-->
@SP
M=M+1
A=M-1
M=0
@SP
M=M+1
A=M-1
M=0
//

//push temp 0
@5
D=M
@SP
M=M+1
A=M-1
M=D

// pop local 0
@SP
AM=M-1
D=M
@LCL
A=M
M=D

(WHILE)
@LCL  // push local 0
A=M
D=M
@SP
M=M+1
A=M-1
M=D

@15 // push constant 15
D=A
@SP
M=M+1
A=M-1
M=D

@SP // eq
AM=M-1
D=M
@SP
AM=M-1
D=M-D
@EQT
D;JEQ
@SP
M=M+1
A=M-1
M=0
@EQCont
0;JMP
(EQT)
@SP
M=M+1
A=M-1
M=-1
(EQCont)

// if
@SP
AM=M-1
D=M
@CONTINUE
D;JNE
@TRUE$1
D;JMP
(TRUE$1)

@LCL // push local 0
A=M
D=M
@SP
M=M+1
A=M-1
M=D


// call Main.count 1
@Main.Main.CONTINUE // save retAdd
D=A
@SP
M=M+1
A=M-1
M=D
@LCL // save LCL
D=M
@SP
M=M+1
A=M-1
M=D
@ARG // save ARG
D=M
@SP
M=M+1
A=M-1
M=D
@THIS //save THIS
D=M
@SP
M=M+1
A=M-1
M=D
@THAT // save THAT
D=M
@SP
M=M+1
A=M-1
M=D

@6  // ARG= SP -5 -1
D=A
@SP 
D=M-D
@ARG
M=D

@SP
D=M
@LCL
M=D

@Main.count
0;JMP
(Main.Main.CONTINUE)



@SP // pop local 1
AM=M-1
D=M
@LCL
A=M
M=D

@WHILE // goto WHILE
0;JMP


(CONTINUE)
@LCL // sum =  local 0
A=M
D=M
@sum
M=D

(END)
@END
0;JMP



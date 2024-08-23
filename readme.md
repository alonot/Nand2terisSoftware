This is a course project we build during our third semester in course: Foundations of Computing Systems by Dr. Sandeep Chandran sir.

This repositories only features the softwares we built during the course. 
The JACK hdl circuits and Computer which we built, are not pushed here.

We made a ping pong game in JACK :  https://github.com/alonot/PingPongInJack

### To test, following steps may be followed on .Tests directory
Please also look at configurations given in .run/
#### 1. Run the compiler, this generates the .vm files
```shell
    java <path to JackCompiler.java> <path to Tests directory or specify .jack file>
```
#### 2. Run the VMTranslator, this generates 1 Tests.asm file
```shell
    java <path to VMTranslator.java> <path to Tests directory or specify .vm file>
```
#### 3. Run the Assembler, this generates the .vm files
```shell
    java <path to Assembler.java> <path to specify .asm file>
```

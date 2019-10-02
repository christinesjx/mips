/*
Jiaxin Sun
CS472
Project 3

 */

public class MEMWB implements Cloneable{

    int MemToReg = 0;
    int RegWrite = 0;
    int LWDataValue = 0;
    int ALUResult = 0;
    int WriteRegNum = 0;
    int Instr = 0;


    public MEMWB clone() throws CloneNotSupportedException{
        return (MEMWB)super.clone();

    }

    public void reset(){
        MemToReg = 0;
        RegWrite = 0;
        LWDataValue = 0;
        ALUResult = 0;
        WriteRegNum = 0;
        Instr = 0;



    }

}

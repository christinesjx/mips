/*
Jiaxin Sun
CS472
Project 3

 */

public class IDEX implements Cloneable{


    int RegDst =0;
    int ALUSrc=0;
    int ALUOp=0;
    int MemRead=0;
    int MemWrite=0;
    int Branch=0;
    int MemToReg=0;
    int RegWrite=0;
    int IncrPC=0;
    int ReadReg1Value=0;
    int ReadReg2Value=0;
    short SEOffet=0;
    int WriteReg_20_16=0;
    int WriteReg_15_11=0;
    int Function=0;
    int OpCode = 0;
    int Instr = 0;

    public IDEX clone() throws CloneNotSupportedException{

        return (IDEX)super.clone();

    }

    public void reset(){

        RegDst =0;
        ALUSrc=0;
        ALUOp=0;
        MemRead=0;
        MemWrite=0;
        Branch=0;
        MemToReg=0;
        RegWrite=0;
        IncrPC=0;
        ReadReg1Value=0;
        ReadReg2Value=0;
        SEOffet=0;
        WriteReg_20_16=0;
        WriteReg_15_11=0;
        Function=0;
        OpCode = 0;
        Instr = 0;


    }



}

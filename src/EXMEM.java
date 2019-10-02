
/*
Jiaxin Sun


 */
public class EXMEM implements Cloneable{

    int MemRead = 0;
    int MemWrite = 0;
    int Branch = 0;
    int MemToReg = 0;
    int RegWrite = 0;
    int CalcBTA = 0;
    boolean Zero = false;
    int ALUResult = 0;
    int SWValue = 0;
    int WriteRegNum = 0;
    int OpCode = 0;
    int Instr = 0;

    public EXMEM clone() throws CloneNotSupportedException{
        return (EXMEM)super.clone();

    }

    public void reset(){

        MemRead = 0;
        MemWrite = 0;
        Branch = 0;
        MemToReg = 0;
        RegWrite = 0;
        CalcBTA = 0;
        Zero = false;
        ALUResult = 0;
        SWValue = 0;
        WriteRegNum = 0;
        OpCode = 0;
        Instr = 0;
    }



}

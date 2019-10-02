
/*
Jiaxin Sun
 */


public class Stage {


    private static final int INVALID = -999;


    private IFID IFID_Read = new IFID();
    private IFID IFID_Write = new IFID();

    private IDEX IDEX_Read = new IDEX();
    private IDEX IDEX_Write = new IDEX();

    private EXMEM EXMEM_Read = new EXMEM();
    private EXMEM EXMEM_Write = new EXMEM();

    private MEMWB MEMWB_Read = new MEMWB();
    private MEMWB MEMWB_Write = new MEMWB();
    private int PC = 0x7a000;

    Initialization I = new Initialization();
    int[] Main_Mem = I.MainMemory();
    int[] Regs = I.Regs();



    public void IF_stage(int InstructionCode) {

        IFID_Write.IncrPC = IFID_Read.IncrPC + 0x4;
        IFID_Write.Inst = InstructionCode;
    }

    public void ID_stage(){

        if(IFID_Read.Inst != 0) {
            IDEX_Write.Instr = IFID_Read.Inst;

            int instruction = IFID_Read.Inst;



            IDEX_Write.WriteReg_20_16 = (instruction >>> 16) & 0b11111;
            IDEX_Write.WriteReg_15_11 = (instruction >>> 11) & 0b11111;
            IDEX_Write.Function = (instruction) & 0b111111;
            IDEX_Write.SEOffet = (short) (instruction & 0x0000ffff);
            IDEX_Write.OpCode = (instruction >>> 26) & 0b111111;

            int scr1reg = (instruction >>> 21) & 0b11111;
            IDEX_Write.ReadReg1Value = Regs[scr1reg];
            IDEX_Write.ReadReg2Value = Regs[IDEX_Write.WriteReg_20_16];
            IDEX_Write.IncrPC = IFID_Read.IncrPC;


            if (IDEX_Write.OpCode == 0) {

                IDEX_Write.RegDst = 1;
                IDEX_Write.ALUOp = 10;
                IDEX_Write.RegWrite = 1;
                IDEX_Write.SEOffet = INVALID;

            } else {
                IDEX_Write.Function = INVALID;

                if (IDEX_Write.OpCode == 0x20) {
                    //lw
                    IDEX_Write.ALUSrc = 1;
                    IDEX_Write.MemRead = 1;
                    IDEX_Write.MemToReg = 1;
                    IDEX_Write.RegWrite = 1;

                }

                if (IDEX_Write.OpCode == 0x28) {
                    //sw
                    IDEX_Write.RegDst = INVALID;
                    IDEX_Write.ALUSrc = 1;
                    IDEX_Write.MemWrite = 1;
                    IDEX_Write.MemToReg = INVALID;
                }


                if (IDEX_Write.OpCode == 0x5 || IDEX_Write.OpCode == 0x4) {
                    //branch
                    IDEX_Write.RegDst = INVALID;
                    IDEX_Write.ALUOp = 1;
                    IDEX_Write.Branch = 1;
                    IDEX_Write.MemToReg = INVALID;


                }
            }
        }
    }



    public void EX_stage(){

        //pass control bit
        EXMEM_Write.Instr = IDEX_Read.Instr;
        EXMEM_Write.MemRead = IDEX_Read.MemRead;
        EXMEM_Write.MemWrite = IDEX_Read.MemWrite;
        EXMEM_Write.Branch = IDEX_Read.Branch;
        EXMEM_Write.MemToReg = IDEX_Read.MemToReg;
        EXMEM_Write.RegWrite = IDEX_Read.RegWrite;
        EXMEM_Write.SWValue = IDEX_Read.ReadReg2Value;
        EXMEM_Write.CalcBTA = INVALID;


        if(IDEX_Read.RegDst==1){
            EXMEM_Write.WriteRegNum = IDEX_Read.WriteReg_15_11;
        }else if (IDEX_Read.RegDst == 0){
            EXMEM_Write.WriteRegNum = IDEX_Read.WriteReg_20_16;
        }else if(IDEX_Read.RegDst == INVALID){
            EXMEM_Write.WriteRegNum = INVALID;
        }


        int value;

        if(IDEX_Read.ALUSrc == 1){

            value = IDEX_Read.SEOffet;
            //load
            //store
            EXMEM_Write.ALUResult = IDEX_Read.ReadReg1Value + value;


        }else if(IDEX_Read.ALUSrc == 0){
            //R type

            value = IDEX_Read.ReadReg2Value;


            //use function code
            if(IDEX_Read.Function == 0x20){
                //add
                EXMEM_Write.ALUResult = IDEX_Read.ReadReg1Value + value;
            }else if(IDEX_Read.Function == 0x22){
                //sub
                EXMEM_Write.ALUResult = IDEX_Read.ReadReg1Value - value;

            }else if(IDEX_Read.Function == 0x24){
                //and
            }

        }

        if(IDEX_Read.ALUOp == 1) {

            System.out.println(Integer.toHexString(IDEX_Read.IncrPC));
            System.out.println(Integer.toHexString(IDEX_Read.SEOffet));

            EXMEM_Write.CalcBTA = IDEX_Read.IncrPC + (IDEX_Read.SEOffet << 2);
            System.out.println(Integer.toHexString(EXMEM_Write.CalcBTA));

            if (IDEX_Read.OpCode == 0x4) {
                //beq


                if (IDEX_Read.ReadReg1Value == IDEX_Read.ReadReg2Value) {
                    EXMEM_Write.Zero = true;
                    PC = EXMEM_Write.CalcBTA;

                }

            } else if (IDEX_Read.OpCode == 0x5) {
                //bnq
                if (IDEX_Read.ReadReg1Value != IDEX_Read.ReadReg2Value) {
                    EXMEM_Write.Zero = true;
                    PC = EXMEM_Write.CalcBTA;

                }
            }

        }

    }



    public void MEM_stage(){

        MEMWB_Write.MemToReg = EXMEM_Read.MemToReg;
        MEMWB_Write.RegWrite = EXMEM_Read.RegWrite;


        MEMWB_Write.Instr = EXMEM_Read.Instr;
        MEMWB_Write.ALUResult = EXMEM_Read.ALUResult;
        MEMWB_Write.WriteRegNum = EXMEM_Read.WriteRegNum;


        if(EXMEM_Read.Branch == 1){


        }


        if(EXMEM_Read.MemRead == 1){
            //lw
            //read from Memory
            MEMWB_Write.LWDataValue = Main_Mem[MEMWB_Write.ALUResult];


        }else{
            MEMWB_Write.LWDataValue = INVALID;

        }



        if(EXMEM_Read.MemWrite ==1){
            //sw

            Main_Mem[MEMWB_Write.ALUResult] = EXMEM_Read.SWValue;

        }


    }



    public void WB_stage(){

        int value;
        if(MEMWB_Read.MemToReg == 1) {
            value = MEMWB_Read.LWDataValue;
            if(MEMWB_Read.RegWrite == 1){

                Regs[MEMWB_Read.WriteRegNum] = value;

            }
        }

        if(MEMWB_Read.MemToReg == 0) {

            value = MEMWB_Read.ALUResult;
            if(MEMWB_Read.RegWrite == 1){

                Regs[MEMWB_Read.WriteRegNum] = value;
            }
        }






    }




    public void Copy_Write_To_Read()throws CloneNotSupportedException{



        IFID_Read = IFID_Write.clone();
        IDEX_Read = IDEX_Write.clone();
        EXMEM_Read = EXMEM_Write.clone();
        MEMWB_Read = MEMWB_Write.clone();


        IFID_Write.reset();
        IDEX_Write.reset();
        EXMEM_Write.reset();
        MEMWB_Write.reset();

    }



    public void Print_out_everything(){

        print_IFID();
        print_IDEX();
        print_EXMEM();
        print_MEMWB();
        print_Register();
    }



    public void print_IFID(){

        System.out.println("IF/ID Write (written to by the IF stage)");
        System.out.println("----------------------------------------");

        if (IFID_Write.Inst == 0) {

            System.out.print("Inst = ?       ");
            System.out.println("IncrPC = ?");


        } else {

            System.out.print("Inst = 0x" + String.format("%08X", IFID_Write.Inst & 0xffffffff) + "       ");
            System.out.println("IncrPC = " + String.format("%05X", IFID_Write.IncrPC & 0xfffff));
        }

        System.out.println();
        System.out.println("IF/ID Read (read by the ID stage)");
        System.out.println("----------------------------------------");

        if (IFID_Read.Inst == 0) {
            System.out.print("Control: 000000000");


        } else {
            System.out.print("Inst = 0x" + String.format("%08X", IFID_Read.Inst & 0xffffffff) + "       ");
            System.out.println("IncrPC = " + String.format("%05X", IFID_Read.IncrPC & 0xfffff));

        }
        System.out.println();
        System.out.println();
    }

    public void print_IDEX(){
        System.out.println("ID/EX Write (written to by the ID stage)");
        System.out.println("----------------------------------------");

        System.out.print("Control: ");

        if (IDEX_Write.Instr == 0) {

            System.out.print("000000000");


        } else {

            if (IDEX_Write.RegDst == INVALID) {
                System.out.print("RegDst=X,  ");
            } else {
                System.out.print("RegDst=" + IDEX_Write.RegDst + ",  ");
            }
            System.out.print("ALUSrc=" + IDEX_Write.ALUSrc + ",  ");
            System.out.print("ALUOp=" + String.format("%02d", IDEX_Write.ALUOp) + ",  ");
            System.out.print("MemRead=" + IDEX_Write.MemRead + ",  ");
            System.out.println("MemWrite=" + IDEX_Write.MemWrite + ",  ");
            System.out.print("         Branch=" + IDEX_Write.Branch + ",  ");


            if (IDEX_Write.MemToReg == INVALID) {
                System.out.print("MemToReg=X,  ");
            } else {
                System.out.print("MemToReg=" + IDEX_Write.MemToReg + ",  ");

            }

            System.out.println("RegWrite=" + IDEX_Write.RegWrite + "  ");
            System.out.println();


            System.out.print("Incr PC = " + String.format("%05X", IDEX_Write.IncrPC & 0xfffff) + "    ");
            System.out.print("ReadReg1Value = " + Integer.toHexString(IDEX_Write.ReadReg1Value) + "    ");
            System.out.println("ReadReg2Value = " + Integer.toHexString(IDEX_Write.ReadReg2Value) + "    ");


            if (IDEX_Write.SEOffet == INVALID) {
                System.out.print("SEOffset = X    ");
            }else {
                System.out.print("SEOffset = " + Integer.toHexString(IDEX_Write.SEOffet) + "   ");
            }

            System.out.print("WriteReg_20_16 = " + IDEX_Write.WriteReg_20_16 + "   ");
            System.out.print("WriteReg_15_11 = " + IDEX_Write.WriteReg_15_11 + "   ");

            if(IDEX_Write.Function == INVALID) {


                System.out.println("Function = X");

            } else {
                System.out.println("Function = x" + Integer.toHexString(IDEX_Write.Function));


            }
        }
        System.out.println();

        System.out.println();


        System.out.println("ID/EX Read (read by the EX stage)");
        System.out.println("----------------------------------------");

        System.out.print("Control: ");

        if (IDEX_Read.Instr == 0) {

            System.out.print("000000000");


        } else {
            if (IDEX_Read.RegDst == INVALID) {
                System.out.print("RegDst=X,  ");
            } else {
                System.out.print("RegDst=" + IDEX_Read.RegDst + ",  ");
            }
            System.out.print("ALUSrc=" + IDEX_Read.ALUSrc + ",  ");
            System.out.print("ALUOp=" + String.format("%02d", IDEX_Read.ALUOp) + ",  ");
            System.out.print("MemRead=" + IDEX_Read.MemRead + ",  ");
            System.out.println("MemWrite=" + IDEX_Read.MemWrite + ",  ");
            System.out.print("         Branch=" + IDEX_Read.Branch + ",  ");


            if (IDEX_Read.MemToReg == INVALID) {
                System.out.print("MemToReg=X,  ");
            } else {
                System.out.print("MemToReg=" + IDEX_Read.MemToReg + ",  ");

            }

            System.out.println("RegWrite=" + IDEX_Read.RegWrite + "  ");
            System.out.println();


            System.out.print("Incr PC = " + String.format("%05X", IDEX_Read.IncrPC & 0xfffff) + "    ");
            System.out.print("ReadReg1Value = " + Integer.toHexString(IDEX_Read.ReadReg1Value) + "    ");
            System.out.println("ReadReg2Value = " + Integer.toHexString(IDEX_Read.ReadReg2Value) + "    ");

            if (IDEX_Read.SEOffet == INVALID) {
                System.out.print("SEOffset = X    ");
            }else {
                System.out.print("SEOffset = " + Integer.toHexString(IDEX_Read.SEOffet) + "   ");
            }

            System.out.print("WriteReg_20_16 = " + IDEX_Read.WriteReg_20_16 + "   ");
            System.out.print("WriteReg_15_11 = " + IDEX_Read.WriteReg_15_11 + "   ");
            if(IDEX_Read.Function == INVALID) {


                System.out.println("Function = X");

            } else {
                System.out.println("Function = x" + Integer.toHexString(IDEX_Read.Function));


            }
        }
        System.out.println();

    }

    public void print_EXMEM(){

        System.out.println("EX/MEM Write (written to by the EX stage)");
        System.out.println("----------------------------------------");


        System.out.print("Control: ");

        if (EXMEM_Write.Instr == 0) {

            System.out.print("000000000");


        } else {
            System.out.print("MemRead=" + EXMEM_Write.MemRead + ",  ");
            System.out.print("MemWrite=" + EXMEM_Write.MemWrite + ",  ");
            System.out.print("Branch=" + EXMEM_Write.Branch + ",  ");


            if (EXMEM_Write.MemToReg == INVALID) {
                System.out.print("MemToReg=X,  ");
            } else {
                System.out.print("MemToReg=" + EXMEM_Write.MemToReg + ",  ");

            }



            System.out.println("RegWrite=" + EXMEM_Write.RegWrite + "  ");

            System.out.println();

            if(EXMEM_Write.CalcBTA == INVALID){
                System.out.print("CalcBTA = X     ");

            }else{
                System.out.print("CalcBTA = " + String.format("%05X", EXMEM_Write.CalcBTA & 0xfffff) + "     ");

            }

            System.out.print("Zero = " + EXMEM_Write.Zero + "     ");


            System.out.println("ALUResult = " + Integer.toHexString(EXMEM_Write.ALUResult));

            System.out.print("SWValue = " + Integer.toHexString(EXMEM_Write.SWValue) + "      ");

            if(EXMEM_Write.WriteRegNum == INVALID) {

                System.out.println("WriteRegNum = X");
            }else{
                System.out.println("WriteRegNum = " + EXMEM_Write.WriteRegNum);

            }

        }
        System.out.println();


        System.out.println();
        System.out.println("EX/MEM Read (read by the MEM stage)");
        System.out.println("----------------------------------------");


        System.out.print("Control: ");
        if (EXMEM_Read.Instr == 0) {

            System.out.print("000000000");


        } else {
            System.out.print("MemRead=" + EXMEM_Read.MemRead + ",  ");
            System.out.print("MemWrite=" + EXMEM_Read.MemWrite + ",  ");
            System.out.print("Branch=" + EXMEM_Read.Branch + ",  ");


            if (EXMEM_Read.MemToReg == INVALID) {
                System.out.print("MemToReg=X,  ");
            } else {
                System.out.print("MemToReg=" + EXMEM_Read.MemToReg + ",  ");

            }



            System.out.println("RegWrite=" + EXMEM_Read.RegWrite + "  ");

            System.out.println();

            if(EXMEM_Read.CalcBTA == INVALID){
                System.out.print("CalcBTA = X     ");

            }else{
                System.out.print("CalcBTA = " + String.format("%05X", EXMEM_Read.CalcBTA & 0xfffff) + "     ");

            }

            System.out.print("Zero = " + EXMEM_Read.Zero + "     ");


            System.out.println("ALUResult = " + Integer.toHexString(EXMEM_Read.ALUResult));

            System.out.print("SWValue = " + Integer.toHexString(EXMEM_Read.SWValue) + "      ");

            if(EXMEM_Read.WriteRegNum == INVALID) {

                System.out.println("WriteRegNum = X");
            }else{
                System.out.println("WriteRegNum = " + EXMEM_Read.WriteRegNum);

            }

        }

        System.out.println();
    }

    public void print_MEMWB(){


        System.out.println("MEM/WB Write (written to by the MEM stage)");
        System.out.println("----------------------------------------");

        System.out.print("Control: ");

        if (MEMWB_Write.Instr == 0) {

            System.out.println("000000000");


        } else {
            if (MEMWB_Write.MemToReg == INVALID) {
                System.out.print("MemToReg=X,  ");
            } else {
                System.out.print("MemToReg=" + MEMWB_Write.MemToReg + ",  ");

            }


            System.out.println("RegWrite=" + MEMWB_Write.RegWrite + "  ");

            if(MEMWB_Write.LWDataValue == INVALID){
                System.out.print("LWDataValue = X    ");

            }else{
                System.out.print("LWDataValue = " + Integer.toHexString(MEMWB_Write.LWDataValue) + "    ");

            }

            System.out.print("ALUResult = " + Integer.toHexString(MEMWB_Write.ALUResult) + "    ");

            if(MEMWB_Write.WriteRegNum == INVALID){
                System.out.println("WriteRegNum = X");

            }else{
                System.out.println("WriteRegNum = " + MEMWB_Write.WriteRegNum);

            }




            if(MEMWB_Write.MemToReg == INVALID && MEMWB_Write.LWDataValue == INVALID && MEMWB_Write.WriteRegNum == INVALID){
                //sw

                System.out.println("    (value "+ Integer.toHexString(EXMEM_Read.SWValue) +" written to memory address "+ Integer.toHexString(MEMWB_Write.ALUResult)+")" );
            }
        }






        System.out.println();





        System.out.println("MEM/WB Read (read by the WB stage)");
        System.out.println("----------------------------------------");

        System.out.print("Control: ");

        if (MEMWB_Read.Instr == 0) {

            System.out.println("000000000");


        } else {
            if (MEMWB_Read.MemToReg == INVALID) {
                System.out.print("MemToReg=X,  ");
            } else {
                System.out.print("MemToReg=" + MEMWB_Read.MemToReg + ",  ");

            }


            System.out.println("RegWrite=" + MEMWB_Read.RegWrite + "  ");

            if(MEMWB_Read.LWDataValue == INVALID){
                System.out.print("LWDataValue = X    ");

            }else{
                System.out.print("LWDataValue = " + Integer.toHexString(MEMWB_Read.LWDataValue) + "    ");

            }

            System.out.print("ALUResult = " + Integer.toHexString(MEMWB_Read.ALUResult) + "    ");

            if(MEMWB_Read.WriteRegNum == INVALID){
                System.out.println("WriteRegNum = X");

            }else{
                System.out.println("WriteRegNum = " + MEMWB_Read.WriteRegNum);

            }



            if(MEMWB_Read.RegWrite == 1) {

                if (MEMWB_Read.MemToReg == 0) {


                    System.out.println("    ($"+MEMWB_Read.WriteRegNum +" is set to "+Integer.toHexString(MEMWB_Read.ALUResult) +")");
                }else{

                    System.out.println("    ($" + MEMWB_Read.WriteRegNum + " is set to " + Integer.toHexString(MEMWB_Read.LWDataValue)+")");
                }
            }


            if(MEMWB_Read.MemToReg ==INVALID && MEMWB_Read.WriteRegNum == INVALID){

                System.out.println("    (no register is written to since a sw/sb does not write to a register)");
            }

        }

        System.out.println();
    }

    public void print_Register(){

        System.out.print("Register: ");
        for(int i = 0; i<Regs.length; i++){
            System.out.print(Integer.toHexString(Regs[i]) + "  ");
        }
        System.out.println();

    }


}

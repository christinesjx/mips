/*
Jiaxin Sun
CS472
Project 3

 */

public class Initialization {

    public int[] MainMemory() {
        //initialize main memory
        int[] MainMem = new int[1024];
        for (int i = 0; i < MainMem.length; i++) {
            MainMem[i] = (short) (i & 0xff);

        }
        return MainMem;

    }

    public int[] Regs(){
        //initialize 32 register
        int[] regs = new int[32];

        regs[0] = 0;

        for(int i = 1; i< regs.length; i++){
            regs[i] = 0x100 + i;

        }
        return  regs;
    }




}

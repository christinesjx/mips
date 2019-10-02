/*
Jiaxin Sun
CS472
Project 3

 */

public class IFID implements Cloneable{
    int Inst = 0;
    int IncrPC = 0x7a000;


    public IFID clone() throws CloneNotSupportedException{

        return (IFID)super.clone();

    }

    public void reset(){
        Inst = 0;
        IncrPC = 0;

    }




}

/*
Jiaxin Sun
CS472
Project 3

 */

public class Main {

    public static void main(String[] args) throws CloneNotSupportedException {

        Stage stage = new Stage();

        int[] InstructionCode = {0,0xa1020000, 0x810afffc, 0x00831820, 0x01263820, 0x01224820,0x81180000,0x81510010, 0x00624022,0,0,0,0};

        for(int ClockCycle = 0; ClockCycle < 13; ClockCycle++){


            System.out.println("Clock Cycle " + ClockCycle );
            System.out.println("==============");

            stage.IF_stage(InstructionCode[ClockCycle]);
            stage.ID_stage();
            stage.EX_stage();
            stage.MEM_stage();
            stage.WB_stage();


            stage.Print_out_everything();
            stage.Copy_Write_To_Read();

            System.out.println();
            System.out.println();

        }


    }

}


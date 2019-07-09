package com.example.tictactoe;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

/**
 * @author Steffen Hanzlik
 * Matriculation number: 1207417
 * https://github.com/Panthalaimon/TicTacToe.git
 * ==========================================================================
 * Instruction Dialog Alert Screen
 */
public class InstructionDialog extends AppCompatDialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Instruction")
                .setMessage("Play on one Device: \n Click On Start on this device button \n"+
                        "and play versus your friend\n" +
                        "\n"+
                        "Play on two Devices: \n" +
                        "Before you can play you have to pair your devices\n"+
                        "over the bluetooth settings of the devices\n"+
                        "\n"+
                        "Click on Start on two devices on both screens\n" +
                        "One click on listen the other one on Show devices\n"+
                        "the one which shows devices search in the list for\n"+
                        "the binded device and click on it\n"+
                        "now you binded\n"+
                        "Player 1 (the one who pressed) \" listen\" begins \n"
                )
                .setPositiveButton("understand", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

        return builder.create();
    }
}

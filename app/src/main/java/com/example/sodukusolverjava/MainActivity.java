package com.example.sodukusolverjava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


enum Direction {
    RIGHT,
    LEFT,
    UP,
    DOWN
}

/** Todo: skapa onclicklisteners för cellerna, få dem att uppdatera cellvärden
 *        se till att det blir rätt cellvärden beroende på vilken submatris man befinner sig i
 *        se till att man inte kan skriva in nummer så att det går emot reglerna
 *         **/

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        final EditText[][] cells = {{findViewById(R.id.editTextNumberSigned00), findViewById(R.id.editTextNumberSigned01), findViewById(R.id.editTextNumberSigned02)},
                                    {findViewById(R.id.editTextNumberSigned10), findViewById(R.id.editTextNumberSigned11), findViewById(R.id.editTextNumberSigned12)},
                                    {findViewById(R.id.editTextNumberSigned20), findViewById(R.id.editTextNumberSigned21), findViewById(R.id.editTextNumberSigned22)},};


        final TextView test = (TextView) findViewById(R.id.textView19);
        final Button leftButton = (Button) findViewById(R.id.leftButton);
        final Button rightButton = (Button) findViewById(R.id.rightButton);
        final Button upButton = (Button) findViewById(R.id.upButton);
        final Button downButton = (Button) findViewById(R.id.downButton);
        final TextView cordText = (TextView) findViewById(R.id.CoordText);
        cordText.setText("0.0");
        final SodukuBoard sodukuBoard = new SodukuBoard(0,0);
        final GameEngine gameEngine = new GameEngine(sodukuBoard);




        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFocusedMatrix(gameEngine, Direction.LEFT, cordText);
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFocusedMatrix(gameEngine, Direction.RIGHT, cordText);
            }
        });
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFocusedMatrix(gameEngine, Direction.UP, cordText);
            }
        });
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFocusedMatrix(gameEngine, Direction.DOWN, cordText);
            }
        });

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {

                attachCellListener(cells[i][j], gameEngine, test);

            }
        }



    }

    /*

        Unsure about the quality of this method either i create one method which both updates the
        focused matrix and returns textString ín GameEngine, however it would be a very specific use method
        or i use 2 different methods in the listener, which i've heard is not good, or I do this, which might
        create harder to read code. However it carries the advantage of being easier to configure, as the hard to read code is "closer"
        to the surface

        maby create a submethod? (Impossible in java)

     */

    private static void updateFocusedMatrix(GameEngine gameEngine ,Direction dir, TextView coordText, EditText[][]){

        gameEngine.changeFocusedMatrix(dir);
        coordText.setText(gameEngine.getCoordinateString());


    }

    // Taking a final parameter feels sketchy to say the least, however it works without crashing the app
    // probably well worth asking about

    private static void attachCellListener(final EditText editText,final GameEngine gameEngine,final TextView test){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    gameEngine.editCell(0,0,Integer.parseInt(charSequence.toString()));
                    test.setText(charSequence);
                }catch(Exception e){ // For when trying to clear the text, otherwise the app crashes
                    editText.clearFocus();
                    editText.getText().clear();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
}



class GameEngine {

    private SodukuBoard sodukuBoard;

    GameEngine(SodukuBoard sodukuBoard) {
        this.sodukuBoard = sodukuBoard;
    }

    // steps one step in the direction given in the parameter
    public void changeFocusedMatrix(Direction dir) {

        if (dir == Direction.RIGHT) {
            if (sodukuBoard.getxCord() == 2) {
                sodukuBoard.setxCord(0);


            } else {
                sodukuBoard.setxCord(sodukuBoard.getxCord() + 1);
            }
        }
        if (dir == Direction.LEFT) {
            if (sodukuBoard.getxCord() == 0) {
                sodukuBoard.setxCord(2);
            } else {
                sodukuBoard.setxCord(sodukuBoard.getxCord() - 1);
            }
        }
        if (dir == Direction.UP) {
            if (sodukuBoard.getyCord() == 2) {
                sodukuBoard.setyCord(0);
            } else {
                sodukuBoard.setyCord(sodukuBoard.getyCord() + 1);
            }
        }
        if (dir == Direction.DOWN) {
            if (sodukuBoard.getyCord() == 0) {
                sodukuBoard.setyCord(2);
            } else {
                sodukuBoard.setyCord(sodukuBoard.getyCord() - 1);
            }
        }
    }

    public String getCoordinateString(){
        return (sodukuBoard.getyCord() + "." + sodukuBoard.getxCord());
    }

    // Will in the future check if a digit can fit in the place where the user wants to put it, but now it just blindly edits the cell
    public void editCell(int row, int col, int digit) {
        sodukuBoard.setCellVal(row, col, digit);
    }


    // importance of proper naming, should i place these in SodukuBoard? Can't check these yet, just write a first version and then
    // proceed w/ getting the listeners for the different cells working so that i can

    private boolean existsInRow(int col,int digit){
        for (int i = 0; i < 9 ; i++) {
            if(sodukuBoard.getCellVal(i,col) == digit){
                return true;
            }
        }
        return false;
    }

    private boolean existsInCol(int row, int digit){
        for (int i = 0; i < 9; i++) {
            if(sodukuBoard.getCellVal(row,i) == digit){
                return true;
            }
        }
        return false;
    }

    private boolean existsInSubMatrix(int digit){

        int maxRowValue = sodukuBoard.getxCord()*3 + 3;
        int maxColValue = sodukuBoard.getyCord()*3 + 3;

        for (int i = sodukuBoard.getxCord(); i < maxColValue ; i++) {
            for (int j = sodukuBoard.getyCord(); j < maxRowValue; j++) {
                if(sodukuBoard.getCellVal(j,i) == digit)
                    return true;
            }
        }
        return false;
    }


}

// Keeps track of the game What submatrix is currently in focus, check so that the input numbers are viable
// also keeps track of the different cells
class SodukuBoard {
    // index for cells is the cells coordinate
    private int[][] cells;
    private int xCord, yCord;


    SodukuBoard(int xCord, int yCord) {
        this.xCord = xCord;
        this.yCord = yCord;
        cells = new int[9][9];
    }


    public int getxCord() {
        return xCord;
    }

    public void setxCord(int xCord) {
        this.xCord = xCord;
    }

    public int getyCord() {
        return yCord;
    }

    public void setyCord(int yCord) {
        this.yCord = yCord;
    }

    public int getCellVal(int row,int col){
        return cells[row][col];
    }


    public void setCellVal(int row, int col, int val){
        cells[yCord*3 + col][xCord*3 + row] = val;
    }

}

class Solver {

}



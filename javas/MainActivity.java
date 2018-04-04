package com.vitaliysix.triplec;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    private final int PLAYER1_PNG = R.drawable.circle;
    private final int PLAYER2_PNG = R.drawable.cross;

    private Hashtable<Integer, ImageView> chessTable;
    private tttEngine director;
    private boolean insaneMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Default
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init game engine
        director = new tttEngine();

        //Fill ImageViews to hash table
        initChessTable();
    }

    //==============================================================================================
    //Public Functions

    //=================================
    //BTN: Create new game
    public void game_new(View view) {
        View promptWindow = findViewById(R.id.thePrompt);
        promptWindow.setVisibility(View.GONE);
        for (int i=0;i<9;i++){
            chessTable.get(i).setImageResource(0);
        }
        director.newGame(0, insaneMode);
    }

    //=================================
    //BTN: Toggle insane mode
    public void game_insane(View view) {
        this.insaneMode = !this.insaneMode;
        Log.d("GAME:","Insane mode:" + this.insaneMode);
    }

    //=================================
    //BTN: Chess is clicked
    public void game_play(View view) {

        //Get position of the clicked image
        int chessPosition = getPos((ImageView) view);

        //If position is found
        if (chessPosition!=-1) {

            //Tell Engine to moveChess, grab feedback from engine.
            String result = director.moveIt(chessPosition);
            Log.d("GAME",result);

            //If engine says GOOD.
            if (!(result.equals("ERR:GAME END") || result.equals("ERR: SLOT OCCUPIED."))) {

                //Animate chess down.
                chessDown(chessPosition, PLAYER1_PNG, 0);

                //If AI moved, animate that too, with a little delay
                int AImove = director.getAIMove();
                if (AImove!=-1){
                    chessDown(AImove, PLAYER2_PNG, 200);
                }
            }
            //If engine says END.
            if (result.equals("GAME END"))
                game_end();
        }
    }

    //==============================================================================================
    //Private functions

    //=================================
    // Animate a chess down
    private void chessDown(int pos, int playerPNG, int delay) {
        ImageView ImgView = chessTable.get(pos);
        ImgView.setImageResource(playerPNG);
        ImgView.setTranslationY(-1000f);
        ImgView.animate().translationYBy(1000f).setDuration(300).setStartDelay(delay);
    }

    //=================================
    //Animate a game end
    private void game_end(){
        String ending;
        if (director.getResult() == 1) {
            ending="Triple C\n\nYou won!";
        } else if (director.getResult() == 2){
            ending="Triple C\n\nYou lose!";
        } else {
            ending="Triple C\n\nGame Tied!";
        }
        TextView cout = (TextView) findViewById(R.id.cout);
        cout.setText(ending);
        View promptWindow = findViewById(R.id.thePrompt);
        promptWindow.setVisibility(View.VISIBLE);
    }

    //=================================
    //Convert view to slot number
    private int getPos(ImageView img){
        int chessPosition;
        switch (img.getId()) {
            case R.id.btn1:  chessPosition = 0;
                break;
            case R.id.btn2:  chessPosition = 1;
                break;
            case R.id.btn3:  chessPosition = 2;
                break;
            case R.id.btn4:  chessPosition = 3;
                break;
            case R.id.btn5:  chessPosition = 4;
                break;
            case R.id.btn6:  chessPosition = 5;
                break;
            case R.id.btn7:  chessPosition = 6;
                break;
            case R.id.btn8:  chessPosition = 7;
                break;
            case R.id.btn9:  chessPosition = 8;
                break;
            default: chessPosition = -1;
                break;
        }
        return chessPosition;
    }

    //=================================
    // Put ImageViews into a Hash table.
    private void initChessTable(){
        chessTable = new Hashtable<Integer, ImageView>();
        chessTable.put(0,(ImageView)findViewById(R.id.btn1));
        chessTable.put(1,(ImageView)findViewById(R.id.btn2));
        chessTable.put(2,(ImageView)findViewById(R.id.btn3));
        chessTable.put(3,(ImageView)findViewById(R.id.btn4));
        chessTable.put(4,(ImageView)findViewById(R.id.btn5));
        chessTable.put(5,(ImageView)findViewById(R.id.btn6));
        chessTable.put(6,(ImageView)findViewById(R.id.btn7));
        chessTable.put(7,(ImageView)findViewById(R.id.btn8));
        chessTable.put(8,(ImageView)findViewById(R.id.btn9));
    }
}

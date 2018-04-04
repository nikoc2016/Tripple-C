package com.vitaliysix.triplec;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;
/**
 * My first AI game
 * 我的第一个AI游戏
 * Tic Tac Toe
 * 井字棋
 *
 * @author NikoC Lu
 */
public class tttEngine {
    
    private int[] gameBoard; //0-8 The game board.
    private int gameMode;    //[0:VS AI, 1:VS HUMAN]
    private int currentPlayer; //Whose turn [1,2]
    private int gameResult; //0-Playing [1,2]Player won, 3-Tie
    private boolean isEnd;   //If game has finished
    private String gameMsg; //The engine's msg
    private int AI_move;    //The Human and AI moves
    private boolean insaneMode;     //If AI is unbeatable
    
    //========================================================
    //PUBLIC FUNCTIONS 
    //+ void newGame(int gameMode);                           | Init game, gameMode = [0:VS AI] [1:VS HUMAN]
    //+ String moveIt(int chessPosition);                     | Place a chess, position = [0-8]
    //+ int getResult();                                      | [0:Progressing] [1-2:Player Won] [3:Tied]
    //+ void debug();                                         | Test Field
    
    tttEngine(){
        init(0);
    }
    
    public void newGame(int gameMode, boolean insaneMode){
        init(gameMode);
        this.insaneMode = insaneMode;
    }
    
    public String moveIt(int chessPosition){
        if (isEnd) {
            //Don't do nothing if game has end.
            this.gameMsg = "ERR:GAME END";
        } else {
            this.gameMsg = "PROGRESSING";
            //Check is slot is available
            if (slotOccupied(this.gameBoard,chessPosition)){
                this.gameMsg = "ERR: SLOT OCCUPIED.";
            } else {
                //Place a chess move
                chessDown(this.gameBoard,chessPosition,this.currentPlayer);
                //Validate Game if Ended
                this.gameResult = validate(this.gameBoard);
                this.isEnd = (gameResult != 0);
                if (isEnd) this.gameMsg = "GAME END";
                else {
                    //Switch Player
                    this.currentPlayer = this.switchPlayer(this.currentPlayer);
                    //Enable AI
                    if (this.gameMode==0 && this.currentPlayer == 2){
                        AI_move();
                    }
                }
            }   
        }
        return this.gameMsg;
    }
    
    public int getResult() {
        return this.gameResult;
    }

    public int getAIMove(){
        int tmp = this.AI_move;
        this.AI_move = -1;
        return tmp;
    }
    
    //========================================================
    //PRIVATE FUNCTIONS
    //- void init(int gameMode);                                | Init the game data
    //- void chessDown(int[] gameBoard, int pos, int player)    | Perfrom a chess down
    //- boolean slotOccupied(int[] gameBoard, int pos);         | Check if certain slot is already occupied
    //- int validate(int[] gameBoard);                          | Validate if game has end RETURN int result
    //- boolean playerWon(int[] board, int player);             | Check if certain player win the game
    //- ArrayList emptySlots(int[] board);                      | Return a list of empty slots
    //- int switchPlayer(int player);                           | Return a swapped player ptr
    //- void AI_move()                                          | Perform a AI move
    //- void AI_test()                                          | Perform a win check
    
    private void init(int gameMode){
        this.gameBoard = new int [9];
        for (int i=0;i<9;i++) this.gameBoard[i] = 0;
        this.gameMode = gameMode;
        this.currentPlayer = 1;
        this.gameMsg = "Initialized.";
        this.isEnd = false;
        this.gameResult = 0;
        this.insaneMode = false;
    }
    
    private void chessDown(int[] gameBoard, int pos, int player){
        gameBoard[pos] = player;
    }
    
    private boolean slotOccupied(int[] gameBoard, int pos){
        return gameBoard[pos]!=0;
    }
    
    private int validate(int[] gameBoard){
            if (playerWon(gameBoard, 1))
                return 1;
            else if (playerWon(gameBoard, 2))
                return 2;
            else if (emptySlots(gameBoard).isEmpty()) {
                return 3;
            } else {
                return 0;
            }
    }
    
    private boolean playerWon(int[] board, int player){
        return (board[0] == player && board[1] == player && board[2] == player) ||
                (board[3] == player && board[4] == player && board[5] == player) ||
                (board[6] == player && board[7] == player && board[8] == player) ||
                (board[0] == player && board[3] == player && board[6] == player) ||
                (board[1] == player && board[4] == player && board[7] == player) ||
                (board[2] == player && board[5] == player && board[8] == player) ||
                (board[0] == player && board[4] == player && board[8] == player) ||
                (board[2] == player && board[4] == player && board[6] == player);
    }
    
    private ArrayList emptySlots(int[] board){
        ArrayList spots = new ArrayList( );
        for (int i=0;i<9;i++){
            if (board[i] == 0)
                spots.add(i);
        }
        return spots;
    }
    
    
    private int switchPlayer(int player) {
        player++;
        if (player>2)
            player = 1;
        return player;
    }
    
    private void AI_move() {
        boolean bypass = false;
        int chessTarget = 0;
        
        ArrayList<Integer> openSpots = emptySlots(this.gameBoard);
        if (openSpots.contains(4)) {
            bypass = true;
            chessTarget = 4;
        }
        if (!bypass){
            for (int i=0;i<openSpots.size();i++) {
                if (this.AI_test(this.gameBoard, openSpots.get(i), 2)) {
                    chessTarget = openSpots.get(i);
                    bypass = true;
                    break;
                }
            }
        }
        if (!bypass){
            for (int i=0;i<openSpots.size();i++) {
                if (this.AI_test(this.gameBoard, openSpots.get(i), 1)) {
                    chessTarget = openSpots.get(i);
                    bypass = true;
                    break;
                }
            }
        }
        if (!bypass){
            Random random = new Random();
            do{
                chessTarget = random.nextInt(9);
            }while(!openSpots.contains(chessTarget));
        }
        if (!insaneMode){
            Random random = new Random();
            if (random.nextInt(10)>7) {
                do {
                    Log.v("GAME:","AI is dreaming");
                    chessTarget = random.nextInt(9);
                } while (!openSpots.contains(chessTarget));
            }
        }

        this.AI_move = chessTarget;
        this.moveIt(chessTarget);
    }
    
    private boolean AI_test(int[] board, int openSpot, int player){
        int[] vBoard = new int[9];
        System.arraycopy(this.gameBoard, 0, vBoard, 0, 9);
        
        this.chessDown(vBoard,openSpot,player);
        return this.validate(vBoard)==player;
    }
}

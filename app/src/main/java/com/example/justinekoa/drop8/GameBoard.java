package com.example.justinekoa.drop8;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Pack200;

/**
 * Created by justinekoa on 11/19/18.
 */

public class GameBoard {
    private Token[][] board = new Token[8][8];
    Scanner reader = new Scanner(System.in);
    private boolean gameover;

    // start gameboard
    GameBoard(){
        clear_board();
    }

    // set gameboard to be 8x8 of null
    private void clear_board(){
        for(int row=0; row<8; row++){
            for(int col=0; col<8; col++){
                this.board[row][col] = null;
            }
        }
    }

    // print board to console
    private void print_board(){
        for(int row=0; row<8; row++){
            for(int col=0; col<8; col++){
                if(this.board[row][col] != null){
                    System.out.print("[" + this.board[row][col].getValue() + "]");
                }
                else{
                    System.out.print("[ ]");
                }
            }
            System.out.println();
        }
    }

    // start game, asking for user input, ends when game over
    public void startGame(){
        int move_count = 0;
        while(!this.gameover){
            move_count++;
            Token current_token = new Token();
            System.out.println("Current Token: " + current_token.getValue());
            System.out.println("Current Move Count: " + move_count);
            this.print_board(); // print before move
            System.out.println("Enter a column: ");
            int col = reader.nextInt();
            int row = find_bottom_of_col(col);
            if(row == -1){
                this.gameover = true;
                break;
            }
            else{
                this.put(current_token, row, col);
                this.print_board(); // look at board with new token before changes
                this.check_for_any_matches(); // check for matches, printing when changes occur
            }

            if(move_count == 5){
                move_count = 0;
                this.level_up();
                if(this.gameover){
                    break;
                }
                this.print_board(); // show board with level up before changes
                this.check_for_any_matches(); // check for matches, printing when changes occur
            }
        }

        System.out.println("Game Over!");
    }

    // level up by moving everything up one row and adding a row of locked tokens on the bottom, can cause game over
    private void level_up(){
        Token[][] new_board = new Token[8][8];
        for(int c=0; c<=7; c++){
            new_board[7][c] = new Token(false);
        }
        for(int r=6; r>=0; r--){
            for(int c=0; c<=7; c++){
                new_board[r][c] = this.board[r+1][c];
            }
        }

        for(int c=0; c<=7; c++){
            if(this.board[0][c] != null){
                this.gameover = true;
            }
        }
        this.board = new_board;
    }

    // puts token on board
    private void put(Token t, int row, int col){
        this.board[row][col] = t;
    }

    // checks for matches in all places on the board
    private void check_for_any_matches(){
        List<int[]> all_matches = new ArrayList<int[]>();

        for(int r=0; r<8; r++){
            for(int c=0; c<8; c++){
                List<int[]> match_found = this.check_pops(r,c);
                for(int[] intArr: match_found){
                    all_matches.add(intArr);
                }
            }
        }

        for(int[] intArr: all_matches){
            this.board[intArr[0]][intArr[1]] = null;
        }
        for(int[] intArr: all_matches){
            this.shift_down_from(intArr[0], intArr[1]);
        }

        if(all_matches.size() > 0){
            System.out.println("Match happened, change occurred: ");
            print_board();
            check_for_any_matches();
        }
    }

    // counts number of touching tokens horizontally and vertically and calls handles matches with these values
    private List<int[]>  check_pops(int row, int col){
        // count number of tokens vertically
        int vertical_token_count = 0;
        for(int r=7; r>=0 && this.board[r][col] != null; r--){
            vertical_token_count++;
        }

        // count number of tokens horizontally touching
        int horizontal_token_count = 0;
        for(int c=col; c<8 && this.board[row][c] != null; c++){
            horizontal_token_count++;
        }

        for(int c=col-1; c>=0 && this.board[row][c] != null; c--){
            horizontal_token_count++;
        }

        return this.handle_matches(vertical_token_count, horizontal_token_count, row, col);
    }

    // handle the matches by editing the ones around it (breaking locked tokens) and adding the location to a list to later be removed (made null)
    private List<int[]>  handle_matches(int v, int h, int row, int col){
        List<int[]> matches = new ArrayList<int[]>();


        for(int r=7; r>=0 && this.board[r][col] != null; r--){
            if(this.board[r][col].is_number() &&  this.board[r][col].getNumber() == v){
                this.board[r][col].setNumber(0);
                this.edit_surroundings(r,col); // edit ones around it
                int[] temp_match = {r, col};
                matches.add(temp_match);
            }
        }

        for(int c=col; c<8 && this.board[row][c] != null; c++){
            if(this.board[row][c].is_number() &&  this.board[row][c].getNumber() == h){
                this.board[row][c].setNumber(0);
                this.edit_surroundings(row,c); // edit ones around it
                int[] temp_match = {row, c};
                matches.add(temp_match);
            }
        }

        for(int c=col-1; c>=0 && this.board[row][c] != null; c--){
            if(this.board[row][c].is_number() &&  this.board[row][c].getNumber() == h){
                this.board[row][c].setNumber(0);
                this.edit_surroundings(row,c); // edit ones around it
                int[] temp_match = {row, c};
                matches.add(temp_match);
            }
        }

        if(matches.size() > 0){ // if a match occur, print board... tokens about to be null are 0 and broken tokens are shown
            print_board();
        }
        return matches;
    }

    // shifts tokens down as long as there are still tokens above it to be moved
    private void shift_down_from(int row, int col){
        for(int i = 7; i >= 0 && !this.check_if_empty_up(i,col); i--){
            if(this.board[i][col] == null){
                this.board[i][col] = this.board[i-1][col];
                this.board[i-1][col] = null;
            }
        }
    }

    // helper method for shift_down_from, checks if all the places on the board above a certain row in a certain column are null (empty)
    private boolean check_if_empty_up(int row, int col){
        for(int i = row; i>=0; i--){
            if(this.board[i][col] != null){
                return false;
            }
        }
        return true;
    }

    // helper method returns if row and col are in board
    private boolean in_board(int row, int col){
        return 0 <= row && row <= 7 && 0 <= col && col <= 7;
    }

    // helper method edits the surrounding tokens for a token that is going to pop (breaks them if they are frozen)
    private void edit_surroundings(int row, int col){
        if(this.in_board(row-1,col) && this.board[row-1][col] != null){
            this.break_token(row-1, col);
        }
        if(this.in_board(row+1,col) && this.board[row+1][col] != null){
            this.break_token(row+1, col);
        }
        if(this.in_board(row,col-1) && this.board[row][col-1] != null){
            this.break_token(row, col-1);
        }
        if(this.in_board(row,col+1) && this.board[row][col+1] != null){
            this.break_token(row, col+1);
        }
    }

    // helper for edit_surroundings, breaks the token if it is a locked token
    private void break_token(int row, int col){
        if(!this.board[row][col].is_number() && !this.board[row][col].is_broken()){
            this.board[row][col].set_broken(true);
        }
    }

    // helper method returns most bottom empty row square of that column
    private int find_bottom_of_col(int col){
        for(int row=7; row>=0; row--){
            if(this.board[row][col] == null){
                return row;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        GameBoard g = new GameBoard();
        g.startGame();
    }
}

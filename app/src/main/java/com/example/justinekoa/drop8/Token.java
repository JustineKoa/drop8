package com.example.justinekoa.drop8;
import java.util.Random;

/**
 * Created by justinekoa on 11/19/18.
 */

public class Token {
    private boolean is_number;
    private boolean is_broken;
    private int number;

    // contructor that sets the toket to a new number or to be locked
    Token(){
        Random r = new Random();
        int number = r.nextInt(15) + 1; // random number between 1 and 15 inclusive
        if(number > 8){
            this.is_number = false;
            this.is_broken = false;
        }
        else{
            this.is_number = true;
            this.number = number;
            this.is_broken = true;
        }
    }

    // constructor to set token to locked one (used for leveling up)
    Token(boolean is_number){
        this.is_number = false;
        this.is_broken = false;
    }

    // returns number
    public int getNumber(){
        return this.number;
    }

    // sets number (used when setting token's number to 0 when testing in the console)
    public void setNumber(int number){
        this.number = number;
    }

    // used to make printing to the console easier
    public String getValue(){
        if(this.is_number()){
            return String.valueOf(this.number);
        }
        else{
            return "*";
        }
    }

    // returns if token is a number or not
    public boolean is_number(){
        return this.is_number;
    }

    // returns if token is broken or not
    public boolean is_broken(){
        return this.is_broken;
    }

    // pretty much only used when breaking a locked token, sets the token to a number
    public void set_broken(boolean is_broken){
        this.is_broken = is_broken;

        if(this.is_broken){
            this.is_number = true;
            this.set_number();
        }
    }

    // sets number to random number between 1-8, used only when a locked token gets broken
    private void set_number(){
        Random r = new Random();
        int number = r.nextInt(8) + 1;
        this.number = number;
    }

}

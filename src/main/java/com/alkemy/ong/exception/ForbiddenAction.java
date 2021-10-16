package com.alkemy.ong.exception;

public class ForbiddenAction extends RuntimeException{

    public ForbiddenAction(String mssg){
        super(mssg);
    }
}

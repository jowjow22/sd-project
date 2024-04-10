package models;

import lombok.Getter;

public class MessageModel {
    @Getter
    private String message = "";

    public MessageModel(String message){
        this.message = message;
    }
}

package gr.ntua.ece.softeng18b.model;

import net.bytebuddy.implementation.bind.annotation.Default;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

@Entity
public class Token {

    @Id
    @NotNull
    private String token;

    private Timestamp timestamp;

    public Token(String token, Timestamp timestamp){
        this.token = token;
        this.timestamp = timestamp;
    }

    public Token() {}

    String getToken(){return this.token;}

    void setToken(String token){this.token = token;}

    void setTimestamp(Timestamp timestamp){this.timestamp = timestamp;}

    Timestamp getTimestamp(){return this.timestamp;}
}
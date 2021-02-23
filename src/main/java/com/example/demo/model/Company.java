package com.example.demo.model;

import javax.persistence.*;

@Entity
@Table(name = "company")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    @Column(name = "reg")
    private String reg;

    @Column(name = "symbol_code")
    private String symbol_Code;

    @Column(name = "boardlot")
    private long boardlot;

    public Company() {
        super();
    }

    //    public Company(String symbol_code, long boardlot) {
//        super();
//        this.symbol_Code = symbol_code;
//        this.boardlot = boardlot;
//    }

    public long getId() {
        return Id;
    }

    public void setId(long id) {
        Id = id;
    }

    public void setReg(String reg) {
        this.reg = reg;
    }

    public String getSymbol_Code() {
        return symbol_Code;
    }

    public void setSymbol_Code(String symbol_Code) {
        this.symbol_Code = symbol_Code;
    }

    public long getBoardlot() {
        return boardlot;
    }

    public void setBoardlot(long boardlot) {
        this.boardlot = boardlot;
    }

}

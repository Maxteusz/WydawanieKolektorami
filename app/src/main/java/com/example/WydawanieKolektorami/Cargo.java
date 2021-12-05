package com.example.WydawanieKolektorami;

public class Cargo {
    int id;
    double quantity;
    String name, Ean,unit;

    public Cargo(int id, double quantity, String name, String unit, String ean) {
        this.id = id;
        this.quantity = quantity;
        this.name = name;
        Ean = ean;
        this.unit = unit;
    }



    public Cargo(int id, double quantity, String name) {
        this.id = id;
        this.quantity = quantity;
        this.name = name;
    }


    public Cargo(double quantity, String ean) {
        this.quantity = quantity;
        Ean = ean;
    }

}

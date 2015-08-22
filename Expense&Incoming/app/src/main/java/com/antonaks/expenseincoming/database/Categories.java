package com.antonaks.expenseincoming.database;


import java.util.Arrays;

/**
 * Created by AntonAks on 14.08.2015.
 */
public class Categories {

    private String [] expCategories = {"","Продукты", "Транспорт", "Подарки", "Обед", "Бытовая Химия"};



    public Categories() {
    }

    public String[] getExpCategories() {
        Arrays.sort(expCategories);
        return expCategories;
    }

    public void setExpCategories(String[] expCategories) {
        this.expCategories = expCategories;
    }
}

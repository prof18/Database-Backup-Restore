package com.prof.dbtest.Data;

import java.sql.Date;

/**
 * Created by marco on 9/25/16.
 */

public class Student {

    private int id;
    private String name;
    private String surname;
    private long born;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public long getBorn() {
        return born;
    }

    public void setBorn(long born) {
        this.born = born;
    }
}

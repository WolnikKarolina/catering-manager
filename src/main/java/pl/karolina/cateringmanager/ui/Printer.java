package pl.karolina.cateringmanager.ui;

import java.util.List;

public class Printer {

    public <T> void printAll (List<T> lists) {
        lists.forEach(System.out::println);
    }

    public <T> void print (T t) {
        System.out.println(t);
    }

    }

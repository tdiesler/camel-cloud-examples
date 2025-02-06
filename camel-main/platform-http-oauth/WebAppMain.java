package com.example;

import org.apache.camel.main.Main;

public class WebAppMain {
    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.configure().addRoutesBuilder(new WebAppRoute());
        main.run();
    }
}

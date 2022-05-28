package com.zlove.bytecode.study.bean;

public class Dog implements Animal {

    private String name = "zlove";

    public Dog(String name) {
        this.name = name;
    }

    @Override
    public void eat() {
        System.out.println("I eat meat.");
    }

    @Override
    public void shout() {
        System.out.println("wang wang wang");
    }
}

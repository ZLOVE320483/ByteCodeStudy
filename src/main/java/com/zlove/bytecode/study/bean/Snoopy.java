package com.zlove.bytecode.study.bean;

public class Snoopy extends Dog {

    private int weight;
    private int height;
    private String color;

    public Snoopy() {
        super("snoopy");
    }

    public Snoopy(int weight) {
        super("snoopy");
        this.weight = weight;
    }

    private void speak() {
        System.out.println("speaking...");
    }

    @Override
    protected void eat() {
        super.eat();
    }

    public void ask() {
        System.out.println("asking...");
    }
}

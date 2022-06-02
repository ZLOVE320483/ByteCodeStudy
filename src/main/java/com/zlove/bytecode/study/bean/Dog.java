package com.zlove.bytecode.study.bean;

import com.zlove.bytecode.study.annotation.AnimalName;
import com.zlove.bytecode.study.annotation.AnimalWeight;

@AnimalName("Doggy")
@AnimalWeight(3)
public class Dog implements Animal {

    public String name;

    public Dog() {
    }

    public Dog(String name) {
        this.name = name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void printName() {
        System.out.println(name);
    }

    private void shout() {
        System.out.println("shouting...");
    }

    protected void eat() {
        System.out.println("eating...");
    }

    public void jump() {
        System.out.println("jumping...");
    }
}

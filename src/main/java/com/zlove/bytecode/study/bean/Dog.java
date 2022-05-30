package com.zlove.bytecode.study.bean;

import com.zlove.bytecode.study.annotation.AnimalName;
import com.zlove.bytecode.study.annotation.AnimalWeight;

@AnimalName("Doggy")
@AnimalWeight(3)
public class Dog implements Animal {

    protected String name;

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
}

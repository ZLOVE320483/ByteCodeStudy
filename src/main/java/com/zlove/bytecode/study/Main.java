package com.zlove.bytecode.study;

import com.zlove.bytecode.study.helper.GenerateCodeHelper;
import javassist.*;


public class Main {

    public static void main(String[] args) {
        CtClass clazz = GenerateCodeHelper.generateCat();
        GenerateCodeHelper.efficientInterface(clazz);
    }
}

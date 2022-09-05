package net.kunmc.lab.ivowel;

public class Test {

    public static void main(String[] args) {
        System.out.println(VowelConverter.getInstance().convertVowelOnly("ikiso", false).getVowelHira());
    }
}

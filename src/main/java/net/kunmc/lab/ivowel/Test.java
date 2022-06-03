package net.kunmc.lab.ivowel;

public class Test {

    public static void main(String[] args) {
        //String ret = VowelManager.getInstance().convertHiragana("課長壊れる～", false);
        System.out.println(VowelManager.getInstance().convertVowelOnly("課長壊れる～", false));
    }


}

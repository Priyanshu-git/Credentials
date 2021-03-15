package com.example.credentials;

import java.util.ArrayList;

public class PasswordGen {
    private boolean caps, small, symbols, digits;

    String generate(int length,boolean caps,boolean small, boolean digits,boolean symbols) {
        this.caps=caps;
        this.digits=digits;
        this.symbols=symbols;
        this.small=small;

        ArrayList<String> arr = new ArrayList<>();
        if (caps) {
            arr.add(randomCapital());
            length--;
        }
        if (small){
            arr.add(randomSmall());
            length--;
        }
        if (symbols) {
            arr.add(randomSymbol());
            length--;
        }
        if (digits) {
            arr.add(randomDigit());
            length--;
        }
        arr.add(randomAll(length));

        StringBuilder pass=new StringBuilder();
        while (arr.size()>0){
            int i=3%arr.size();
            pass.append(arr.get(i));
            arr.remove(i);
        }
        return pass.toString();
    }

    private String randomCapital() {

        int r = (int) (Math.random() * 100);
        r = r % 26;
        return String.valueOf((char) ('A' + r));
    }

    private String randomSmall() {
        int r = (int) (Math.random() * 100);
        r = r % 26;
        return String.valueOf((char) ('a' + r));
    }

    private String randomDigit() {
        int r = (int) (Math.random() * 10);
        r = r % 10;
        return String.valueOf(r);
    }

    private String randomSymbol() {
        int r = (int) (Math.random() * 100);
        char arr[] = {'@', '#', '$', '%', '=', ':', '?', '.', '/', '|', '~', '>', '*', '(', ')', '<'};
        r = r % arr.length;
        return String.valueOf(arr[r]);
    }

    private String randomAll(int n) {
        if (n<1) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; ) {
            int r = (int) (Math.random() * 4);
            switch (r) {
                case 0:
                    if (caps) {
                        sb.append(randomCapital());
                        i++;
                    }
                    break;
                case 1:
                    if (digits) {
                        sb.append(randomDigit());
                        i++;
                    }
                    break;
                case 2:
                    if (small) {
                        sb.append(randomSmall());
                        i++;
                    }
                    break;
                case 3:
                    if (symbols) {
                        sb.append(randomSymbol());
                        i++;
                    }
                    break;
            }
        }
        return sb.toString();
    }
}

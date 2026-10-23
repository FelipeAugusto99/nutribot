package br.com.fiap.nutribot.dto;

final class Numbers {

    private Numbers() {}

    // uma casa decimal
    static double round(double value) {
        return Math.round(value * 10) / 10.0;
    }
}

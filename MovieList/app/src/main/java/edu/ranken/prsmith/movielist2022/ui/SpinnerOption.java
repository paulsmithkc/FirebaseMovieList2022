package edu.ranken.prsmith.movielist2022.ui;

public class SpinnerOption<T> {
    private final String text;
    private final T value;

    public SpinnerOption(String text, T value) {
        this.text = text;
        this.value = value;
    }

    @Override
    public String toString() {
        return text;
    }

    public String getText() {
        return text;
    }

    public T getValue() {
        return value;
    }
}

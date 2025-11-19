package com.basketbandit.util;

import java.util.LinkedList;

public class LoopingLinkedList<E> extends LinkedList<E> {
    public E recycle() {
        E object = pop();
        add(object);
        return object;
    }
}

package com.isaacbrodsky.freeze2.elements;

public interface Element {
    int code();
    ElementImpl impl();
    ElementDef def();
    String name();
}

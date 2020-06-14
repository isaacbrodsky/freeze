package com.isaacbrodsky.freeze2.elements;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SuperZElements implements Elements {
    public static final SuperZElements INSTANCE = new SuperZElements();
    private SuperZElements() {

    }

    @Override
    public Element resolveType(int code) {
        return SuperZElement.forCode(code);
    }

    @Override
    public int getTextMin() {
        return SuperZElement.TEXT_MIN.code();
    }

    @Override
    public List<Element> defaultEditorPalette() {
        return Arrays.asList(SuperZElement.SOLID, SuperZElement.NORMAL, SuperZElement.BREAKABLE, SuperZElement.EMPTY, SuperZElement.LINE);
    }

    @Override
    public Class<? extends Enum<? extends Element>> getElementClass() {
        return SuperZElement.class;
    }

    @Override
    public Collection<Element> allElements() {
        return Collections.unmodifiableList(Arrays.asList(SuperZElement.values()));
    }

    @Override
    public Element unknownElement() {
        return SuperZElement.UNDEFINED;
    }
}

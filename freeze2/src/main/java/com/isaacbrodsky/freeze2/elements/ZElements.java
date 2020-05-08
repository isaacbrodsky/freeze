package com.isaacbrodsky.freeze2.elements;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ZElements implements Elements {
    public static final ZElements INSTANCE = new ZElements();
    private ZElements() {

    }

    @Override
    public Element resolveType(int code) {
        return ZElement.forCode(code);
    }

    @Override
    public int getTextMin() {
        return ZElement.TEXT_MIN.code();
    }

    @Override
    public List<Element> defaultEditorPalette() {
        return Arrays.asList(ZElement.SOLID, ZElement.NORMAL, ZElement.BREAKABLE, ZElement.EMPTY, ZElement.LINE);
    }

    @Override
    public Class<? extends Enum<? extends Element>> getElementClass() {
        return ZElement.class;
    }

    @Override
    public Collection<Element> allElements() {
        return Collections.unmodifiableList(Arrays.asList(ZElement.values()));
    }

    @Override
    public Element unknownElement() {
        return ZElement.UNDEFINED;
    }
}

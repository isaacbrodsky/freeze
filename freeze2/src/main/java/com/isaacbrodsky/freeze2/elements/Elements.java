package com.isaacbrodsky.freeze2.elements;

import java.util.Collection;
import java.util.List;

public interface Elements {
    Element resolveType(int code);

    int getTextMin();

    List<Element> defaultEditorPalette();

    Class<? extends Enum<? extends Element>> getElementClass();

    Collection<Element> allElements();

    Element unknownElement();
}

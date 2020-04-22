package com.isaacbrodsky.zztsearch.etl.text;

public interface GameTextVisitor {
    void visit(BoardGameText board);
    void visit(ElementGameText element);
    void visit(ObjectGameText object);
    void visit(WorldGameText world);
}

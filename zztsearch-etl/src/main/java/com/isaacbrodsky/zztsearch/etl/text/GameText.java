package com.isaacbrodsky.zztsearch.etl.text;

public interface GameText {
    void accept(GameTextVisitor visitor);
}
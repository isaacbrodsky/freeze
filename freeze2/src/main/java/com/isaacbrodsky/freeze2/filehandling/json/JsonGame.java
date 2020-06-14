package com.isaacbrodsky.freeze2.filehandling.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isaacbrodsky.freeze2.filehandling.WorldList;
import com.isaacbrodsky.freeze2.game.GameController;
import com.isaacbrodsky.freeze2.utils.StringBackedInputStream;
import com.isaacbrodsky.freeze2.utils.StringBackedOutputStream;

import java.io.IOException;

public class JsonGame {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private JsonGame() {
    }

    public static void save(GameController game, WorldList worldList, String fileName) throws IOException {
        StringBackedOutputStream buf = new StringBackedOutputStream();

        objectMapper.writeValue(buf, game);

        worldList.putWorldBytes(buf, fileName);
    }

    public static GameController load(WorldList worldList, String fileName) throws IOException {
        StringBackedInputStream buf = worldList.getWorldBytes(fileName);
        GameController game = objectMapper.readValue(buf, GameController.class);
        return game;
    }
}

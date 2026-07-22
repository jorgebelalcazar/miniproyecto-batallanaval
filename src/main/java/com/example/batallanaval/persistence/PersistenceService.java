package com.example.batallanaval.persistence;

import com.example.batallanaval.service.GameManager;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Handles saving and loading the game to disk (HU-5, criterion 12).
 * <p>
 * It uses the two mechanisms required by the statement:
 * <ul>
 *   <li>a <b>serialized file</b> ({@code game.dat}) that stores the whole
 *       {@link GameManager}, so the exact board state can be restored, and</li>
 *   <li>a <b>flat text file</b> ({@code player.txt}) that stores the player's
 *       nickname and the number of enemy ships sunk.</li>
 * </ul>
 * This is the only class that touches the file system.
 *
 */
public class PersistenceService {

    private static final Path SAVE_DIR = Path.of("saves");
    private static final Path GAME_FILE = SAVE_DIR.resolve("game.dat");
    private static final Path PLAYER_FILE = SAVE_DIR.resolve("player.txt");

    /**
     * Saves the current game, writing both the serialized game state and the flat
     * player file (HU-5).
     *
     * @param game the game to save
     * @throws IOException if writing to disk fails
     */
    public void saveGame(GameManager game) throws IOException {
        Files.createDirectories(SAVE_DIR);
        writeSerializedGame(game);
        writePlayerFile(game);
    }

    /**
     * Writes the whole game as a serialized object (the serializable mechanism).
     */
    private void writeSerializedGame(GameManager game) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(
                Files.newOutputStream(GAME_FILE))) {
            out.writeObject(game);
        }
    }

    /**
     * Writes the nickname and sunk-ship count as a plain text file (the flat mechanism).
     */
    private void writePlayerFile(GameManager game) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(PLAYER_FILE.toFile()))) {
            writer.write("nickname=" + game.getNickname());
            writer.newLine();
            writer.write("enemyShipsSunk=" + game.getEnemyShipsSunkByHuman());
            writer.newLine();
        }
    }

    /**
     * Loads the most recent saved game from the serialized file.
     *
     * @return the saved game, or an empty {@link Optional} if there is none
     * @throws IOException            if reading fails
     * @throws ClassNotFoundException if the saved class no longer matches
     */
    public Optional<GameManager> loadGame() throws IOException, ClassNotFoundException {
        if (!hasSavedGame()) {
            return Optional.empty();
        }
        try (ObjectInputStream in = new ObjectInputStream(
                Files.newInputStream(GAME_FILE))) {
            GameManager game = (GameManager) in.readObject();
            return Optional.of(game);
        }
    }

    /**
     * @return {@code true} if a saved game file exists on disk
     */
    public boolean hasSavedGame() {
        return Files.exists(GAME_FILE);
    }

    /**
     * Reads the flat player file (nickname and enemy ships sunk).
     *
     * @return the player data, or empty if the flat file does not exist
     * @throws IOException if reading fails
     */
    public Optional<PlayerData> loadPlayerData() throws IOException {
        if (!Files.exists(PLAYER_FILE)) {
            return Optional.empty();
        }
        String nickname = "";
        int sunk = 0;
        for (String line : Files.readAllLines(PLAYER_FILE)) {
            if (line.startsWith("nickname=")) {
                nickname = line.substring("nickname=".length());
            } else if (line.startsWith("enemyShipsSunk=")) {
                sunk = Integer.parseInt(line.substring("enemyShipsSunk=".length()));
            }
        }
        return Optional.of(new PlayerData(nickname, sunk));
    }

    /**
     * Simple immutable holder for the data stored in the flat player file.
     *
     * @param nickname      the player's nickname
     * @param enemyShipsSunk the number of enemy ships the human has sunk
     */
    public record PlayerData(String nickname, int enemyShipsSunk) implements Serializable {
    }
}
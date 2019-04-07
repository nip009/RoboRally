package inf112.roborally.game.board;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import inf112.roborally.game.RoboRallyGame;
import inf112.roborally.game.animations.Animation;
import inf112.roborally.game.animations.LaserAnimation;
import inf112.roborally.game.animations.RepairAnimation;
import inf112.roborally.game.enums.Direction;
import inf112.roborally.game.enums.Rotate;
import inf112.roborally.game.objects.Flag;
import inf112.roborally.game.objects.GameObject;
import inf112.roborally.game.objects.LaserBeam;
import inf112.roborally.game.player.Player;
import inf112.roborally.game.objects.StartPosition;
import inf112.roborally.game.tools.TiledTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static inf112.roborally.game.tools.TiledTools.cellContainsKey;
import static inf112.roborally.game.tools.TiledTools.getValue;


@SuppressWarnings("Duplicates")
public class Board extends TiledBoard {

    protected ArrayList<Player> players;
    protected ArrayList<Flag> flags;
    protected ArrayList<LaserAnimation> lasers;
    protected ArrayList<LaserBeam> laserGuns;
    protected ArrayList<StartPosition> startPlates;

    // Need this one so we don't check for non existing music when sounds are muted/disposed
    private boolean soundIsMuted;


    public Board() {
        players = new ArrayList<>();
        flags = new ArrayList<>();
        lasers = new ArrayList<>();
        laserGuns = new ArrayList<>();
        startPlates = new ArrayList<>();
        soundIsMuted = false;
    }

    public ArrayList<LaserBeam> getLaserGuns() {
        return laserGuns;
    }

    public void findLaserGuns(){
        for (int x = 0; x < laserLayer.getWidth(); x++) {
            for (int y = 0; y < laserLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = laserLayer.getCell(x, y);
                if (cell != null)
                    laserGuns.add(new LaserBeam(x, y, Direction.valueOf(getValue(cell)), this));
            }
        }
    }

    public void findStartPlates() {
        for (int x = 0; x < startLayer.getWidth(); x++) {
            for (int y = 0; y < startLayer.getHeight(); y++) {
                TiledMapTileLayer.Cell cell = startLayer.getCell(x, y);
                if (cell != null) {
                    int value = Integer.parseInt(getValue(cell));
                    startPlates.add(new StartPosition(x, y, value));
                }
            }
        }
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void placePlayers() {
        findStartPlates();
        Collections.sort(startPlates);
        int startNumber = 0;
        for (Player currentPlayer : players) {
            currentPlayer.moveToPosition(startPlates.get(startNumber++).position);
            currentPlayer.setDirection(Direction.EAST);
            currentPlayer.updateSprite();
            currentPlayer.getBackup().moveToPlayerPosition();
        }
    }

    public void boardMoves() {
        expressBeltsMovePlayers();
        beltsMovePlayers();
        lasersFire();
        robotLasersFire();
        visitFlags();
        visitSpecialFields();
    }

    private void expressBeltsMovePlayers() {
        for (Player player : players) {
            if (player == null) continue;
            expressBeltsMove(player);
            if (player.isOffTheBoard(floorLayer)) {
                if (!soundIsMuted && !player.hasScreamed()) {
                    player.getSoundFromPlayer(2).play();
                }
                player.destroy();
            }
        }
    }

    private void beltsMovePlayers() {
        for (Player player : players) {
            if (player == null) continue;
            beltsMove(player);
            if (player.isOffTheBoard(floorLayer)) {
                if (!soundIsMuted && !player.hasScreamed()) {
                    player.getSoundFromPlayer(2).play();
                }
                player.destroy();
            }
        }
    }

    private void expressBeltsMove(Player player) {
        TiledMapTileLayer.Cell currentCell = beltLayer.getCell(player.getX(), player.getY());
        if (player.isOnExpressBelt(beltLayer)) {
            Direction beltDir = Direction.valueOf(getValue(currentCell));
            if (!player.canGo(beltDir, wallLayer) || player.crashWithRobot(beltDir, this)) return;

            player.moveInDirection(beltDir);
            currentCell = beltLayer.getCell(player.getX(), player.getY());
            if (!cellContainsKey(currentCell, "Rotate")) return;

            Iterator<Object> i = currentCell.getTile().getProperties().getValues();
            i.next();
            player.rotate(Rotate.valueOf(i.next().toString()));

        }
    }

    private void beltsMove(Player player) {
        TiledMapTileLayer.Cell currentCell = beltLayer.getCell(player.getX(), player.getY());
        if (cellContainsKey(currentCell, "Normal") || cellContainsKey(currentCell, "Express")) {

            Direction beltDir = Direction.valueOf(getValue(currentCell));
            if (!player.canGo(beltDir, wallLayer) || player.crashWithRobot(beltDir, this)) return;

            player.moveInDirection(beltDir);
            currentCell = beltLayer.getCell(player.getX(), player.getY());
            if (!cellContainsKey(currentCell, "Rotate")) return;

            Iterator<Object> i = currentCell.getTile().getProperties().getValues();
            i.next();
            player.rotate(Rotate.valueOf(i.next().toString()));
        }

        // Gyros rotate
        if (cellContainsKey(currentCell, "Gyro")) {
            player.rotate(Rotate.valueOf(getValue(currentCell)));
        }
    }

    public void lasersFire() {
        for (Player player : players) {
            if (player.hitByLaser(laserLayer)) {
                if (!soundIsMuted) {
                    player.getSoundFromPlayer(0).play();
                }
                player.takeDamage();
            }
        }
    }

    private void visitFlags() {
        for (Player player : players) {
            for (Flag flag : flags) {
                if (player.position.equals(flag.position)) {
                    player.visitFlag(flag.getFlagNumber());
                    player.getBackup().moveToPlayerPosition();
                }
            }
        }
    }

    private void visitSpecialFields() {
        for (Player player : players) {
            if (player.isOnRepair(floorLayer) || player.isOnOption(floorLayer)) {
                player.getBackup().moveToPlayerPosition();
            }
        }
    }


    public void cleanUp() {
        for (Player player : players) {
            if ((player.isOnRepair(floorLayer) || player.isOnOption(floorLayer)) && player.getDamage() > 0) {
                player.repairOneDamage();
                if (!soundIsMuted) {
                    player.getSoundFromPlayer(1).play();
                }
                addAnimation(new RepairAnimation(player.position));
            }
            if (player.isOnOption(floorLayer)) {
                System.out.println("Give option card to player!");
            }
        }
    }

    private void addAnimation(Animation animation) {
        ((RoboRallyGame) Gdx.app.getApplicationListener()).gameScreen.animations.add(animation);
    }

    public void drawGameObjects(SpriteBatch batch) {
        drawBackup(batch);
        drawList(players, batch);
        drawList(flags, batch);
    }

    public void drawBackup(SpriteBatch batch) {
        for (Player player : players) {
            player.getBackup().getSprite().draw(batch);
        }
    }

    private void drawList(ArrayList<? extends GameObject> list, SpriteBatch batch) {
        for (GameObject object : list)
            object.draw(batch);
    }

    public void killTheSound() {
        for (Player p : players) {
            p.killTheSound();
        }
        soundIsMuted = true;
    }

    public void restartTheSound() {
        for (Player p : players) {
            p.createSounds();
        }
        soundIsMuted = false;
    }

    public ArrayList<Player> getPlayers() {
        return this.players;
    }


    public void robotLasersFire() {
        for (Player player : players) {
            player.getLaserCannon().fire(this);
        }
    }

    public ArrayList<Flag> getFlags() {
        return flags;
    }
}

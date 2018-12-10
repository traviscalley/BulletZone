package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class Game {
    /**
     * Field dimensions
     */
    private static final int FIELD_DIM = 16;
    private final long id;
    private boolean isTank = true;
    //this will hold players and powerups and walls
    private final ArrayList<FieldHolder> holderGrid = new ArrayList<>();
    //this will hold what's "under" the holder grid, like water and hills
    private final ArrayList<FieldHolder> terrainGrid = new ArrayList<>();

    //list of playable instead???
    private final ConcurrentMap<Long, PlayableObject> players = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Long> playersIP = new ConcurrentHashMap<>();
    private final Object monitor = new Object();

    public Game() {
        this.id = 0;
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    /** isTank - gets boolean to determine if the player is playing as a tank or ship.
     *
     * @return isTank
     */
    public boolean isTank() { return isTank; }

    /** setTank - sets boolean to determine if player is a tank or ship.
     *
     * @param b - true for tank, false for ship
     */
    public void setTank(boolean b) {isTank = b;}

    @JsonIgnore
    public ArrayList<FieldHolder> getHolderGrid() {
        return holderGrid;
    }

    @JsonIgnore
    public ArrayList<FieldHolder> getTerrainGrid() {
        return terrainGrid;
    }

    /** addPlayer - adds a polymorhpic PlayableObject players collection.
     *
     * @param ip - player ip
     * @param obj - players object
     */
    public void addPlayer(String ip, PlayableObject obj)
    {
        synchronized (players)
        {
            players.put(obj.getId(), obj);
            playersIP.put(ip, obj.getId());
        }
    }

    /** getPlayer - gets the player's PlayableObject with the given id
     *
     * @param id - players id
     * @return PlayableObject - either a tank or ship
     */
    public PlayableObject getPlayer(int id) {
        return players.get(id);
    }

    /** getPlayers - gets players collection
     *
     * @return ConcurrentMap<Long, PlayableObject>
     */
    public ConcurrentMap<Long, PlayableObject> getPlayers() {
        return players;
    }

    /** getPlayer - returns a PlayableObject for the given IP, null if not present
     *
     * @param ip - players ip address
     * @return PlayableObject either a ship or tank
     */
    public PlayableObject getPlayer(String ip)
    {
        if (playersIP.containsKey(ip))
            return players.get(playersIP.get(ip));
        return null;
    }

    /** removePlayer - removes player from the game with the given id.
     *
     * @param id - players id
     */
    public void removePlayer(long id)
    {
        synchronized (players)
        {
            PlayableObject s = players.remove(id);
            if (s != null)
                playersIP.remove(s.getIp());
        }
    }

    public List<Optional<FieldEntity>> getGrid() {
        synchronized (holderGrid) {
            List<Optional<FieldEntity>> entities = new ArrayList<Optional<FieldEntity>>();

            FieldEntity entity;
            for (FieldHolder holder : holderGrid) {
                if (holder.isPresent()) {
                    entity = holder.getEntity();
                    entity = entity.copy();

                    entities.add(Optional.<FieldEntity>of(entity));
                } else {
                    entities.add(Optional.<FieldEntity>absent());
                }
            }
            return entities;
        }
    }

    public int[][] getGrid2D() {
        int[][] grid = new int[FIELD_DIM][FIELD_DIM];

        synchronized (holderGrid) {
            synchronized (terrainGrid) {
                FieldHolder holder;
                FieldHolder terrain;
                for (int i = 0; i < FIELD_DIM; i++) {
                    for (int j = 0; j < FIELD_DIM; j++) {
                        holder = holderGrid.get(i * FIELD_DIM + j);
                        terrain = terrainGrid.get(i * FIELD_DIM + j);
                        if (holder.isPresent()) {
                            grid[i][j] = holder.getEntity().getIntValue();
                        } else {
                            grid[i][j] = 0;
                        }

                        //this is gonna break everyting in a second
                        if (terrain.isPresent())
                            grid[i][j] += terrain.getEntity().getIntValue();
                    }
                }
            }
        }

        return grid;
    }
}

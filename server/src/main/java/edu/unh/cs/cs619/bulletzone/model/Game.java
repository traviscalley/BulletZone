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
    //this will hold players and powerups and walls
    private final ArrayList<FieldHolder> holderGrid = new ArrayList<>();
    //this will hold what's "under" the holder grid, like water and hills
    private final ArrayList<FieldHolder> terrainGrid = new ArrayList<>();

    private final ConcurrentMap<Long, Tank> tanks = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Long> playersIP = new ConcurrentHashMap<>();

    private final Object monitor = new Object();

    public Game() {
        this.id = 0;
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    @JsonIgnore
    public ArrayList<FieldHolder> getHolderGrid() {
        return holderGrid;
    }

    @JsonIgnore
    public ArrayList<FieldHolder> getTerrainGrid() {
        return terrainGrid;
    }


    public void addTank(String ip, Tank tank) {
        synchronized (tanks) {
            tanks.put(tank.getId(), tank);
            playersIP.put(ip, tank.getId());
        }
    }

    public Tank getTank(int tankId) {
        return tanks.get(tankId);
    }

    public ConcurrentMap<Long, Tank> getTanks() {
        return tanks;
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

    public Tank getTank(String ip){
        if (playersIP.containsKey(ip)){
            return tanks.get(playersIP.get(ip));
        }
        return null;
    }

    public void removeTank(long tankId){
        synchronized (tanks) {
            Tank t = tanks.remove(tankId);
            if (t != null) {
                playersIP.remove(t.getIp());
            }
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

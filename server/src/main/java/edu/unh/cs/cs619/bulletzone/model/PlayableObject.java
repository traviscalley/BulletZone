package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Stack;

import edu.unh.cs.cs619.bulletzone.powerup.PowerRack;
import edu.unh.cs.cs619.bulletzone.powerup.Powerup;

public abstract class PlayableObject extends FieldEntity {
    private static final String TAG = "AbstractPlayable";

    protected final long id;
    protected final String ip;

    protected long lastMoveTime;
    protected int allowedMoveInterval;

    protected long lastTurnTime;
    protected int allowedTurnInterval;

    protected long lastFireTime;
    protected int allowedFireInterval;

    protected int numberOfBullets;
    protected int allowedNumberOfBullets;

    protected int life;

    protected Direction direction;

    //just one powerup for now
    protected Powerup powerup;
    protected Stack<PlayableConfig> stateStack = new Stack<>();

    public PlayableObject(long id, Direction direction, String ip) {
        this.id = id;
        this.direction = direction;
        this.ip = ip;
    }

    @Override
    public FieldEntity copy() {
        return new Tank(id, direction, ip);
    }

    @Override
    public void hit(int damage) {
        life = life - damage;
        System.out.println("PlayableObject life: " + id + " : " + life);
    }

    public long getLastMoveTime() {
        return lastMoveTime;
    }

    public long getLastTurnTime() {
        return lastTurnTime;
    }

    public void setLastTurnTime(long lastMoveTime) {
        this.lastTurnTime = lastTurnTime;
    }

    public long getAllowedTurnInterval() {
        return allowedTurnInterval;
    }

    public void setAllowedTurnInterval(int allowedTurnInterval) {
        this.allowedTurnInterval = allowedTurnInterval;
    }




    public void setLastMoveTime(long lastMoveTime) {
        this.lastMoveTime = lastMoveTime;
    }

    public long getAllowedMoveInterval() {
        return allowedMoveInterval;
    }

    public void setAllowedMoveInterval(int allowedMoveInterval) {
        this.allowedMoveInterval = allowedMoveInterval;
    }

    public long getLastFireTime() {
        return lastFireTime;
    }

    public void setLastFireTime(long lastFireTime) {
        this.lastFireTime = lastFireTime;
    }

    public long getAllowedFireInterval() {
        return allowedFireInterval;
    }

    public void setAllowedFireInterval(int allowedFireInterval) {
        this.allowedFireInterval = allowedFireInterval;
    }

    public int getNumberOfBullets() {
        return numberOfBullets;
    }

    public void setNumberOfBullets(int numberOfBullets) {
        this.numberOfBullets = numberOfBullets;
    }

    public int getAllowedNumberOfBullets() {
        return allowedNumberOfBullets;
    }

    public void setAllowedNumberOfBullets(int allowedNumberOfBullets) {
        this.allowedNumberOfBullets = allowedNumberOfBullets;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @JsonIgnore
    public long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "P";
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public String getIp(){
        return ip;
    }

    @Override
    public boolean powerupSpawnable(){return false;}

    public void addPowerup(Powerup power){
        if(powerup instanceof PowerRack) {//complex cas
            if(((PowerRack) powerup).addPowerup(power)){ //if there is room to add, it is added
                power.powerupPlayer(this);
            }
        }
        else {//simple case
            //if(powerup != null)
            //powerup.unpowerupPlayer(this);
            if(!stateStack.empty())
                setConfig(stateStack.pop());
            powerup = power;
            powerup.powerupPlayer(this);
            stateStack.push(makeConfig());
        }
    }

    //called by eject
    public void removePowerup(){
        if(!stateStack.empty()) {
            if(powerup instanceof PowerRack) {
                ((PowerRack) powerup).removePowerup();
                //lord save you

            }
            setConfig(stateStack.pop());
        }
        powerup = null;
    }

    private void setConfig(PlayableConfig config)
    {
        allowedMoveInterval = config.allowedMoveInterval;
        allowedTurnInterval = config.allowedTurnInterval;
        allowedFireInterval = config.allowedFireInterval;
        allowedNumberOfBullets = config.allowedNumberOfBullets;
    }

    private PlayableConfig makeConfig(){
        return new PlayableConfig(allowedMoveInterval, allowedTurnInterval,
                allowedFireInterval, allowedNumberOfBullets);
    }

    private class PlayableConfig{
        public int allowedMoveInterval;
        public int allowedTurnInterval;
        public int allowedFireInterval;
        public int allowedNumberOfBullets;

        public PlayableConfig(int move, int turn, int fire, int bullets){
            allowedMoveInterval = move;
            allowedTurnInterval = turn;
            allowedFireInterval = fire;
            allowedNumberOfBullets = bullets;
        }
    }

}

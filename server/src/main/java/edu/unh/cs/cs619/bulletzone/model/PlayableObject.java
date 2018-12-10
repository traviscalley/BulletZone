package edu.unh.cs.cs619.bulletzone.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.Stack;

import edu.unh.cs.cs619.bulletzone.powerup.PowerRack;
import edu.unh.cs.cs619.bulletzone.powerup.Powerup;

public abstract class PlayableObject extends FieldEntity {
    private static final String TAG = "AbstractPlayable";

    protected Game game;

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
    protected Stack<Powerup> powerupStack = new Stack<>();
    protected int powerupN = 0;
    protected int powerLimit = 1;

    protected Stack<PlayableConfig> stateStack = new Stack<>();
    //protected PlayableConfig initState;

    public PlayableObject(long id, Direction direction, String ip, Game g) {
        this.id = id;
        this.direction = direction;
        this.ip = ip;
        this.game = g;
        //initState = makeConfig();

    }

    @Override
    public FieldEntity copy() {
        return new Tank(id, direction, ip, game);
    }

    @Override
    public void hit(Bullet bullet) {
        if (id == bullet.getBulletId()) {
            bullet.setDamage(0);
        }
        life -= bullet.getDamage();

        if (life <= 0) {
            parent.clearField();
            parent = null;
            game.removeTank(id);
        }
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

    public void setPowerupLimit(int powerLimit){
        this.powerLimit = powerLimit;
    }

    public int getPowerupLimit(){
        return this.powerLimit;
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

    @Override
    public boolean tankSpawnable(){
        return false;
    }

    public void addPowerup(Powerup power){
        if(powerLimit == 1) {
            if(powerupN != 0)
                remove1Powerup();
            applyPowerup(power);
        }
        else{
            if(powerupN < powerLimit)
                applyPowerup(power);
        }
    }

    private void applyPowerup(Powerup powerup){
        powerup.powerupPlayer(this);
        powerupStack.push(powerup);
        powerupN++;
        stateStack.push(makeConfig());
    }

    //called by eject and when it gets hit
    public boolean remove1Powerup(){
        if(powerupN != 0) {
            stateStack.pop();
            setConfig(stateStack.peek());
            powerupStack.pop();
            powerupN--;
            return true;
        }
        return false;
    }

    protected void setConfig(PlayableConfig config) {
        allowedMoveInterval = config.allowedMoveInterval;
        allowedTurnInterval = config.allowedTurnInterval;
        allowedFireInterval = config.allowedFireInterval;
        allowedNumberOfBullets = config.allowedNumberOfBullets;
        powerLimit = config.powerLimit;
    }

    protected PlayableConfig makeConfig(){
        return new PlayableConfig(allowedMoveInterval, allowedTurnInterval,
                allowedFireInterval, allowedNumberOfBullets, powerLimit);
    }

    private class PlayableConfig{
        public int allowedMoveInterval;
        public int allowedTurnInterval;
        public int allowedFireInterval;
        public int allowedNumberOfBullets;
        public int powerLimit;

        public PlayableConfig(int move, int turn, int fire, int bullets, int plimit){
            allowedMoveInterval = move;
            allowedTurnInterval = turn;
            allowedFireInterval = fire;
            allowedNumberOfBullets = bullets;
            powerLimit = plimit;
        }
    }

}

package com.mygdx.app.game;

import java.util.Date;

public class GlobalStatistic {
    private static GlobalStatistic ourInstance = new GlobalStatistic();
    public static GlobalStatistic getInstance() {
        return ourInstance;
    }
    private long totalTimeOfGame;

    private int totalScore;
    private int totalShots;


    public GlobalStatistic() {
        this.totalScore = 0;
        this.totalShots = 0;
        this.totalTimeOfGame = new Date().getTime();
    }

    public void addShot(){
        totalShots++;
    }


    public void addTotalScore(int amount){
        totalScore += amount;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getTotalShots() {
        return totalShots;
    }

    public long getTotalTimeOfGame() {
        return (new Date().getTime() - totalTimeOfGame);
    }
}

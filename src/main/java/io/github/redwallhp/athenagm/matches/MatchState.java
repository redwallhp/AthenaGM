package io.github.redwallhp.athenagm.matches;


public enum MatchState {


    WAITING(),
    PLAYING(),
    ENDED();


    public String toString() {
        String s = this.name().toLowerCase();
        s = s.substring(0, 1).toUpperCase() + s.substring(1);;
        return s;
    }

}

package com.ucldraw;

public record FootballTeam(String code, String name, String country, String group) {

    public static FootballTeam newTeam(String code, String name, String country, String group) {
        return new FootballTeam(code, name, country, group);
    }


    @Override
    public String toString() {
        return "[" + code + "] " + name + " (" + country + ") " + group;
    }

}

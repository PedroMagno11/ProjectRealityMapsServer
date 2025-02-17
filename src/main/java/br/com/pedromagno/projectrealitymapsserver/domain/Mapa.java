package br.com.pedromagno.projectrealitymapsserver.domain;

import org.springframework.hateoas.RepresentationModel;

import java.util.List;

public class Map extends RepresentationModel<Map> {
    private String name;
    private List<Tile> tiles;

    public Map() {

    }

    public Map(String name, List<Tile> tiles) {
        this.name = name;
        this.tiles = tiles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public void setTiles(List<Tile> tiles) {
        this.tiles = tiles;
    }
}
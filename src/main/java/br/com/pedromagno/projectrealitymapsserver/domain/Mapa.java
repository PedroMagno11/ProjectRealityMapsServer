package br.com.pedromagno.projectrealitymapsserver.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Mapa extends RepresentationModel<Mapa> {
    private String nome;
    private List<String> tiles;

    public Mapa() {

    }

    public Mapa(String name, List<String> tiles) {
        this.nome = name;
        this.tiles = tiles;
    }

    public Mapa(String nome) {
        this.nome = nome;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<String> getTiles() {
        return tiles;
    }

    public void setTiles(List<String> tiles) {
        this.tiles = tiles;
    }
}
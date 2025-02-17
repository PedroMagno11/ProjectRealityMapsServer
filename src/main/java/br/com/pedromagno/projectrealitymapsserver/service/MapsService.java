package br.com.pedromagno.projectrealitymapsserver.service;

import br.com.pedromagno.projectrealitymapsserver.domain.Mapa;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class MapsService {
    @Value("${maps.address}")
    private String MAPS_PATH;


    public Page<Mapa> listarMapas(Pageable pageable){
        File maps = new File(MAPS_PATH);
        List<Mapa> listaDeMapas = new ArrayList<>();

        String[] mapas = maps.list(new FilenameFilter(){
            @Override
            public boolean accept(File diretorioAtual, String nomeDoMapa) {
                return new File(diretorioAtual, nomeDoMapa).isDirectory();
            }
        });

        if (Objects.isNull(mapas)){
            throw new RuntimeException("Mapas não encontrados");
        }

        for (String mapa: mapas){
                listaDeMapas.add(buscarMapa(mapa));
        }

        // Cria uma sublista baseada na página solicitada
        int inicio = (int) pageable.getOffset();
        int fim = Math.min(inicio + pageable.getPageSize(), listaDeMapas.size());
        List<Mapa> sublistaMapas = listaDeMapas.subList(inicio, fim);
        return new PageImpl<>(sublistaMapas, pageable, sublistaMapas.size());
    }

    public Mapa buscarMapa(String mapa){

        List<String> tilesUrl = new ArrayList<>();
        File diretorioMapa = new File(MAPS_PATH, mapa);

        if (diretorioMapa.exists() && diretorioMapa.isDirectory()) {

            for (File diretorioZoom : diretorioMapa.listFiles()) {
                if (diretorioZoom.isDirectory()) {
                    for (File diretorioX : diretorioZoom.listFiles()) {

                        if (diretorioX.isDirectory()) {

                            for (File tile : diretorioX.listFiles()) {

                                if (tile.isFile()) {
                                     if (tile.isFile()) {
                                            String url = String.format("http://localhost:8080/api/mapas/%s/%s/%s/%s",
                                                    mapa, diretorioZoom.getName(), diretorioX.getName(), tile.getName().substring(0,tile.getName().lastIndexOf('.')));
                                            tilesUrl.add(url);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        return new Mapa(mapa, tilesUrl);
    }

    public Resource getTile(String mapa, String zoom, String x, String y) throws IOException {
        Path tilePath = Paths.get(MAPS_PATH, mapa, zoom, x, y + ".jpg").toAbsolutePath().normalize();
        Path diretorioRequisitado = tilePath.toAbsolutePath().normalize();

        if(!diretorioRequisitado.startsWith(Paths.get(MAPS_PATH).toAbsolutePath().normalize())){
            throw new IOException("Você não tem permissão para acessar este diretório");
        }

        Resource resource = new UrlResource(tilePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new IOException("Tile não encontrado!");
        }

        return resource;
    }

}

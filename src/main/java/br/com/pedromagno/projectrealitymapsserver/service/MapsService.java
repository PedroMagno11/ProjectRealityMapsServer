package br.com.pedromagno.projectrealitymapsserver.service;

import br.com.pedromagno.projectrealitymapsserver.domain.Mapa;
import br.com.pedromagno.projectrealitymapsserver.util.FileUtils;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MapsService {
    @Value("${maps.address}")
    private String MAPS_PATH;

    @Value("${server.address}")
    private String SERVER_ADDRESS;

    @Value("${server.port}")
    private String SERVER_PORT;


    public Page<Mapa> listarMapas(Pageable pageable){
        File diretorioMapas = new File(MAPS_PATH);

        // Verifica se o diretorio de mapas existe ou se é mesmo um diretório, se não for, lança uma exceção
        if(!diretorioMapas.exists() || !diretorioMapas.isDirectory()){
            throw new RuntimeException("Diretorio de mapas não encontrado!");
        }

        List<Mapa> listaDeMapas =
                Arrays.stream(FileUtils.listarDiretorios(diretorioMapas))
                // Pega o nome de cada mapa
                .map(File::getName)
                // Busca por cada mapa
                .map(this::buscarMapa)
                // Transforma essa coleção numa lista
                .collect(Collectors.toList());

        int inicio = (int)pageable.getOffset();
        int fim = Math.min(inicio + pageable.getPageSize(), listaDeMapas.size());

        List<Mapa> sublistaMapas = listaDeMapas.subList(inicio, fim);
        return new PageImpl<>(sublistaMapas, pageable, sublistaMapas.size());
    }

    public Mapa buscarMapa(String mapa){

        File diretorioMapa = new File(MAPS_PATH, mapa);

        // Se o diretorio de mapas não existir ou não for um diretório, irá retornar um mapa sem tiles
        if (!diretorioMapa.exists() || !diretorioMapa.isDirectory()) {
            return new Mapa(mapa, Collections.emptyList());
        }

        List<String> tilesUrl = new ArrayList<>();

            for (File diretorioZoom : diretorioMapa.listFiles(File::isDirectory)) {
                for (File diretorioX : diretorioZoom.listFiles(File::isDirectory)) {
                    for (File tile : diretorioX.listFiles(File::isFile)) {
                        tilesUrl.add(construtorUrl(mapa, diretorioZoom.getName(), diretorioX.getName(), tile.getName()));
                    }
                }
            }

        return new Mapa(mapa, tilesUrl);
    }

    /**
     * Essa função tem por finalidade contruir a url que será responsável por retornar uma tile
     * @param mapa
     * @param zoom
     * @param x
     * @param y -> é o nome do tile no formato jpg
     * @return String
     */
    private String construtorUrl(String mapa, String zoom, String x, String y) {
        String tileSemExtensao = y.substring(0, y.lastIndexOf("."));
        return String.format("http://%s:%s/api/mapas/%s/%s/%s/%s",SERVER_ADDRESS, SERVER_PORT, mapa, zoom, x, tileSemExtensao);
    }

    public Resource getTile(String mapa, String zoom, String x, String y) throws IOException {
        Path tilePath = Paths.get(MAPS_PATH, mapa, zoom, x, y + ".jpg").toAbsolutePath().normalize();

        if(!tilePath.startsWith(Paths.get(MAPS_PATH).toAbsolutePath().normalize())){
            throw new IOException("Você não tem permissão para acessar este diretório");
        }

        Resource resource = new UrlResource(tilePath.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new IOException("Tile não encontrado!");
        }

        return resource;
    }

}

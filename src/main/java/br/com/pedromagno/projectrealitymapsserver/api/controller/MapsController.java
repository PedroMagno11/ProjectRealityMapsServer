package br.com.pedromagno.projectrealitymapsserver.api.controller;

import br.com.pedromagno.projectrealitymapsserver.service.MapsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import br.com.pedromagno.projectrealitymapsserver.domain.Mapa;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/mapas")
public class MapsController {

    @Value("${maps.address}")
    private String BASE_DIR;

    private final MapsService mapsService;

    public MapsController(MapsService mapsService) {
        this.mapsService = mapsService;
    }

    @Operation(summary = "Obtém todos os mapas")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "Mapas encontrados",
            content = {
                    @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Mapa.class))
            }),
            @ApiResponse(responseCode = "404", description = "Mapas não encontrados", content = @Content)
    })
    @GetMapping()
    public PageImpl<EntityModel<Mapa>> listarMapas(@RequestParam(defaultValue = "0") int pagina, @RequestParam(defaultValue = "10") int quantidadeDeMapas) {
        Pageable pageable = PageRequest.of(pagina, quantidadeDeMapas);

        List<EntityModel<Mapa>> mapas = mapsService.listarMapas(pageable)
                .stream()
                .map(mapa -> {

                    EntityModel<Mapa> entityMapa = EntityModel.of(mapa,
                            linkTo(methodOn(MapsController.class).getMapa(mapa.getNome())).withRel(mapa.getNome()));

                    return entityMapa;
                }).collect(Collectors.toList());

        return new PageImpl<>(mapas, pageable, 10);
    }

    @Operation(summary = "Obtém mapa pelo nome")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "Mapa encontrado",
            content = {
                    @Content(mediaType = "application/json",
                    schema = @Schema(implementation = Mapa.class))
            }),
            @ApiResponse(responseCode = "404", description = "Mapa não encontrado",
            content = @Content)
    })
    @GetMapping("/{mapa}")
    public EntityModel<Mapa> getMapa(@PathVariable String mapa) {
        Mapa map = mapsService.buscarMapa(mapa);

        return EntityModel.of(map, linkTo(methodOn(MapsController.class).getMapa(map.getNome())).withSelfRel());
    }

    @Operation(summary = "Obtém os tiles do mapa")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "Tile encontrado",
            content = {
                    @Content(mediaType = "image/jpeg")
            }),
            @ApiResponse(responseCode = "404", description = "Tile não encontrado",
            content = @Content)
    })
    @GetMapping("/{mapa}/{zoom}/{x}/{y}")
    public ResponseEntity getTile(@PathVariable String mapa, @PathVariable String zoom,
                                            @PathVariable String x, @PathVariable String y) {
        try {
            Resource resource = mapsService.getTile(mapa, zoom, x, y);
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
            headers.add(HttpHeaders.CONTENT_TYPE, "image/jpeg");

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}

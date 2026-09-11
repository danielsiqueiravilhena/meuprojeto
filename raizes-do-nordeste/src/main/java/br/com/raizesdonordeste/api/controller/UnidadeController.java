package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.domain.entity.Unidade;
import br.com.raizesdonordeste.domain.repository.UnidadeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/unidades")
public class UnidadeController {

    private final UnidadeRepository unidadeRepository;

    public UnidadeController(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }

    @GetMapping
    public ResponseEntity<List<Unidade>> listar() {
        return ResponseEntity.ok(unidadeRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Unidade> buscarPorId(@PathVariable Long id) {
        return unidadeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Unidade> criar(@RequestBody Unidade unidade) {
        Unidade novaUnidade = unidadeRepository.save(unidade);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaUnidade);
    }
}
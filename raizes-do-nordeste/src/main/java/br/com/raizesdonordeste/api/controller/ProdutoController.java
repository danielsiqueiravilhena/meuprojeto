package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.domain.entity.Produto;
import br.com.raizesdonordeste.domain.repository.ProdutoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/produtos")
@Tag(
        name = "Produtos",
        description = "Cadastro e consulta de produtos"
)
public class ProdutoController {

    private final ProdutoRepository produtoRepository;

    public ProdutoController(
            ProdutoRepository produtoRepository) {

        this.produtoRepository = produtoRepository;
    }

    @Operation(
            summary = "Listar produtos",
            description = "Lista todos os produtos cadastrados."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Produtos encontrados com sucesso"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente ou inválido"
            )
    })
    @GetMapping
    public ResponseEntity<List<Produto>> listar() {

        return ResponseEntity.ok(
                produtoRepository.findAll()
        );
    }

    @Operation(
            summary = "Buscar produto por ID",
            description = "Busca um produto específico pelo identificador."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Produto encontrado"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente ou inválido"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto não encontrado"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(

            @Parameter(
                    description = "ID do produto",
                    example = "1"
            )
            @PathVariable Long id) {

        return produtoRepository
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }

    @Operation(
            summary = "Cadastrar produto",
            description = "Cadastra um novo produto no sistema."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Produto cadastrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente ou inválido"
            )
    })
    @PostMapping
    public ResponseEntity<Produto> criar(
            @RequestBody Produto produto) {

        Produto novoProduto =
                produtoRepository.save(produto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(novoProduto);
    }
}
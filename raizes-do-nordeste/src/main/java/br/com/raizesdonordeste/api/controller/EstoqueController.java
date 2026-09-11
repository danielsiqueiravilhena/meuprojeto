package br.com.raizesdonordeste.api.controller;

import br.com.raizesdonordeste.domain.entity.Estoque;
import br.com.raizesdonordeste.domain.entity.Produto;
import br.com.raizesdonordeste.domain.entity.Unidade;
import br.com.raizesdonordeste.domain.repository.EstoqueRepository;
import br.com.raizesdonordeste.domain.repository.ProdutoRepository;
import br.com.raizesdonordeste.domain.repository.UnidadeRepository;

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
@RequestMapping("/estoque")
@Tag(
        name = "Estoque",
        description = "Consulta e atualização do estoque de produtos por unidade"
)
public class EstoqueController {

    private final EstoqueRepository estoqueRepository;
    private final ProdutoRepository produtoRepository;
    private final UnidadeRepository unidadeRepository;

    public EstoqueController(
            EstoqueRepository estoqueRepository,
            ProdutoRepository produtoRepository,
            UnidadeRepository unidadeRepository) {

        this.estoqueRepository = estoqueRepository;
        this.produtoRepository = produtoRepository;
        this.unidadeRepository = unidadeRepository;
    }

    @Operation(
            summary = "Listar estoque",
            description = "Lista os registros de estoque por unidade e produto."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Estoque consultado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente ou inválido"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário sem permissão"
            )
    })
    @GetMapping
    public ResponseEntity<List<Estoque>> listar() {

        return ResponseEntity.ok(
                estoqueRepository.findAll()
        );
    }

    @Operation(
            summary = "Buscar estoque por ID",
            description = "Busca um registro específico de estoque."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Registro de estoque encontrado"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente ou inválido"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário sem permissão"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Registro de estoque não encontrado"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<Estoque> buscarPorId(

            @Parameter(
                    description = "ID do registro de estoque",
                    example = "1"
            )
            @PathVariable Long id) {

        return estoqueRepository
                .findById(id)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity.notFound().build()
                );
    }

    @Operation(
            summary = "Cadastrar estoque",
            description =
                    "Cria um registro de estoque associado a um produto e a uma unidade existentes."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Estoque cadastrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Produto, unidade ou quantidade não informados corretamente"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente ou inválido"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário sem permissão"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Produto ou unidade não encontrados"
            )
    })
    @PostMapping
    public ResponseEntity<?> criar(
            @RequestBody Estoque estoque) {

        if (estoque.getProduto() == null ||
                estoque.getProduto().getId() == null) {

            return ResponseEntity
                    .badRequest()
                    .body("O produto deve ser informado.");
        }

        if (estoque.getUnidade() == null ||
                estoque.getUnidade().getId() == null) {

            return ResponseEntity
                    .badRequest()
                    .body("A unidade deve ser informada.");
        }

        if (estoque.getQuantidade() == null ||
                estoque.getQuantidade() < 0) {

            return ResponseEntity
                    .badRequest()
                    .body("A quantidade deve ser maior ou igual a zero.");
        }

        Long produtoId =
                estoque.getProduto().getId();

        Long unidadeId =
                estoque.getUnidade().getId();

        Produto produto =
                produtoRepository
                        .findById(produtoId)
                        .orElse(null);

        if (produto == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Produto não encontrado.");
        }

        Unidade unidade =
                unidadeRepository
                        .findById(unidadeId)
                        .orElse(null);

        if (unidade == null) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Unidade não encontrada.");
        }

        if (!unidade.getAtiva()) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("A unidade informada está inativa.");
        }

        if (estoqueRepository
                .findByUnidadeIdAndProdutoId(
                        unidadeId,
                        produtoId
                )
                .isPresent()) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            "Já existe estoque cadastrado para este produto nesta unidade."
                    );
        }

        estoque.setProduto(produto);
        estoque.setUnidade(unidade);

        Estoque novoEstoque =
                estoqueRepository.save(estoque);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(novoEstoque);
    }

    @Operation(
            summary = "Atualizar quantidade em estoque",
            description =
                    "Atualiza a quantidade disponível de um produto em uma unidade."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Estoque atualizado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Quantidade inválida"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token ausente ou inválido"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Usuário sem permissão"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Registro de estoque não encontrado"
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(

            @Parameter(
                    description = "ID do registro de estoque",
                    example = "1"
            )
            @PathVariable Long id,

            @RequestBody Estoque estoqueAtualizado) {

        Estoque estoque =
                estoqueRepository
                        .findById(id)
                        .orElse(null);

        if (estoque == null) {

            return ResponseEntity
                    .notFound()
                    .build();
        }

        if (estoqueAtualizado.getQuantidade() == null ||
                estoqueAtualizado.getQuantidade() < 0) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "A quantidade deve ser maior ou igual a zero."
                    );
        }

        estoque.setQuantidade(
                estoqueAtualizado.getQuantidade()
        );

        Estoque estoqueSalvo =
                estoqueRepository.save(estoque);

        return ResponseEntity.ok(
                estoqueSalvo
        );
    }
}
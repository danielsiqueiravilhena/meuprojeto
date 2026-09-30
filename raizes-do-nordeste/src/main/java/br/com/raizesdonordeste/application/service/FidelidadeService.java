package br.com.raizesdonordeste.application.service;

import br.com.raizesdonordeste.domain.entity.Fidelidade;
import br.com.raizesdonordeste.domain.entity.HistoricoFidelidade;
import br.com.raizesdonordeste.domain.repository.FidelidadeRepository;
import br.com.raizesdonordeste.domain.repository.HistoricoFidelidadeRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FidelidadeService {

    private final FidelidadeRepository fidelidadeRepository;
    private final HistoricoFidelidadeRepository historicoFidelidadeRepository;

    public FidelidadeService(
            FidelidadeRepository fidelidadeRepository,
            HistoricoFidelidadeRepository historicoFidelidadeRepository) {

        this.fidelidadeRepository = fidelidadeRepository;
        this.historicoFidelidadeRepository = historicoFidelidadeRepository;
    }

    // CONSULTAR FIDELIDADE

    public Fidelidade consultar(Long clienteId) {

        return fidelidadeRepository.findByClienteId(clienteId)
                .orElseGet(() ->
                        fidelidadeRepository.save(
                                new Fidelidade(
                                        clienteId,
                                        0,
                                        false
                                )
                        )
                );
    }

    // REGISTRAR CONSENTIMENTO LGPD

    public Fidelidade registrarConsentimento(
            Long clienteId,
            Boolean consentimento) {

        if (consentimento == null) {

            throw new IllegalArgumentException(
                    "O consentimento LGPD deve ser informado."
            );
        }

        Fidelidade fidelidade = consultar(clienteId);

        fidelidade.setConsentimentoLgpd(consentimento);

        return fidelidadeRepository.save(fidelidade);
    }

    // ADICIONAR PONTOS

    public Fidelidade adicionarPontos(
            Long clienteId,
            Integer pontos) {

        if (pontos == null || pontos <= 0) {

            throw new IllegalArgumentException(
                    "A quantidade de pontos deve ser maior que zero."
            );
        }

        Fidelidade fidelidade = consultar(clienteId);

        fidelidade.setPontos(
                fidelidade.getPontos() + pontos
        );

        Fidelidade fidelidadeSalva =
                fidelidadeRepository.save(fidelidade);

        HistoricoFidelidade historico =
                new HistoricoFidelidade(
                        clienteId,
                        pontos,
                        "Pagamento aprovado"
                );

        historicoFidelidadeRepository.save(historico);

        return fidelidadeSalva;
    }

    // RESGATAR PONTOS

    public Fidelidade resgatarPontos(
            Long clienteId,
            Integer pontos) {

        if (pontos == null || pontos <= 0) {

            throw new IllegalArgumentException(
                    "A quantidade de pontos para resgate deve ser maior que zero."
            );
        }

        Fidelidade fidelidade = consultar(clienteId);

        if (!Boolean.TRUE.equals(
                fidelidade.getConsentimentoLgpd())) {

            throw new IllegalArgumentException(
                    "O cliente deve possuir consentimento LGPD para resgatar pontos."
            );
        }

        if (fidelidade.getPontos() < pontos) {

            throw new IllegalArgumentException(
                    "Saldo de pontos insuficiente para o resgate."
            );
        }

        fidelidade.setPontos(
                fidelidade.getPontos() - pontos
        );

        Fidelidade fidelidadeSalva =
                fidelidadeRepository.save(fidelidade);

        HistoricoFidelidade historico =
                new HistoricoFidelidade(
                        clienteId,
                        -pontos,
                        "Resgate de pontos"
                );

        historicoFidelidadeRepository.save(historico);

        return fidelidadeSalva;
    }

    // CONSULTAR HISTÓRICO

    public List<HistoricoFidelidade> consultarHistorico(
            Long clienteId) {

        return historicoFidelidadeRepository
                .findByClienteIdOrderByDataHoraDesc(clienteId);
    }

    // REGISTRAR HISTÓRICO SEM ALTERAR O SALDO

    public HistoricoFidelidade registrarHistorico(
            Long clienteId,
            Integer pontos,
            String motivo) {

        if (pontos == null || pontos <= 0) {

            throw new IllegalArgumentException(
                    "A quantidade de pontos deve ser maior que zero."
            );
        }

        HistoricoFidelidade historico =
                new HistoricoFidelidade(
                        clienteId,
                        pontos,
                        motivo
                );

        return historicoFidelidadeRepository.save(historico);
    }

    // EXCLUIR REGISTRO DO HISTÓRICO

    public void excluirHistorico(Long id) {

        if (!historicoFidelidadeRepository.existsById(id)) {

            throw new IllegalArgumentException(
                    "Registro de histórico não encontrado."
            );
        }

        historicoFidelidadeRepository.deleteById(id);
    }
}
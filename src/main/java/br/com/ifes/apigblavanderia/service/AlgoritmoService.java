package br.com.ifes.apigblavanderia.service;

import br.com.ifes.apigblavanderia.domain.Cromossomo;
import br.com.ifes.apigblavanderia.domain.Maquina;
import br.com.ifes.apigblavanderia.domain.OrdemProcesso;
import br.com.ifes.apigblavanderia.domain.Processo;
import br.com.ifes.apigblavanderia.repository.MaquinaRepository;
import br.com.ifes.apigblavanderia.repository.OrdemProcessoRepository;
import br.com.ifes.apigblavanderia.repository.ProcessoRepository;
import br.com.ifes.apigblavanderia.service.util.AlgoritimoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlgoritmoService {

    public static final Integer PORCENTAGEM_CRUZAMENTO = 20;
    public static final Integer PORCENTAGEM_MUTACAO = 20;

    private List<OrdemProcesso> ordemProcessos = new ArrayList<>();
    private List<Processo> processos = new ArrayList<>();
    private List<Maquina> maquinas = new ArrayList<>();

    private final OrdemProcessoRepository ordemProcessoRepository;
    private final ProcessoRepository processoRepository;
    private final MaquinaRepository maquinaRepository;
    private final PopulacaoService populacaoService;
    private final CruzamentoService cruzamentoService;
    private final SelecaoService selecaoService;
    private final MutacaoService mutacaoService;

    public void abasteceTodasBasesDeDados() {
        processos = processoRepository.abasteceBaseDados();
        ordemProcessos = ordemProcessoRepository.abasteceBaseDados();
        maquinas = maquinaRepository.abasteceBaseDados();

        ordemProcessos.forEach(op -> op.setProcessos(getProcessosOP(op.getId())));
        maquinas.forEach(op -> op.setProcessosQueRealiza(getProcessosMaquina(op.getId())));

        log.info("Bases de dados preenchidas com sucesso!");
    }

    private List<Processo> getProcessosOP(Integer ordemProcessoId) {
        return processos.stream()
                .filter(processo -> processo.getOrdemProcessoId().equals(ordemProcessoId))
                .collect(Collectors.toList());
    }

    private List<Processo> getProcessosMaquina(Integer maquinaId) {
        return processos.stream()
                .filter(processo -> processo.getMaquinaId().equals(maquinaId))
                .collect(Collectors.toList());
    }

    public Long evolucao(Integer tamanhoInicialPopulacao, Integer evolucoes) {
        abasteceTodasBasesDeDados();
        List<Cromossomo> populacao = populacaoService.inicializaPopulacao(ordemProcessos, maquinas, tamanhoInicialPopulacao);
        AtomicInteger tamanhoPopulacao = new AtomicInteger(0);

        for (int iteracao = 0; iteracao < evolucoes; iteracao++) {
            tamanhoPopulacao.set(populacao.size());

            while (tamanhoPopulacao.get() < (tamanhoPopulacao.get() * 2)) {
                List<Cromossomo> pais = new ArrayList<>(selecaoService.ranking(populacao, 5));
                processoSelecaoParaAdicionarNovoIndividuoNaPopulacao(populacao, pais);
                tamanhoPopulacao.set(populacao.size());
            }

            AlgoritimoUtil.ordenaPorMelhorAvaliacao(populacao);
            populacao = populacaoService.miLambda(populacao, tamanhoPopulacao.get());
        }

        return populacao.get(0).getAvaliacao();
    }

    private void processoSelecaoParaAdicionarNovoIndividuoNaPopulacao(List<Cromossomo> populacao, List<Cromossomo> pais) {
        if (AlgoritimoUtil.sortearPorcentagem() > PORCENTAGEM_CRUZAMENTO) {
            Cromossomo filho = cruzamentoService.crossoverBaseadoEmMaioria(pais);

            if (AlgoritimoUtil.sortearPorcentagem() <= PORCENTAGEM_MUTACAO) {
                mutacaoService.mutacao(filho);
            }

            //Implementar método para parar a conversão genética

            populacao.add(filho);
            return;
        }

        AlgoritimoUtil.ordenaPorMelhorAvaliacao(pais);
        pais.stream().findFirst().ifPresent(populacao::add);
    }
}

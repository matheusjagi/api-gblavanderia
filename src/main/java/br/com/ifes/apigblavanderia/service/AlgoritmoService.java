package br.com.ifes.apigblavanderia.service;

import br.com.ifes.apigblavanderia.domain.Cromossomo;
import br.com.ifes.apigblavanderia.domain.Maquina;
import br.com.ifes.apigblavanderia.domain.OrdemProcesso;
import br.com.ifes.apigblavanderia.domain.Processo;
import br.com.ifes.apigblavanderia.repository.CapacidadeMaquinaRepository;
import br.com.ifes.apigblavanderia.repository.MaquinaRepository;
import br.com.ifes.apigblavanderia.repository.OrdemProcessoRepository;
import br.com.ifes.apigblavanderia.repository.ProcessoRepository;
import br.com.ifes.apigblavanderia.service.util.AlgoritimoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlgoritmoService {

    public static final Integer PORCENTAGEM_CRUZAMENTO = 20;
    public static final Integer PORCENTAGEM_MUTACAO = 15;

    private final OrdemProcessoRepository ordemProcessoRepository;
    private final ProcessoRepository processoRepository;
    private final MaquinaRepository maquinaRepository;
    private final CapacidadeMaquinaRepository capacidadeMaquinaRepository;
    private final PopulacaoService populacaoService;
    private final CruzamentoService cruzamentoService;
    private final SelecaoService selecaoService;
    private final MutacaoService mutacaoService;

    public void abasteceTodasBasesDeDados(List<OrdemProcesso> ordemProcessos, List<Processo> processos, List<Maquina> maquinas) {
        maquinas = maquinaRepository.abasteceBaseDados();
        capacidadeMaquinaRepository.abasteceBaseDados(maquinas);
        processos = processoRepository.abasteceBaseDados();
        ordemProcessos = ordemProcessoRepository.abasteceBaseDados(processos);

        log.info("Bases de dados preenchidas com sucesso!");
    }

    public Long evolucao(Integer tamanhoInicialPopulacao, Integer evolucoes) {
        List<OrdemProcesso> ordemProcessos = new ArrayList<>();
        List<Processo> processos = new ArrayList<>();
        List<Maquina> maquinas = new ArrayList<>();

        abasteceTodasBasesDeDados(ordemProcessos, processos, maquinas);

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

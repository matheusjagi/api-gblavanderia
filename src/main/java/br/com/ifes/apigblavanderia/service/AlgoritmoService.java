package br.com.ifes.apigblavanderia.service;

import br.com.ifes.apigblavanderia.config.ApplicationProperties;
import br.com.ifes.apigblavanderia.domain.Cromossomo;
import br.com.ifes.apigblavanderia.domain.Maquina;
import br.com.ifes.apigblavanderia.domain.OrdemProcesso;
import br.com.ifes.apigblavanderia.domain.Processo;
import br.com.ifes.apigblavanderia.repository.CapacidadeMaquinaRepository;
import br.com.ifes.apigblavanderia.repository.MaquinaRepository;
import br.com.ifes.apigblavanderia.repository.OrdemProcessoRepository;
import br.com.ifes.apigblavanderia.repository.ProcessoRepository;
import br.com.ifes.apigblavanderia.service.util.AlgoritimoUtil;
import br.com.ifes.apigblavanderia.service.util.LogUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlgoritmoService {

    private final OrdemProcessoRepository ordemProcessoRepository;
    private final ProcessoRepository processoRepository;
    private final MaquinaRepository maquinaRepository;
    private final CapacidadeMaquinaRepository capacidadeMaquinaRepository;
    private final PopulacaoService populacaoService;
    private final CruzamentoService cruzamentoService;
    private final SelecaoService selecaoService;
    private final MutacaoService mutacaoService;
    private final SequenciamentoService sequenciamentoService;
    private final LogUtil logUtil;
    private final ApplicationProperties env;

    public Long evolucao(Integer tamanhoInicialPopulacao, Integer evolucoes) throws IOException {
        List<Processo> processos = processoRepository.abasteceBaseDados();
        List<OrdemProcesso> ordemProcessos = ordemProcessoRepository.abasteceBaseDados(processos);
        List<Maquina> maquinas = maquinaRepository.abasteceBaseDados();
        capacidadeMaquinaRepository.abasteceBaseDados(maquinas);
        log.info("Bases de dados preenchidas com sucesso!");

        List<Cromossomo> populacao = populacaoService.inicializaPopulacao(ordemProcessos, maquinas, tamanhoInicialPopulacao);

        for (int iteracao = 0; iteracao < evolucoes; iteracao++) {
            List<Cromossomo> novaPopulacao = new ArrayList<>();

            while (novaPopulacao.size() < tamanhoInicialPopulacao) {
                List<Cromossomo> pais = new ArrayList<>(selecaoService.ranking(populacao, env.getQuantidadePaisSelecionados()));
                processoSelecaoParaAdicionarNovoIndividuoNaPopulacao(novaPopulacao, pais);
            }

            sequenciamentoService.sequenciamentoPorOrdemDeProcesso(maquinas, novaPopulacao);

            logUtil.escreveLogPopulacao(populacao, LogUtil.PATH_POPULACAO_TXT, iteracao);
            logUtil.escreveLogPopulacao(novaPopulacao, LogUtil.PATH_NOVA_POPULACAO_TXT, iteracao);

            populacao = populacaoService.elitismo(populacao, novaPopulacao, tamanhoInicialPopulacao);

            logUtil.escreveLog(populacao, iteracao, evolucoes);
        }

        return populacao.get(0).getAvaliacao();
    }

    private void processoSelecaoParaAdicionarNovoIndividuoNaPopulacao(List<Cromossomo> novaPopulacao, List<Cromossomo> pais) {
        if (AlgoritimoUtil.sortearPorcentagem() <= env.getPorcentagemCruzamento()) {
            Cromossomo filho = cruzamentoService.crossoverBaseadoEmMaioria(pais);

            if (AlgoritimoUtil.sortearPorcentagem() <= env.getPorcentagemMutacao()) {
                mutacaoService.mutacao(filho);
            }

            novaPopulacao.add(filho);
            return;
        }

        pais.stream()
            .findAny()
            .ifPresent(pai -> {
                Cromossomo novoPai = new Cromossomo(pai);
                mutacaoService.mutacao(novoPai);
                novaPopulacao.add(novoPai);
            });
    }
}

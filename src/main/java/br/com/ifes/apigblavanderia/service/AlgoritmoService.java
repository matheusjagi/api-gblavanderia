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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlgoritmoService {

    public static final Integer PORCENTAGEM_CRUZAMENTO = 20;
    public static final Integer PORCENTAGEM_MUTACAO = 20;
    public static final Integer QUANTIDADE_PAIS_SELECIONADOS = 7;
    public static final String PATH_FILE_TXT = "C:\\Users\\matheus.jagi\\Documents\\TCF Pós\\logs\\8-log-POPULACAO[50]-EVOLUCOES[200].txt";

    public static final String PATH_POPULACAO_TXT = "C:\\Users\\matheus.jagi\\Documents\\TCF Pós\\logs\\populacao.txt";

    public static final String PATH_NOVA_POPULACAO_TXT = "C:\\Users\\matheus.jagi\\Documents\\TCF Pós\\logs\\nova-populacao.txt";

    private final OrdemProcessoRepository ordemProcessoRepository;
    private final ProcessoRepository processoRepository;
    private final MaquinaRepository maquinaRepository;
    private final CapacidadeMaquinaRepository capacidadeMaquinaRepository;
    private final PopulacaoService populacaoService;
    private final CruzamentoService cruzamentoService;
    private final SelecaoService selecaoService;
    private final MutacaoService mutacaoService;
    private final SequenciamentoService sequenciamentoService;

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
                List<Cromossomo> pais = new ArrayList<>(selecaoService.ranking(populacao, QUANTIDADE_PAIS_SELECIONADOS));
                processoSelecaoParaAdicionarNovoIndividuoNaPopulacao(novaPopulacao, pais);
            }

            sequenciamentoService.sequenciamentoPorOrdemDeProcesso(maquinas, novaPopulacao);

            escreveLogPopulacao(populacao, PATH_POPULACAO_TXT, iteracao);
            escreveLogPopulacao(novaPopulacao, PATH_NOVA_POPULACAO_TXT, iteracao);

            populacao.addAll(novaPopulacao);
            populacao = populacaoService.miLambda(populacao, tamanhoInicialPopulacao);

            escreveLog(populacao, iteracao);
        }

        return populacao.get(0).getAvaliacao();
    }

    private void escreveLog(List<Cromossomo> populacao, int iteracao) throws IOException {
        String content = LocalDateTime.now() + " | " +
                "Evolução [" + iteracao + "] | " +
                "Melhor avaliação [" + populacao.get(0).getAvaliacao() + "] | " +
                "Pior avaliação [" + populacao.get(populacao.size() - 1).getAvaliacao() + "]\n";

        log.info(content);

        Files.writeString(Paths.get(PATH_FILE_TXT), content, CREATE, APPEND);
    }

    private void escreveLogPopulacao(List<Cromossomo> populacao, String path, Integer evolucao) throws IOException {
        String title = "=============================[EVOLUÇÃO "+ evolucao +"]=============================\n\n";

        String avalicoes = populacao.stream()
                .sorted(Comparator.comparing(Cromossomo::getAvaliacao))
                .map(pop -> String.format("[%d] Avaliação: %d", populacao.indexOf(pop) + 1, pop.getAvaliacao()))
                .collect(Collectors.joining("\n"));

        String content = title.concat(avalicoes).concat("\n\n");

        Files.writeString(Paths.get(path), content, CREATE, APPEND);
    }

    private void processoSelecaoParaAdicionarNovoIndividuoNaPopulacao(List<Cromossomo> novaPopulacao, List<Cromossomo> pais) {
        if (AlgoritimoUtil.sortearPorcentagem() > PORCENTAGEM_CRUZAMENTO) {
            Cromossomo filho = cruzamentoService.crossoverBaseadoEmMaioria(pais);

            if (AlgoritimoUtil.sortearPorcentagem() <= PORCENTAGEM_MUTACAO) {
                mutacaoService.mutacao(filho);
            }

            //Implementar método para parar a conversão genética
            novaPopulacao.add(filho);
            return;
        }

        pais.stream()
            .findAny()
            .ifPresent(pai -> novaPopulacao.add(new Cromossomo(pai)));
    }
}

package br.com.ifes.apigblavanderia.service;

import br.com.ifes.apigblavanderia.domain.Cromossomo;
import br.com.ifes.apigblavanderia.domain.Maquina;
import br.com.ifes.apigblavanderia.domain.OrdemProcesso;
import br.com.ifes.apigblavanderia.service.util.AlgoritimoUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class PopulacaoService {

    public static final Double TAXA_ELITISMO = 0.3;

    public static final Integer OPERADOR_DIVERSIDADE = 50;

    private final SequenciamentoService sequenciamentoService;

    private Cromossomo inicializaCromossomo(List<OrdemProcesso> ordemProcessos) {
        Cromossomo cromossomo = new Cromossomo();
        ordemProcessos.forEach(op -> cromossomo.getGenes().add(op.clone()));
        AlgoritimoUtil.defineSequenciamentoAleatorioDosGenes(ordemProcessos.size(), cromossomo);
        return cromossomo;
    }

    public List<Cromossomo> inicializaPopulacao(List<OrdemProcesso> ordemProcessos, List<Maquina> maquinas, Integer tamanhoPopulacao) {
        List<Cromossomo> populacao = Stream.generate(() -> inicializaCromossomo(ordemProcessos))
                .limit(tamanhoPopulacao)
                .collect(Collectors.toList());

        sequenciamentoService.sequenciamentoPorOrdemDeProcesso(maquinas, populacao);
        AlgoritimoUtil.ordenaPorMelhorAvaliacao(populacao);
        return populacao;
    }

    public List<Cromossomo> miLambda(List<Cromossomo> populacao, List<Cromossomo> novaPopulacao, Integer tamanhoPopulacaoInicial){
        populacao.addAll(novaPopulacao);
        AlgoritimoUtil.ordenaPorMelhorAvaliacao(populacao);

        return populacao.stream()
                .limit(tamanhoPopulacaoInicial)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Cromossomo> elitismo(List<Cromossomo> populacao, List<Cromossomo> novaPopulacao,
                                     Integer tamanhoPopulacaoInicial) {
        AlgoritimoUtil.ordenaPorMelhorAvaliacao(novaPopulacao);

        Double quantidadeEscolhidos = tamanhoPopulacaoInicial * TAXA_ELITISMO;

        List<Cromossomo> escolhidos = novaPopulacao.stream()
                .limit(quantidadeEscolhidos.intValue())
                .collect(Collectors.toCollection(ArrayList::new));

        populacao.addAll(escolhidos);
        AlgoritimoUtil.ordenaPorMelhorAvaliacao(populacao);

        return populacao.stream()
                .limit(tamanhoPopulacaoInicial)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private List<Cromossomo> inserirDiversidade(List<Cromossomo> populacao, List<Cromossomo> novaPopulacao,
                                                Integer tamanhoPopulacaoInicial, Integer iteracao,
                                                List<OrdemProcesso> ordemProcessos, List<Maquina> maquinas) {
        if (iteracao != 0 && (iteracao % OPERADOR_DIVERSIDADE) == 0) {
            log.info("Operador de diversidade acionado!");

            populacao = populacao.stream()
                    .filter(AlgoritimoUtil.distinctByKey(Cromossomo::getAvaliacao))
                    .collect(Collectors.toCollection(ArrayList::new));

            List<Cromossomo> complementoPopulacao = inicializaPopulacao(ordemProcessos, maquinas,
                    tamanhoPopulacaoInicial - novaPopulacao.size());

            populacao.addAll(complementoPopulacao);
        }

        return populacao;
    }
}
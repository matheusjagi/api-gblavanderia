package br.com.ifes.apigblavanderia.service;

import br.com.ifes.apigblavanderia.domain.Cromossomo;
import br.com.ifes.apigblavanderia.domain.Maquina;
import br.com.ifes.apigblavanderia.domain.OrdemProcesso;
import br.com.ifes.apigblavanderia.service.util.AlgoritimoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PopulacaoService {

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

    public List<Cromossomo> miLambda(List<Cromossomo> populacao, Integer tamanhoPopulacaoInicial){
        AlgoritimoUtil.ordenaPorMelhorAvaliacao(populacao);

        return populacao.stream()
                .limit(tamanhoPopulacaoInicial)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
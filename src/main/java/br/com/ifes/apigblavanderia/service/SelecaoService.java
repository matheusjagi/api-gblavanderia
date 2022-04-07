package br.com.ifes.apigblavanderia.service;

import br.com.ifes.apigblavanderia.domain.Cromossomo;
import br.com.ifes.apigblavanderia.service.util.AlgoritimoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Service
@Slf4j
public class SelecaoService {

    public static final Double MIN = 0.9;
    public static final Double MAX = 1.1;

    public List<Cromossomo> ranking(List<Cromossomo> populacao, Integer quantidadeIndividuosSelecionados){
        List<Double> ranking = new ArrayList<>();
        Integer tamanhoPopulacao = populacao.size();

        AlgoritimoUtil.ordenaPorPiorAvaliacao(populacao);

        IntStream.range(0, tamanhoPopulacao).forEach(indexCalculo ->
            ranking.add(calculaRanking(MIN, MAX, Double.valueOf(tamanhoPopulacao), (indexCalculo + 1)))
        );

        List<Cromossomo> individuosSelecionados = new ArrayList<>();

        IntStream.range(0,quantidadeIndividuosSelecionados).forEach(index -> {
            Double numeroSorteado = AlgoritimoUtil.sortearDouble(MIN, MAX);

            AtomicInteger indexIndividuoEscolhido = new AtomicInteger();

            ranking.stream()
                    .filter(numero -> numero >= numeroSorteado)
                    .findFirst()
                    .ifPresent(individuo -> indexIndividuoEscolhido.set(ranking.indexOf(individuo)));

            individuosSelecionados.add(populacao.get(indexIndividuoEscolhido.get()));
        });

        return individuosSelecionados;
    }

    public Double calculaRanking(Double MIN, Double MAX, Double tamanhoPopulacao, Integer classificacao){
        return MIN + (MAX - MIN) * ( (classificacao - 1) / (tamanhoPopulacao - 1));
    }

    public List<Cromossomo> torneio(List<Cromossomo> populacao, Integer tamanhoTorneio, Integer quantidadeIndividuosSelecionados){
        List<Cromossomo> individuosSelecionados = new ArrayList<>();

        IntStream.range(0,quantidadeIndividuosSelecionados).forEach(index -> {
            List<Cromossomo> listaTorneio = selecionaPaisAleatorios(populacao, tamanhoTorneio);
            individuosSelecionados.add(Collections.max(listaTorneio, Comparator.comparing(Cromossomo::getAvaliacao)));
        });

        return individuosSelecionados;
    }

    public List<Cromossomo> selecionaPaisAleatorios(List<Cromossomo> populacao, Integer quantidadePais){
        List<Cromossomo> pais = new ArrayList<>();

        IntStream.range(0, quantidadePais).forEach(index ->
            pais.add(populacao.get(AlgoritimoUtil.sortearNumero(0, populacao.size() - 1)))
        );

        return pais;
    }
}

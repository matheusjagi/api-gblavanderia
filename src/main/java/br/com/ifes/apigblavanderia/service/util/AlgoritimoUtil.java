package br.com.ifes.apigblavanderia.service.util;

import br.com.ifes.apigblavanderia.domain.Cromossomo;
import br.com.ifes.apigblavanderia.domain.Minuto;
import br.com.ifes.apigblavanderia.domain.OrdemProcesso;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class AlgoritimoUtil {

    public static final Integer DIA_TRABALHADO_EM_MINUTOS = 480;

    private AlgoritimoUtil() {
        throw new IllegalStateException("Classe utilitária");
    }

    public static Integer sortearPorcentagem(){
        return sortearNumero(0, 100);
    }

    public static Double sortearDouble(Double MIN, Double MAX) {
        return MIN + (Double)(Math.random() * (MAX - MIN));
    }

    public static Integer sortearNumero(Integer start, Integer end) {
        Random random = new Random(ThreadLocalRandom.current().nextInt());
        return random.nextInt((end - start) + 1) + start;
    }

    public static List<Minuto> criaDiaTrabalhadoEmMinutos() {
        return Stream.generate(Minuto::new)
                .limit(DIA_TRABALHADO_EM_MINUTOS)
                .collect(Collectors.toList());
    }

    public static void ordenaCromossomoPorOrdemDeSequenciamento(Cromossomo cromossomo) {
        cromossomo.getGenes().sort(Comparator.comparing(OrdemProcesso::getSequenciamento));
    }

    public static void ordenaPorPiorAvaliacao(List<Cromossomo> cromossomos) {
        cromossomos.sort(Comparator.comparing(Cromossomo::getAvaliacao));
    }
}

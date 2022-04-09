package br.com.ifes.apigblavanderia.service.util;

import br.com.ifes.apigblavanderia.domain.Cromossomo;
import br.com.ifes.apigblavanderia.domain.Minuto;
import br.com.ifes.apigblavanderia.domain.OrdemProcesso;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
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

    public static void ordenaPorMelhorAvaliacao(List<Cromossomo> cromossomos) {
        cromossomos.sort(Comparator.comparing(Cromossomo::getAvaliacao).reversed());
    }

    public static void trocaPosicaoDeSequencimentoDosGenes(Cromossomo cromossomo, Integer tamanhoGenes) {
        Integer primeiraPosicaoSorteada = AlgoritimoUtil.sortearNumero(0, tamanhoGenes - 1);
        Integer segundaPosicaoSorteada = AlgoritimoUtil.sortearNumero(0, tamanhoGenes - 1);

        Integer sequenciamentoPrimeiraPosicao = cromossomo.getGenes().get(primeiraPosicaoSorteada).getSequenciamento();
        Integer sequenciamentoSegundaPosicao = cromossomo.getGenes().get(segundaPosicaoSorteada).getSequenciamento();

        cromossomo.getGenes().get(primeiraPosicaoSorteada).setSequenciamento(sequenciamentoSegundaPosicao);
        cromossomo.getGenes().get(segundaPosicaoSorteada).setSequenciamento(sequenciamentoPrimeiraPosicao);
    }

    public static void defineSequenciamentoAleatorioDosGenes(Integer quantidadeOrdemProcessos, Cromossomo cromossomo) {
        cromossomo.getGenes().forEach(gen -> sortearSequenciamento(quantidadeOrdemProcessos, cromossomo, gen));
        ordenaCromossomoPorOrdemDeSequenciamento(cromossomo);
    }

    public static void sortearSequenciamento(Integer quantidadeOrdemProcessos, Cromossomo cromossomo, OrdemProcesso gen) {
        AtomicInteger sequenciamento = new AtomicInteger();

        do {
            sequenciamento.set(AlgoritimoUtil.sortearNumero(1, quantidadeOrdemProcessos));
        } while (verificaSequeciamentoIgual(sequenciamento, cromossomo));

        gen.setSequenciamento(sequenciamento.get());
    }

    public static Boolean verificaSequeciamentoIgual(AtomicInteger sequenciamento, Cromossomo cromossomo) {
        return cromossomo.getGenes().stream()
                .anyMatch(obj -> Objects.equals(sequenciamento.get(), obj.getSequenciamento()));
    }
}

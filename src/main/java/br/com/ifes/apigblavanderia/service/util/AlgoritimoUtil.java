package br.com.ifes.apigblavanderia.service.util;

import br.com.ifes.apigblavanderia.domain.Minuto;

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
        return sortearNumero(100);
    }

    public static Integer sortearNumero(Integer limit){
        Random random = new Random(ThreadLocalRandom.current().nextInt());
        return random.nextInt(limit);
    }

    public static List<Minuto> criaDiaTrabalhadoEmMinutos() {
        return Stream.generate(Minuto::new)
                .limit(DIA_TRABALHADO_EM_MINUTOS)
                .collect(Collectors.toList());
    }
}

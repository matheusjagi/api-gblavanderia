package br.com.ifes.apigblavanderia.service.util;

import br.com.ifes.apigblavanderia.domain.Cromossomo;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

@Slf4j
public class LogUtil {

    public static final String PATH_FILE_TXT = "C:\\Users\\matheus.jagi\\Documents\\TCF Pós\\logs\\refazendo\\POPULACAO[100]-EVOLUCOES[100].txt";

    public static final String PATH_POPULACAO_TXT = "C:\\Users\\matheus.jagi\\Documents\\TCF Pós\\logs\\refazendo\\populacao.txt";

    public static final String PATH_NOVA_POPULACAO_TXT = "C:\\Users\\matheus.jagi\\Documents\\TCF Pós\\logs\\refazendo\\nova-populacao.txt";

    private LogUtil() {
        throw new IllegalStateException("Classe utilitária");
    }

    public static void escreveLog(List<Cromossomo> populacao, int iteracao) throws IOException {
        String content = LocalDateTime.now() + " | " +
                "Evolução [" + iteracao + "] | " +
                "Melhor avaliação [" + populacao.get(0).getAvaliacao() + "] | " +
                "Pior avaliação [" + populacao.get(populacao.size() - 1).getAvaliacao() + "]\n";

        log.info(content);

        Files.writeString(Paths.get(PATH_FILE_TXT), content, CREATE, APPEND);
    }

    public static void escreveLogPopulacao(List<Cromossomo> populacao, String path, Integer evolucao) throws IOException {
        String title = "=============================[EVOLUÇÃO "+ evolucao +"]=============================\n\n";

        String avalicoes = populacao.stream()
                .sorted(Comparator.comparing(Cromossomo::getAvaliacao))
                .map(pop -> String.format("[%d] Avaliação: %d", populacao.indexOf(pop) + 1, pop.getAvaliacao()))
                .collect(Collectors.joining("\n"));

        String content = title.concat(avalicoes).concat("\n\n");

        Files.writeString(Paths.get(path), content, CREATE, APPEND);
    }

}

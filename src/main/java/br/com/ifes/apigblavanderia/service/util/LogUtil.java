package br.com.ifes.apigblavanderia.service.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.com.ifes.apigblavanderia.config.ApplicationProperties;
import br.com.ifes.apigblavanderia.domain.Cromossomo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogUtil {

    public static final Path PATH_FILE_TXT = Paths.get("result", "evolucoes-avaliacoes.txt");
    public static final Path PATH_POPULACAO_TXT = Paths.get("result", "populacao.txt");
    public static final Path PATH_NOVA_POPULACAO_TXT = Paths.get("result", "nova-populacao.txt");

    private final ApplicationProperties env;

    public void escreveLog(List<Cromossomo> populacao, int iteracao, int quantidadeEvolucoes) throws IOException {
        String content = LocalDateTime.now().format(env.getDateTimeFormat()) + " | " +
                "Evolução [" + iteracao + "] | " +
                "Melhor avaliação [" + populacao.get(0).getAvaliacao() + "] | " +
                "Pior avaliação [" + populacao.get(populacao.size() - 1).getAvaliacao() + "]\n";

        if (iteracao == 0) {
            String title = String.format("Processo de otimização realizado em [%s] com: \n\t- POPULAÇÃO INICIAL [%d]\n\t- QUANTIDADE DE EVOLUÇÕES [%d]\n\n",
                LocalDateTime.now().format(env.getDateTimeFormat()), populacao.size(), quantidadeEvolucoes);

            content = title.concat(content);
        }

        if (iteracao == quantidadeEvolucoes - 1) {
            content = String.format("%s\nProcesso de sequenciamento finalizado em [%s]\n\t- MELHOR AVALIAÇÃO ATINGIDA: %d\n\n\f",
                content, LocalDateTime.now().format(env.getDateTimeFormat()), populacao.get(0).getAvaliacao());
        }

        log.info(content);

        Files.createDirectories(PATH_FILE_TXT.getParent());

        Files.writeString(PATH_FILE_TXT, content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public void escreveLogPopulacao(List<Cromossomo> populacao, Path path, Integer evolucao) throws IOException {
        String title = "=============================[EVOLUÇÃO "+ evolucao +"]=============================\n\n";

        String avaliacoes = populacao.stream()
                .sorted(Comparator.comparing(Cromossomo::getAvaliacao))
                .map(pop -> String.format("[%d] Avaliação: %d", populacao.indexOf(pop) + 1, pop.getAvaliacao()))
                .collect(Collectors.joining("\n"));

        String content = title.concat(avaliacoes).concat("\n\n");

        Files.createDirectories(path.getParent());

        Files.writeString(path, content, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
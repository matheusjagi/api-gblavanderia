package br.com.ifes.apigblavanderia.repository;

import br.com.ifes.apigblavanderia.domain.Processo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ProcessoRepository extends DataRepository<Processo> {

    @Override
    public List<Processo> abasteceBaseDados() {
        try {
            log.info("Preenchendo base de dados de PROCESSOS...");

            return Files.lines(Paths.get(getUrlArquivoCSV("processos.csv")), StandardCharsets.ISO_8859_1)
                    .skip(1)
                    .map(line -> line.split(";"))
                    .map(col -> new Processo(Integer.valueOf(col[0]), col[1]))
                    .collect(Collectors.toList());

        } catch (IOException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao preencher a base de dados de Processos");
        }
    }
}

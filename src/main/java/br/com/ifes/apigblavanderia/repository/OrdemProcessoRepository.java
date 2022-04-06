package br.com.ifes.apigblavanderia.repository;

import br.com.ifes.apigblavanderia.domain.OrdemProcesso;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class OrdemProcessoRepository extends DataRepository<OrdemProcesso> {

    @Override
    public List<OrdemProcesso> abasteceBaseDados() {
        try {
            log.info("Preenchendo base de dados de ORDEM DE PROCESSOS...");

            return Files.lines(Paths.get(getUrlArquivoCSV("ordem-processos.csv")), StandardCharsets.ISO_8859_1)
                    .skip(1)
                    .map(line -> line.split(";"))
                    .map(col -> new OrdemProcesso(
                            Integer.valueOf(col[1]),
                            Integer.valueOf(col[2]),
                            col[3],
                            LocalDate.parse(col[4])))
                    .collect(Collectors.toList());

        } catch (IOException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao preencher a base de dados das OPs");
        }
    }
}

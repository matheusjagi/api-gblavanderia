package br.com.ifes.apigblavanderia.repository;

import br.com.ifes.apigblavanderia.domain.Maquina;
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
public class MaquinaRepository extends DataRepository<Maquina> {

    @Override
    public List<Maquina> abasteceBaseDados() {
        try {
            log.info("Preenchendo base de dados de MAQUINAS...");

            return Files.lines(Paths.get(getUrlArquivoCSV("maquinas.csv")), StandardCharsets.ISO_8859_1)
                    .skip(1)
                    .map(line -> line.split(";"))
                    .map(col -> new Maquina(
                            Integer.valueOf(col[1]),
                            col[3],
                            Integer.valueOf(col[2])))
                    .collect(Collectors.toList());

        } catch (IOException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao preencher a base de dados de Máquinas");
        }
    }
}

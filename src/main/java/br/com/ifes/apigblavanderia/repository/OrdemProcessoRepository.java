package br.com.ifes.apigblavanderia.repository;

import br.com.ifes.apigblavanderia.domain.OrdemProcesso;
import br.com.ifes.apigblavanderia.domain.Processo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
public class OrdemProcessoRepository extends DataRepository<OrdemProcesso> {

    public List<OrdemProcesso> abasteceBaseDados(List<Processo> processos) {
        List<OrdemProcesso> ordemProcessos = new ArrayList<>();

        try {
            log.info("Preenchendo base de dados de ORDEM DE PROCESSOS...");

            Files.lines(Paths.get(getUrlArquivoCSV("ordemProcessos.csv")), StandardCharsets.ISO_8859_1)
                    .skip(1)
                    .map(line -> line.split(";"))
                    .forEach(col -> {
                        Integer ordemProcessoId = Integer.valueOf(col[0]);
                        Integer quantidadePecas = Integer.valueOf(col[1]);
                        Integer prioridade = Integer.valueOf(col[2]);
                        String referenciaCliente = col[3];
                        Integer processoId = Integer.valueOf(col[4]);

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
                        LocalDate dataEntrega = LocalDateTime.parse(col[5], formatter).toLocalDate();

                        ordemProcessos.stream()
                                .filter(op -> Objects.equals(op.getId(), ordemProcessoId))
                                .findFirst()
                                .ifPresentOrElse(
                                        (op) -> setProcesso(processos, quantidadePecas, Integer.valueOf(col[4]), op),
                                        () -> {
                                            OrdemProcesso ordemProcesso = new OrdemProcesso(ordemProcessoId, prioridade,
                                                    referenciaCliente, dataEntrega);
                                            setProcesso(processos, quantidadePecas, processoId, ordemProcesso);
                                            ordemProcessos.add(ordemProcesso);
                                        }
                                );
                    });

            return ordemProcessos;

        } catch (IOException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao preencher a base de dados das OPs");
        }
    }

    private void setProcesso(List<Processo> processos, Integer quantidadePecas, Integer processoId, OrdemProcesso ordemProcesso) {
        processos.stream()
                .filter(processo -> Objects.equals(processo.getId(), processoId))
                .findFirst()
                .ifPresent(processo -> {
                    Processo processoClone = processo.clone();
                    processoClone.setQuantidadePecas(quantidadePecas);
                    ordemProcesso.getProcessos().add(processoClone);
                });
    }

    @Override
    List<OrdemProcesso> abasteceBaseDados() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Método não implementado");
    }
}

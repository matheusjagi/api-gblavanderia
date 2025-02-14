package br.com.ifes.apigblavanderia.repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import br.com.ifes.apigblavanderia.domain.Maquina;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class CapacidadeMaquinaRepository extends DataRepository<Maquina> {

    public void abasteceBaseDados(List<Maquina> maquinas) {
        try {
            log.info("Preenchendo base de dados das CAPACIDADES DE PRODUCAO POR HORA da máquinas...");

            Files.lines(Paths.get(getUrlArquivoCSV("capacidadeHora.csv")), StandardCharsets.ISO_8859_1)
                    .skip(1)
                    .map(line -> line.split(";"))
                    .forEach(col -> {
                        Integer maquinaId = Integer.valueOf(col[0]);
                        Integer processoId = Integer.valueOf(col[1]);
                        Integer producaoHora = Integer.valueOf(col[2]);

                        maquinas.stream()
                                .filter(maquina -> Objects.equals(maquina.getId(), maquinaId))
                                .findFirst()
                                .ifPresent(maquina -> {
                                    maquina.setProducaoMaximaPorHora(producaoHora);
                                    setProcessoRealizadoPelaMaquina(processoId, maquina);
                                });
                    });

        } catch (IOException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao preencher a base de dados das Capacidades das Máquinas");
        }
    }

    private void setProcessoRealizadoPelaMaquina(Integer processoId, Maquina maquina) {
        if (maquina.getProcessosQueRealiza().stream().noneMatch(processo -> processo.equals(processoId))) {
            maquina.getProcessosQueRealiza().add(processoId);
        }
    }

    @Override
    List<Maquina> abasteceBaseDados() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Método não implementado");
    }
}

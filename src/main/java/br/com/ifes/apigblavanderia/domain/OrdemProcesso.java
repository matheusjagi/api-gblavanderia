package br.com.ifes.apigblavanderia.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrdemProcesso {

    private Integer id;

    private Integer prioridade;

    private List<Processo> processos = new ArrayList<>();

    private String nomeEmpresa;

    private LocalDate dataEntrega;

    private LocalDate dataPrevistaEntrega;

    private Long diasAtraso = 0L;

    private Integer sequenciamento;

    public OrdemProcesso(Integer id, Integer prioridade, String nomeEmpresa, LocalDate dataEntrega) {
        this.id = id;
        this.prioridade = prioridade;
        this.nomeEmpresa = nomeEmpresa;
        this.dataEntrega = dataEntrega;
    }
}

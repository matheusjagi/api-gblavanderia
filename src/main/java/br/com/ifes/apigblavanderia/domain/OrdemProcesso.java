package br.com.ifes.apigblavanderia.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrdemProcesso implements Serializable, Cloneable {

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

    public OrdemProcesso(Integer id, Integer prioridade, List<Processo> processos, String nomeEmpresa,
                         LocalDate dataEntrega, LocalDate dataPrevistaEntrega, Long diasAtraso, Integer sequenciamento) {
        this.id = id;
        this.prioridade = prioridade;
        this.processos = processos;
        this.nomeEmpresa = nomeEmpresa;
        this.dataEntrega = dataEntrega;
        this.dataPrevistaEntrega = dataPrevistaEntrega;
        this.diasAtraso = diasAtraso;
        this.sequenciamento = sequenciamento;
    }

    @Override
    public OrdemProcesso clone() {
        try {
            return (OrdemProcesso) super.clone();
        } catch (CloneNotSupportedException e) {
            return new OrdemProcesso(this.id, this.prioridade, this.processos, this.nomeEmpresa, this.dataEntrega,
                    this.dataPrevistaEntrega, this.diasAtraso, this.sequenciamento);
        }
    }
}

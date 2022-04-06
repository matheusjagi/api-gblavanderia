package br.com.ifes.apigblavanderia.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Processo {

    private Integer id;

    private String nome;

    private Integer quantidadePecas;

    private Integer ordemProcessoId;

    private Integer maquinaId;

    private Maquina maquinaAlocada;

    public Processo(Integer id) {
        this.id = id;
    }

    public Processo(Integer id, String nome, Integer quantidadePecas) {
        this.id = id;
        this.nome = nome;
        this.quantidadePecas = quantidadePecas;
    }
}

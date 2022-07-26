package br.com.ifes.apigblavanderia.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class Processo implements Serializable, Cloneable {

    private Integer id;

    private String descricao;

    private Integer quantidadePecas;

    private Maquina maquinaAlocada;

    public Processo(Integer id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public Processo(Integer id, String descricao, Integer quantidadePecas, Maquina maquinaAlocada) {
        this.id = id;
        this.descricao = descricao;
        this.quantidadePecas = quantidadePecas;
        this.maquinaAlocada = maquinaAlocada;
    }

    @Override
    public Processo clone() {
        try {
            return (Processo) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Processo(this.id, this.descricao, this.quantidadePecas, this.maquinaAlocada);
        }
    }
}

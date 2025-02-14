package br.com.ifes.apigblavanderia.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import br.com.ifes.apigblavanderia.domain.Cromossomo;
import br.com.ifes.apigblavanderia.domain.OrdemProcesso;
import br.com.ifes.apigblavanderia.service.util.AlgoritimoUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CruzamentoService {

    private final SequenciamentoService sequenciamentoService;

    public Cromossomo crossoverBaseadoEmMaioria(List<Cromossomo> pais) {
        Integer tamanhoPais = pais.size();
        Integer tamanhoGenes = pais.get(0).getGenes().size();

        Cromossomo filhoGerado = new Cromossomo();

        IntStream.range(0, tamanhoGenes).forEach(index -> {
            List<OrdemProcesso> cromossomoVerificador = new ArrayList<>();

            IntStream.range(0, tamanhoPais).forEach(iterator ->
                cromossomoVerificador.add(pais.get(iterator).getGenes().get(index).clone())
            );

            cromossomoVerificador.stream()
                    .min(Comparator.comparing(OrdemProcesso::getDiasAtraso))
                    .ifPresent(op -> filhoGerado.getGenes().add(index, op.clone()));
        });

        trocaSequenciamentosIguais(tamanhoGenes, filhoGerado);
        AlgoritimoUtil.ordenaCromossomoPorOrdemDeSequenciamento(filhoGerado);
        sequenciamentoService.setAvalicaoCromossomo(filhoGerado);
        return filhoGerado;
    }

    private void trocaSequenciamentosIguais(Integer tamanhoGenes, Cromossomo filhoGerado) {
        filhoGerado.getGenes().forEach(gen -> {
            if (AlgoritimoUtil.verificaSequeciamentoIgual(gen.getSequenciamento(), filhoGerado)) {
                resorteiaSequenciamentoConsiderandoDiasDeAtraso(tamanhoGenes, filhoGerado, gen);
            }
        });
    }

    private void resorteiaSequenciamentoConsiderandoDiasDeAtraso(Integer tamanhoGenes, Cromossomo filhoGerado, OrdemProcesso gen) {
        filhoGerado.getGenes().stream()
                .filter(op -> Objects.equals(op.getSequenciamento(), gen.getSequenciamento()))
                .sorted(Comparator.comparing(OrdemProcesso::getDiasAtraso))
                .skip(1)
                .forEach(op -> AlgoritimoUtil.sortearSequenciamento(tamanhoGenes, filhoGerado, op));
    }
}

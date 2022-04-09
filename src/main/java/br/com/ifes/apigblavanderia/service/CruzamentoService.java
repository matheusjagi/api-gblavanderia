package br.com.ifes.apigblavanderia.service;

import br.com.ifes.apigblavanderia.domain.Cromossomo;
import br.com.ifes.apigblavanderia.domain.OrdemProcesso;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class CruzamentoService {

    private SequenciamentoService sequenciamentoService;

    public Cromossomo crossoverBaseadoEmMaioria(List<Cromossomo> pais) {
        Integer tamanhoPais = pais.size();
        Integer tamanhoGenes = pais.get(0).getGenes().size();

        List<OrdemProcesso> cromossomoVerificador = new ArrayList<>(tamanhoPais);
        Cromossomo filhoGerado = new Cromossomo();

        IntStream.range(0, tamanhoGenes).forEach(index -> {
            IntStream.range(0, tamanhoPais).forEach(iterator ->
                cromossomoVerificador.add(pais.get(iterator).getGenes().get(index).clone())
            );

            cromossomoVerificador.stream()
                    .min(Comparator.comparing(OrdemProcesso::getDiasAtraso))
                    .ifPresent(op -> filhoGerado.getGenes().set(index, op));
        });

        sequenciamentoService.setAvalicaoCromossomo(filhoGerado);
        return filhoGerado;
    }
}

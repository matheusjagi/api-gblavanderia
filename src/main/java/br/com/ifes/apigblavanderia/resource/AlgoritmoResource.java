package br.com.ifes.apigblavanderia.resource;

import br.com.ifes.apigblavanderia.service.AlgoritmoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/gblavanderia")
@RequiredArgsConstructor
public class AlgoritmoResource {

    private final AlgoritmoService algoritmoService;

    @GetMapping("/{tamanhoInicialPopulacao}/{quantidadeEvolucoes}")
    public ResponseEntity<Long> otimizacaoPrioridadeOP(@PathVariable("tamanhoInicialPopulacao") Integer tamanhoInicialPopulacao,
                                                       @PathVariable("quantidadeEvolucoes") Integer quantidadeEvolucoes) throws IOException {
        return ResponseEntity.ok(algoritmoService.evolucao(tamanhoInicialPopulacao, quantidadeEvolucoes));
    }
}

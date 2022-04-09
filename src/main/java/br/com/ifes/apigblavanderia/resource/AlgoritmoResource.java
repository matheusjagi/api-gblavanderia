package br.com.ifes.apigblavanderia.resource;

import br.com.ifes.apigblavanderia.service.AlgoritmoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gblavanderia")
@RequiredArgsConstructor
public class AlgoritmoResource {

    private final AlgoritmoService algoritmoService;

    @GetMapping("/{tamanhoInicialPopulacao}/{quantidadeEvolucoes}")
    @ResponseStatus(HttpStatus.OK)
    public void otimizacaoPrioridadeOP(@PathVariable("tamanhoInicialPopulacao") Integer tamanhoInicialPopulacao,
                                       @PathVariable("quantidadeEvolucoes") Integer quantidadeEvolucoes) {
        algoritmoService.evolucao(tamanhoInicialPopulacao, quantidadeEvolucoes);
    }

    @GetMapping("/csv")
    @ResponseStatus(HttpStatus.OK)
    public void testeLeituraCSV() {
        algoritmoService.testeLeituraCSV();
    }
}

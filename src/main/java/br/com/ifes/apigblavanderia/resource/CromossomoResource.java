package br.com.ifes.apigblavanderia.resource;

import br.com.ifes.apigblavanderia.service.CromossomoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gblavanderia")
@RequiredArgsConstructor
public class CromossomoResource {

    private final CromossomoService cromossomoService;

    @GetMapping
    public ResponseEntity<Void> otimizacaoPrioridadeOP() {
//        cromossomoService.testeSequenciamento();
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

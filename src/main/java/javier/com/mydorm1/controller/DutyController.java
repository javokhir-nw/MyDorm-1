package javier.com.mydorm1.controller;

import javier.com.mydorm1.dto.*;
import javier.com.mydorm1.service.DutyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/duty")
public class DutyController {

    private final DutyService dutyService;

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('list duty')")
    public ResponseEntity<PageWrapper> create(@RequestBody Pagination<Search> pagination){
        return ResponseEntity.ok(dutyService.list(pagination));
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('get duty-by-id')")
    public ResponseEntity<DutyDto> getById(@PathVariable Long id){
        return ResponseEntity.ok(dutyService.getById(id));
    }
}

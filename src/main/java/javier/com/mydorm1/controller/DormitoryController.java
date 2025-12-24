package javier.com.mydorm1.controller;


import javier.com.mydorm1.dto.*;
import javier.com.mydorm1.service.DormitoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/dorm")
public class DormitoryController {
    private final DormitoryService dormService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('create dorm')")
    public ResponseEntity<String> create(@RequestBody DormRequestDto dto){
        return ResponseEntity.ok(dormService.createOrUpdate(dto));
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('update dorm')")
    public ResponseEntity<String> update(@RequestBody DormRequestDto dto){
        return ResponseEntity.ok(dormService.update(dto));
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('get dorm-by-id')")
    public ResponseEntity<DormResponseDto> get(@PathVariable("id") Long id){
        return ResponseEntity.ok(dormService.getById(id));
    }

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('get dorm-list')")
    public ResponseEntity<PageWrapper> getAll(@RequestBody Pagination<Search> pagination){
        return ResponseEntity.ok(dormService.getList(pagination));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('delete dorm')")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return ResponseEntity.ok(dormService.delete(id));
    }
}

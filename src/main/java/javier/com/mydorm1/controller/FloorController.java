package javier.com.mydorm1.controller;


import javier.com.mydorm1.dto.*;
import javier.com.mydorm1.service.DormitoryService;
import javier.com.mydorm1.service.FloorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/floor")
public class FloorController {
    private final FloorService floorService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('create floor')")
    public ResponseEntity<String> create(@RequestBody FloorRequestDto dto){
        return ResponseEntity.ok(floorService.createOrUpdate(dto));
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('update floor')")
    public ResponseEntity<String> update(@RequestBody FloorRequestDto dto){
        return ResponseEntity.ok(floorService.update(dto));
    }

    @PostMapping("/list/{dormId}")
    @PreAuthorize("hasAuthority('get floor-list')")
    public ResponseEntity<PageWrapper> getAll(@PathVariable("dormId") Long dormId,@RequestBody Pagination<Search> pagination){
        return ResponseEntity.ok(floorService.getList(dormId,pagination));
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('get floor-by-id')")
    public ResponseEntity<FloorResponseDto> getById(@PathVariable("id") Long id){
        return ResponseEntity.ok(floorService.getById(id));
    }
}

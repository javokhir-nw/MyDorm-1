package javier.com.mydorm1.controller;

import javier.com.mydorm1.dto.RoomTypeDto;
import javier.com.mydorm1.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/room-type")
public class RoomTypeController {
    private final RoomTypeService roomTypeService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('create room-type')")
    public ResponseEntity<String> create(@RequestBody RoomTypeDto dto){
        return ResponseEntity.ok(roomTypeService.createOrUpdate(dto));
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('update room-type')")
    public ResponseEntity<String> update(@RequestBody RoomTypeDto dto){
        return ResponseEntity.ok(roomTypeService.createOrUpdate(dto));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('delete room-type')")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return ResponseEntity.ok(roomTypeService.deleteById(id));
    }

    @GetMapping("/list")
    @PreAuthorize("hasAuthority('get list')")
    public ResponseEntity<List<RoomTypeDto>> getList(){
        return ResponseEntity.ok(roomTypeService.getList());
    }
}

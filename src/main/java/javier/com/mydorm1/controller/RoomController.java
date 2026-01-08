package javier.com.mydorm1.controller;


import javier.com.mydorm1.dto.*;
import javier.com.mydorm1.service.FloorService;
import javier.com.mydorm1.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/room")
public class RoomController {
    private final RoomService roomService;

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('create room')")
    public ResponseEntity<String> create(@RequestBody RoomRequestDto dto){
        return ResponseEntity.ok(roomService.createOrUpdate(dto));
    }

    @PostMapping("/update")
    @PreAuthorize("hasAuthority('update room')")
    public ResponseEntity<String> update(@RequestBody RoomRequestDto dto){
        return ResponseEntity.ok(roomService.update(dto));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('delete room')")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return ResponseEntity.ok(roomService.deleteById(id));
    }

    @GetMapping("/list/{floorId}")
    @PreAuthorize("hasAuthority('get room-list')")
    public ResponseEntity<List<RoomResponseDto>> getAll(@PathVariable Long floorId){
        return ResponseEntity.ok(roomService.getList(floorId));
    }
}

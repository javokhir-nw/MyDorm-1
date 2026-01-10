package javier.com.mydorm1.controller;

import javier.com.mydorm1.dto.AttendanceDto;
import javier.com.mydorm1.dto.PageWrapper;
import javier.com.mydorm1.dto.Pagination;
import javier.com.mydorm1.dto.Search;
import javier.com.mydorm1.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/list")
    @PreAuthorize("hasAuthority('list attendance')")
    public ResponseEntity<PageWrapper> create(@RequestBody Pagination<Search> pagination){
        return ResponseEntity.ok(attendanceService.list(pagination));
    }

    @GetMapping("/get/{id}")
    @PreAuthorize("hasAuthority('get attendance-by-id')")
    public ResponseEntity<AttendanceDto> getById(@PathVariable Long id){
        return ResponseEntity.ok(attendanceService.getById(id));
    }
}

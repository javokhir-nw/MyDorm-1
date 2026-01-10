package javier.com.mydorm1.service;

import javier.com.mydorm1.auth.dto.UserDto;
import javier.com.mydorm1.auth.mapper.UserMapper;
import javier.com.mydorm1.auth.repo.UserRepository;
import javier.com.mydorm1.dto.AttendanceDto;
import javier.com.mydorm1.dto.PageWrapper;
import javier.com.mydorm1.dto.Pagination;
import javier.com.mydorm1.dto.Search;
import javier.com.mydorm1.model.Attendance;
import javier.com.mydorm1.model.Dormitory;
import javier.com.mydorm1.model.Floor;
import javier.com.mydorm1.repo.AttendanceRepository;
import javier.com.mydorm1.util.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final Utils utils;
    private final UserMapper userMapper;

    public PageWrapper list(Pagination<Search> pagination) {
        Search search = pagination.getSearch();
        Page<Attendance> attendancePage = attendanceRepository.list(
                search.getValue(),
                search.getDormId(),
                search.getFloorId(),
                search.getDate(),
                PageRequest.of(pagination.getPage(),pagination.getSize())
        );

        List<AttendanceDto> list = attendancePage.stream().map(AttendanceDto::new).toList();

        return PageWrapper.builder().list(list)
                .total(attendancePage.getTotalElements())
                .totalPages(attendancePage.getTotalPages())
                .build();
    }

    public AttendanceDto getById(Long attendanceId) {
        Attendance attendance = attendanceRepository.findById(attendanceId).orElseThrow(() -> new RuntimeException("Navbatchilik id boyicha topilmadi"));
        Long floorId = attendance.getFloor().getId();
        AttendanceDto response = new AttendanceDto(attendance);
        List<Long> absentUserIds = utils.extractIdsFromString(attendance.getAbsentUserIds());
        List<UserDto> list = userRepository.findAllUsersFetchRoomByFloorId(floorId).stream().map(
                u -> {
                    UserDto dto = userMapper.toAttendanceUserDto(u);
                    if (absentUserIds.contains(u.getId())) {
                        dto.setIsAttended(Boolean.FALSE);
                    } else {
                        dto.setIsAttended(Boolean.TRUE);
                    }
                    return dto;
                }
        ).toList();
        response.setUserDtoList(list);
        return response;
    }
}

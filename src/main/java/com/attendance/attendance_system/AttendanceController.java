package com.attendance.attendance_system;

import org.springframework.web.bind.annotation.*;

import com.attendance.attendance_system.Attendance;
import com.attendance.attendance_system.Student;
import com.attendance.attendance_system.StudentRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/attendance")
@CrossOrigin(origins = "*")
public class AttendanceController {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;

    public AttendanceController(
            AttendanceRepository attendanceRepository,
            StudentRepository studentRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
    }

    @GetMapping
public List<Map<String, Object>> getAttendance(
        @RequestParam(required = false) String date) {
    System.out.println("UPDATED ATTENDANCE CONTROLLER RUNNING");
    LocalDate attendanceDate =
            (date == null || date.isBlank())
                    ? LocalDate.now()
                    : LocalDate.parse(date);

    List<Attendance> records =
            attendanceRepository.findByAttendanceDate(attendanceDate);

    List<Map<String, Object>> resultList = new java.util.ArrayList<>();

    for (Attendance record : records) {
        Map<String, Object> result = new HashMap<>();

        result.put("id", record.getId());
        result.put("studentId", record.getStudentId());
        result.put("attendanceDate", record.getAttendanceDate());
        result.put("status", record.getStatus());
        System.out.println("Attendance student ID: " + record.getStudentId());
        Student student = studentRepository
                .findById(record.getStudentId())
                .orElse(null);
        System.out.println("Student found: " + student);
    
        if (student != null) {
            result.put("studentName", student.getName());
            result.put("rollNumber", student.getRollNumber());
            result.put("department", student.getDepartment());
        } else {
            result.put("studentName", "Student not found");
            result.put("rollNumber", "-");
            result.put("department", "-");
        }

        resultList.add(result);
    }

    return resultList;
}
    @PostMapping
    public Attendance markAttendance(@RequestBody Attendance attendance) {

        if (attendance.getStudentId() == null
                || attendance.getAttendanceDate() == null
                || attendance.getStatus() == null
                || !(attendance.getStatus().equals("Present")
                || attendance.getStatus().equals("Absent"))) {
            throw new IllegalArgumentException("Invalid attendance details");
        }

        List<Attendance> records =
                attendanceRepository.findAllByStudentIdAndAttendanceDate(
                        attendance.getStudentId(),
                        attendance.getAttendanceDate()
                );

        Attendance record;

        if (records.isEmpty()) {
            record = attendance;
        } else {
            record = records.get(0);
            record.setStatus(attendance.getStatus());

            if (records.size() > 1) {
                attendanceRepository.deleteAll(records.subList(1, records.size()));
            }
        }

        return attendanceRepository.save(record);
    }
}

package com.attendance.attendance_system;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;

    public StudentController(
            StudentRepository studentRepository,
            AttendanceRepository attendanceRepository) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @PostMapping
    public Student addStudent(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {

        List<Attendance> records =
                attendanceRepository.findAll();

        for (Attendance record : records) {
            if (record.getStudentId().equals(id)) {
                attendanceRepository.delete(record);
            }
        }

        studentRepository.deleteById(id);
    }
}

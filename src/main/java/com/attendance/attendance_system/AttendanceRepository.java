
package com.attendance.attendance_system;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByAttendanceDate(LocalDate attendanceDate);

    List<Attendance> findAllByStudentIdAndAttendanceDate(
            Long studentId,
            LocalDate attendanceDate
    );
}

package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public List<Student> getALlStudents() {
        return studentRepository.findAll();
    }

    public void addStudent(Student student) {
        String email = student.getEmail();
        if (studentRepository.selectExistsEmail(email)) {
            throw new BadRequestException("Email " + email + " already in use");
        }
        studentRepository.save(student);
    }

    public void deleteStudent(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new StudentNotFoundException(
                    "Student with id " + studentId + " does not exist");
        }
        studentRepository.deleteById(studentId);
    }
}

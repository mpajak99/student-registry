package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    private StudentService underTest;

    @BeforeEach
    void setUp() {
        underTest = new StudentService(studentRepository);
    }

    @Test
    void canGetALlStudents() {
        //when
        underTest.getALlStudents();

        //then
        verify(studentRepository).findAll();
    }

    @Test
    void canAddStudent() {
        //given
        String email = "jamila@gmail.com";
        Student student = new Student(
                "Jamila",
                email,
                Gender.FEMALE
        );
        //when
        underTest.addStudent(student);

        //then
        ArgumentCaptor<Student> studentArgumentCaptor =
                ArgumentCaptor.forClass(Student.class);

        verify(studentRepository)
                .save(studentArgumentCaptor.capture());

        Student captured = studentArgumentCaptor.getValue();

        assertThat(captured).isEqualTo(student);
    }

    @Test
    void willThrowBadRequestExceptionWhenAddedStudentEmailIsTaken() {
        //given
        String email = "jamila@gmail.com";
        Student student = new Student(
                "Jamila",
                email,
                Gender.FEMALE
        );

        given(studentRepository.selectExistsEmail(email))
                .willReturn(true);
        //when
        //then
        assertThatThrownBy(() -> underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + email + " already in use");

        verify(studentRepository, never()).save(any());
    }

    @Test
    void canDeleteStudent() {
        //given
        Long studentId = 1L;

        given(studentRepository.existsById(studentId))
                .willReturn(true);

        //when
        underTest.deleteStudent(studentId);

        //then
        verify(studentRepository).deleteById(studentId);

    }

    @Test
    void willThrowStudentNotFoundExceptionWhenDeletedStudentDoesNotExist() {
        //given
        Long studentId = 1L;

        given(studentRepository.existsById(studentId))
                .willReturn(false);

        //when
        //then
        assertThatThrownBy(() -> underTest.deleteStudent(studentId))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + studentId + " does not exist");

        verify(studentRepository, never()).deleteById(any());

    }
}
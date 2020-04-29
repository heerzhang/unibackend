package org.fjsei.yewu.repository;

import org.springframework.security.access.prepost.PreAuthorize;

import javax.persistence.*;

/**
 * @program: spring-boot-example
 * @description:
 * @author:
 * @create: 2018-05-04 10:38
 **/

@Entity
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "commonSeq")
    @SequenceGenerator(name = "commonSeq", initialValue = 1, allocationSize = 1, sequenceName = "SEQUENCE_COMMON")
    protected Long id;

    private String name;
    private String age;
    private String course;

    public Teacher() {
    }

    public Teacher(String name, String age, String course) {
        this.name = name;
        this.age = age;
        this.course = course;
    }

    @Override
    public String toString() {
        return "Teacher{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", age='" + age + '\'' +
            ", course='" + course + '\'' +
            '}';
    }

    public Long getId() {
        return  id;
    }

    public void setId(Long id) {
        this.id =  id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
    @PreAuthorize("hasRole('ADMIN')")
    public String getCourse() {
        return course;
    }
    @PreAuthorize("hasRole('ADMIN')")
    public void setCourse(String course) {
        this.course = course;
    }
}

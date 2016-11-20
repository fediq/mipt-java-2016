package ru.mipt.java2016.homework.tests.task2;

import java.util.Date;

/**
 * Пример сложного объекта для хранения.
 *
 * @author Fedor S. Lavrentyev
 * @since 13.10.16
 */
public class Student extends StudentKey {
    private final String hometown;
    private final Date birthDate;
    private final boolean hasDormitory;
    private final double averageScore;

    public Student(int groupId, String name, String hometown,
                   Date birthDate, boolean hasDormitory, double averageScore) {
        super(groupId, name);
        this.hometown = hometown;
        this.birthDate = birthDate;
        this.hasDormitory = hasDormitory;
        this.averageScore = averageScore;
    }

    public String getHometown() {
        return hometown;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public boolean isHasDormitory() {
        return hasDormitory;
    }

    public double getAverageScore() {
        return averageScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Student)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }

        Student student = (Student) o;

        if (isHasDormitory() != student.isHasDormitory()) {
            return false;
        }
        if (Double.compare(student.getAverageScore(), getAverageScore()) != 0) {
            return false;
        }
        if (getHometown() != null ? !getHometown().equals(student.getHometown()) : student.getHometown() != null) {
            return false;
        }
        return getBirthDate() != null ? getBirthDate().equals(student.getBirthDate()) : student.getBirthDate() == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        result = 31 * result + (getHometown() != null ? getHometown().hashCode() : 0);
        result = 31 * result + (getBirthDate() != null ? getBirthDate().hashCode() : 0);
        result = 31 * result + (isHasDormitory() ? 1 : 0);
        temp = Double.doubleToLongBits(getAverageScore());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }


}
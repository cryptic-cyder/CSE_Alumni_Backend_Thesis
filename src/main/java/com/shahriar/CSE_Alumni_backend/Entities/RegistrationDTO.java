package com.shahriar.CSE_Alumni_backend.Entities;

public class RegistrationDTO {

    private String studentId;
    private String fullName;
    private String departmentId;
    private String _token;








    public RegistrationDTO() {

    }

    public RegistrationDTO(String studentId, String fullName, String departmentId, String _token) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.departmentId = departmentId;
        this._token = _token;
    }

    // Getters and setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String get_token() {
        return _token;
    }

    public void set_token(String _token) {
        this._token = _token;
    }

}

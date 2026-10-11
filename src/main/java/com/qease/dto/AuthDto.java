package com.qease.dto;

import com.qease.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDto {

    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        public LoginRequest() {}
        public LoginRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        @NotBlank(message = "Full name is required")
        private String fullName;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;

        private String phone;
        private Role role;

        public RegisterRequest() {}
        public RegisterRequest(String fullName, String email, String password, String phone, Role role) {
            this.fullName = fullName;
            this.email = email;
            this.password = password;
            this.phone = phone;
            this.role = role;
        }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }
    }

    public static class JwtResponse {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String fullName;
        private String email;
        private Role role;

        public JwtResponse() {}
        public JwtResponse(String token, String type, Long id, String fullName, String email, Role role) {
            this.token = token;
            this.type = type;
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.role = role;
        }

        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private String token;
            private String type = "Bearer";
            private Long id;
            private String fullName;
            private String email;
            private Role role;

            public Builder token(String token) { this.token = token; return this; }
            public Builder type(String type) { this.type = type; return this; }
            public Builder id(Long id) { this.id = id; return this; }
            public Builder fullName(String fullName) { this.fullName = fullName; return this; }
            public Builder email(String email) { this.email = email; return this; }
            public Builder role(Role role) { this.role = role; return this; }
            public JwtResponse build() { return new JwtResponse(token, type, id, fullName, email, role); }
        }
    }

    public static class UserDto {
        private Long id;
        private String fullName;
        private String email;
        private String phone;
        private Role role;
        private boolean active;

        public UserDto() {}
        public UserDto(Long id, String fullName, String email, String phone, Role role, boolean active) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.role = role;
            this.active = active;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public Role getRole() { return role; }
        public void setRole(Role role) { this.role = role; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }

        public static Builder builder() { return new Builder(); }
        public static class Builder {
            private Long id;
            private String fullName;
            private String email;
            private String phone;
            private Role role;
            private boolean active;

            public Builder id(Long id) { this.id = id; return this; }
            public Builder fullName(String fullName) { this.fullName = fullName; return this; }
            public Builder email(String email) { this.email = email; return this; }
            public Builder phone(String phone) { this.phone = phone; return this; }
            public Builder role(Role role) { this.role = role; return this; }
            public Builder active(boolean active) { this.active = active; return this; }
            public UserDto build() { return new UserDto(id, fullName, email, phone, role, active); }
        }
    }
}

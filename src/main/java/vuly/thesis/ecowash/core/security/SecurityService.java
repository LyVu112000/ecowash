package vuly.thesis.ecowash.core.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    public String getFullName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsDto principal;
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        } else {
            principal = (UserDetailsDto) authentication.getPrincipal();
            return principal.getUser().getFullName();
        }
    }

    public Long getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsDto principal;
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        } else {
            principal = (UserDetailsDto) authentication.getPrincipal();
            return principal.getUserId();
        }
    }

    public String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsDto principal;
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        } else {
            principal = (UserDetailsDto) authentication.getPrincipal();
            return principal.getEmail();
        }
    }

    public Long getStaffId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsDto principal;
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        } else {
            principal = (UserDetailsDto) authentication.getPrincipal();
            return principal.getStaffId();
        }
    }

    public String getRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsDto principal;
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        } else {
            principal = (UserDetailsDto) authentication.getPrincipal();
            return principal.getRole();
        }
    }
}

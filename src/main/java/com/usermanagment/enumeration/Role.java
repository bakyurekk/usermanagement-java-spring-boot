package com.usermanagment.enumeration;

import com.usermanagment.constant.Authority;

import static com.usermanagment.constant.Authority.*;

public enum Role {
    ROLE_USER(USER_AUTHORITIES),
    ROLE_HR(HR_AUTHORITIES),
    ROLE_MANAGER(MANAGER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES),
    ROLE_SUPER_USER(SUPER_USER_AUTHORITIES);

    private  String[] authorities;
    Role(String... authorities){
        this.authorities = authorities;
    }

    public String[] getAuthorities(){
        return authorities;
    }
}

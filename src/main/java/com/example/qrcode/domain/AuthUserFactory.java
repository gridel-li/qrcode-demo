package com.example.qrcode.domain;

/**
 * The type Auth user factory.
 *
 * @author GuoqingLee
 */
public final class AuthUserFactory {

    private AuthUserFactory() {
    }

    /**
     * Create auth user.
     *
     * @param user the user
     * @return the auth user
     */
    public static AuthUser create(SysUser user) {
        AuthUser authUser = new AuthUser();
        authUser.setId(user.getId());
        authUser.setLoginName(user.getLoginName());
        authUser.setName(user.getName());
        authUser.setEmail(user.getEmail());
        authUser.setPhone(user.getPhone());
        authUser.setMobile(user.getMobile());
        authUser.setPassword(user.getPassword());
        authUser.setEnabled(user.getEnabled());
        authUser.setAuthorities(null);
        return authUser;
    }

}

package com.atlas.auth.mapper;

import com.atlas.auth.dto.RegisterResponse;
import com.atlas.auth.entity.User;

public class UserMapper {
    public static RegisterResponse toRegisterResponse(User user){
        return new RegisterResponse(user.getId(),user.getUsername(),user.getEmail());
    }
}

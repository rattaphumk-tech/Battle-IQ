package com.battleiq.service;

import com.battleiq.dto.LoginRequestDTO;
import com.battleiq.dto.UserRequestDTO;
import com.battleiq.dto.UserResponseDTO;

public interface UserService {

    UserResponseDTO register(UserRequestDTO request);

    UserResponseDTO login(LoginRequestDTO request);

    UserResponseDTO getUserById(Long id);
}

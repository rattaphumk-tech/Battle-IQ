package com.battleiq.service;

import com.battleiq.dto.request.LoginRequestDTO;
import com.battleiq.dto.request.UserRequestDTO;
import com.battleiq.dto.response.UserResponseDTO;

public interface UserService {

    UserResponseDTO register(UserRequestDTO request);

    UserResponseDTO login(LoginRequestDTO request);

    UserResponseDTO getUserById(Long id);
}

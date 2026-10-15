package ir.nas.command;

import ir.nas.dao.UserInfoDAO;
import ir.nas.dto.UserInfoResponseDTO;

public class UserInfoDaoToUserInfoResponseDto
{
    public static UserInfoResponseDTO convert(final UserInfoDAO userInfoDAO)
    {
        return UserInfoResponseDTO.builder()
                .id(userInfoDAO.getId())
                .username(userInfoDAO.getUsername())
                .name(userInfoDAO.getName())
                .age(userInfoDAO.getAge())
                .build();
    }
}

package ir.nas.command;

import ir.nas.dao.UserInfoDAO;
import ir.nas.dto.UserInfoRequestDTO;

public class UserInfoRequestDtoTOUserInfoDao
{
    public static UserInfoDAO convert(UserInfoRequestDTO userInfoRequestDTO)
    {
        return UserInfoDAO.builder()
                .username(userInfoRequestDTO.getUsername())
                .password(userInfoRequestDTO.getPassword())
                .name(userInfoRequestDTO.getName())
                .age(userInfoRequestDTO.getAge())
                .build();
    }
}

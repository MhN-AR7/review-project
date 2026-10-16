package ir.nas.service;

import ir.nas.command.UserInfoDaoToUserInfoResponseDto;
import ir.nas.command.UserInfoRequestDtoTOUserInfoDao;
import ir.nas.dao.UserInfoDAO;
import ir.nas.dto.UserInfoRequestDTO;
import ir.nas.dto.UserInfoResponseDTO;
import ir.nas.repository.UserInfoRepository;
import ir.nas.util.HibernateUtil;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserInfoService
{
    private final UserInfoRepository repository;

    public UserInfoResponseDTO create(UserInfoRequestDTO userInfoRequestDTO)
    {
        UserInfoDAO userInfoDAO = this.repository.create(
                UserInfoRequestDtoTOUserInfoDao.convert(userInfoRequestDTO));

        return UserInfoDaoToUserInfoResponseDto.convert(userInfoDAO);
    }

    public Optional<UserInfoResponseDTO> findById(Long id) {
        Optional<UserInfoDAO> userInfoDAOOptional = repository.findById(id);
        UserInfoDAO userInfoDAO;
        if (userInfoDAOOptional.isPresent()) {
            userInfoDAO = userInfoDAOOptional.get();
            return Optional.of(UserInfoDaoToUserInfoResponseDto.convert(userInfoDAO));
        }
        else
            return Optional.empty();
    }

    public List<UserInfoResponseDTO> findAll() {
        return this.repository.findAll()
                .stream()
                .map(UserInfoDaoToUserInfoResponseDto::convert)
                .toList();
    }

    public UserInfoResponseDTO update(Long id, UserInfoRequestDTO userInfoRequestDTO) {
        UserInfoDAO userInfoDAO = this.repository.update(
                id,
                UserInfoRequestDtoTOUserInfoDao.convert(userInfoRequestDTO)
        );

        return UserInfoDaoToUserInfoResponseDto.convert(userInfoDAO);
    }
}

package ir.nas.repository;

import java.util.List;
import java.util.Optional;

import ir.nas.dao.UserInfoDAO;
import ir.nas.util.HibernateUtil;

public class UserInfoRepository
{
    public UserInfoDAO create(UserInfoDAO userInfoDAO) {
        return HibernateUtil.inTxReturn(em -> {
            em.persist(userInfoDAO);
            return userInfoDAO;
        });
    }

    public Optional<UserInfoDAO> findById(Long id) {
        return Optional.ofNullable(
                HibernateUtil.read(
                        em -> em.find(UserInfoDAO.class, id)
                )
        );
    }

    public List<UserInfoDAO> findAll() {
        return HibernateUtil.read(em ->
            em.createQuery("FROM UserInfoDAO", UserInfoDAO.class).getResultList()
        );
    }

    public UserInfoDAO update(Long id, UserInfoDAO userInfoDAO) {
                return HibernateUtil.inTxReturn(
                        em -> {
                            UserInfoDAO foundedUser = em.find(UserInfoDAO.class, id);
                            if (Optional.ofNullable(foundedUser).isEmpty()) return null;

                            if (Optional.ofNullable(userInfoDAO.getUsername()).isPresent())
                                foundedUser.setUsername(userInfoDAO.getUsername());

                            if (Optional.ofNullable(userInfoDAO.getPassword()).isPresent())
                                foundedUser.setPassword(userInfoDAO.getPassword());

                            if (Optional.ofNullable(userInfoDAO.getName()).isPresent())
                                foundedUser.setName(userInfoDAO.getName());

                            if(Optional.ofNullable(userInfoDAO.getAge()).isPresent())
                                foundedUser.setAge(userInfoDAO.getAge());

                            return foundedUser;
                        }
                );
    }
}

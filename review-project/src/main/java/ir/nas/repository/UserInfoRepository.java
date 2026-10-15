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
}

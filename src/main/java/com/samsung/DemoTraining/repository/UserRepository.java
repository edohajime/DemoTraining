package com.samsung.DemoTraining.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.samsung.DemoTraining.repository.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	User findByUsername(String username);

	@Query(nativeQuery = true, value = "select * from users where username like %?1%")
	Page<User> findAllByUsernameLike(String searchName, Pageable pageable);

}

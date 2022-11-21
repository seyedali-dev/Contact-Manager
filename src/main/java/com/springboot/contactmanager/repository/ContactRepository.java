package com.springboot.contactmanager.repository;

import com.springboot.contactmanager.entities.Contact;
import com.springboot.contactmanager.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    @Query("from Contact as c where c.user.id =:userId")
    Page<Contact> getContactByUserId(int userId, Pageable pageable);

    List<Contact> findByNameContainingAndUser(String keyword, User user);
}

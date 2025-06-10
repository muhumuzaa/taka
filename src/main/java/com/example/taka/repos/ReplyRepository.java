package com.example.taka.repos;

import com.example.taka.models.Reply;
import com.example.taka.models.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findByReplier(UserProfile replier);
}

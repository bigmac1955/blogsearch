package com.example.blogsearchserver.data.repository;

import com.example.blogsearchserver.data.entity.KeywordView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
public interface KeywordViewRepository extends JpaRepository<KeywordView, String> {

    @Transactional(readOnly = true)
    KeywordView findByKeyword(String keyword);

    @Transactional(readOnly = true)
    Optional<List<KeywordView>> findTop10ByOrderByViewsDesc();
}

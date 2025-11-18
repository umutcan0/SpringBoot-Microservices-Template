package com.example.demo.repository;

import com.example.demo.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findByCompletedTrue();
    List<Note> findByTitleContainingIgnoreCase(String title);
    List<Note> findAllByOrderByCreatedAtDesc();
    @Query("SELECT n FROM Note n " +
            "WHERE (:keyword IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:completed IS NULL OR n.completed = :completed)")
    List<Note> filterNotes(@Param("keyword") String keyword,
                           @Param("completed") Boolean completed,
                           Sort sort);

    @Query("""
       SELECT n FROM Note n
       WHERE (:keyword IS NULL OR LOWER(n.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
              OR LOWER(n.content) LIKE LOWER(CONCAT('%', :keyword, '%')))
       AND (:completed IS NULL OR n.completed = :completed)
       """)
    Page<Note> searchNotes(@Param("keyword") String keyword,
                           @Param("completed") Boolean completed,
                           Pageable pageable);
    boolean existsByTitle(String title);
    List<Note> findAllByDeletedAtIsNull();
    Optional<Note> findByIdAndDeletedAtIsNotNull(Long id);


}

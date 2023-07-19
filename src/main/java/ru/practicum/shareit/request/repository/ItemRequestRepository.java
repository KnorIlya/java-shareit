package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByUserIdOrderByCreatedDesc(Long id);

    @Query("select ir from ItemRequest ir " +
            "where ir.user.id <> ?1 " +
            "order by ir.created desc")
    Page<ItemRequest> findAllAnotherRequest(Long id, Pageable pageable);
}

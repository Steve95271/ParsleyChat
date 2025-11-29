package com.parsley.parsley_chat.repository;

import com.parsley.parsley_chat.model.jpaEntity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Find all messages in a specific room, ordered by timestamp ascending
     * @param roomId the room ID to query
     * @return list of messages in chronological order
     */
    List<Message> findByRoomIdOrderByTimestampAsc(Long roomId);

}

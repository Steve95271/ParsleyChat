package com.parsley.parsley_chat.repository;

import com.parsley.parsley_chat.model.jpaEntity.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test for MessageRepository
 */
@DataJpaTest(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Long testRoomId;
    private Long otherRoomId;

    @BeforeEach
    void setUp() {
        testRoomId = 1L;
        otherRoomId = 2L;
    }

    @Test
    void testSaveMessage() {
        // Given: A new message entity
        Message message = new Message(
                null,
                testRoomId,
                100L,
                "TestUser",
                "Hello, World!",
                LocalDateTime.now()
        );

        // When: Save the message
        Message savedMessage = messageRepository.save(message);

        // Then: Message should be persisted with generated ID
        assertThat(savedMessage.getId()).isNotNull();
        assertThat(savedMessage.getRoomId()).isEqualTo(testRoomId);
        assertThat(savedMessage.getSenderName()).isEqualTo("TestUser");
        assertThat(savedMessage.getContent()).isEqualTo("Hello, World!");
    }

    @Test
    void testFindByRoomIdOrderByTimestampAsc() {
        // Given: Multiple messages in different rooms with different timestamps
        Message message1 = createMessage(testRoomId, "User1", "First message", LocalDateTime.now().minusMinutes(3));
        Message message2 = createMessage(testRoomId, "User2", "Second message", LocalDateTime.now().minusMinutes(2));
        Message message3 = createMessage(testRoomId, "User3", "Third message", LocalDateTime.now().minusMinutes(1));
        Message message4 = createMessage(otherRoomId, "User4", "Different room", LocalDateTime.now());

        entityManager.persist(message1);
        entityManager.persist(message2);
        entityManager.persist(message3);
        entityManager.persist(message4);
        entityManager.flush();

        // When: Query messages by room ID
        List<Message> messages = messageRepository.findByRoomIdOrderByTimestampAsc(testRoomId);

        // Then: Should return messages from the correct room, ordered by timestamp
        assertThat(messages).hasSize(3);
        assertThat(messages.get(0).getContent()).isEqualTo("First message");
        assertThat(messages.get(1).getContent()).isEqualTo("Second message");
        assertThat(messages.get(2).getContent()).isEqualTo("Third message");

        // Verify all messages belong to the test room
        assertThat(messages).allMatch(msg -> msg.getRoomId().equals(testRoomId));
    }

    @Test
    void testFindByRoomIdOrderByTimestampAsc_EmptyRoom() {
        // Given: No messages in the room
        Long emptyRoomId = 999L;

        // When: Query messages by room ID
        List<Message> messages = messageRepository.findByRoomIdOrderByTimestampAsc(emptyRoomId);

        // Then: Should return empty list
        assertThat(messages).isEmpty();
    }

    @Test
    void testFindById() {
        // Given: A persisted message
        Message message = createMessage(testRoomId, "User1", "Test message", LocalDateTime.now());
        Message savedMessage = entityManager.persist(message);
        entityManager.flush();

        // When: Find by ID
        Message foundMessage = messageRepository.findById(savedMessage.getId()).orElse(null);

        // Then: Should find the message
        assertThat(foundMessage).isNotNull();
        assertThat(foundMessage.getId()).isEqualTo(savedMessage.getId());
        assertThat(foundMessage.getContent()).isEqualTo("Test message");
    }

    @Test
    void testDeleteMessage() {
        // Given: A persisted message
        Message message = createMessage(testRoomId, "User1", "To be deleted", LocalDateTime.now());
        Message savedMessage = entityManager.persist(message);
        entityManager.flush();
        Long messageId = savedMessage.getId();

        // When: Delete the message
        messageRepository.deleteById(messageId);
        entityManager.flush();

        // Then: Message should not be found
        assertThat(messageRepository.findById(messageId)).isEmpty();
    }

    @Test
    void testCountMessages() {
        // Given: 3 messages in the repository
        entityManager.persist(createMessage(testRoomId, "User1", "Message 1", LocalDateTime.now()));
        entityManager.persist(createMessage(testRoomId, "User2", "Message 2", LocalDateTime.now()));
        entityManager.persist(createMessage(otherRoomId, "User3", "Message 3", LocalDateTime.now()));
        entityManager.flush();

        // When: Count all messages
        long count = messageRepository.count();

        // Then: Should have 3 messages
        assertThat(count).isEqualTo(3);
    }

    // Helper method to create message entities
    private Message createMessage(Long roomId, String senderName, String content, LocalDateTime timestamp) {
        return new Message(null, roomId, 1L, senderName, content, timestamp);
    }
}

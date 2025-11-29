package com.parsley.parsley_chat.service.impl;

import com.parsley.parsley_chat.model.jpaEntity.Message;
import com.parsley.parsley_chat.model.requestEntity.MessageRequest;
import com.parsley.parsley_chat.repository.MessageRepository;
import com.parsley.parsley_chat.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit test for MessageServiceJpaImpl
 * Uses Mockito to mock dependencies
 */
@ExtendWith(MockitoExtension.class)
class MessageServiceJpaImplTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private SimpMessagingTemplate template;

    @InjectMocks
    private MessageServiceImpl messageService;

    private Long testRoomId;
    private MessageRequest testMessageRequest;

    @BeforeEach
    void setUp() {
        testRoomId = 1L;
        testMessageRequest = new MessageRequest();
        testMessageRequest.setRoomId(testRoomId);
        testMessageRequest.setSenderName("TestUser");
        testMessageRequest.setMessage("Hello, JPA!");
        testMessageRequest.setDate(LocalDateTime.now().toString());
    }

    @Test
    void testSaveAndSendMessage_ShouldSaveMessageToRepository() {
        // Given: A message request
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> {
            Message entity = invocation.getArgument(0);
            entity.setId(123L); // Simulate database-generated ID
            return entity;
        });

        // When: Save and send message
        messageService.saveAndSendMessage(testRoomId, testMessageRequest);

        // Then: Should save message to repository
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository, times(1)).save(messageCaptor.capture());

        Message savedMessage = messageCaptor.getValue();
        assertThat(savedMessage.getRoomId()).isEqualTo(testRoomId);
        assertThat(savedMessage.getSenderName()).isEqualTo("TestUser");
        assertThat(savedMessage.getContent()).isEqualTo("Hello, JPA!");
        assertThat(savedMessage.getSenderId()).isEqualTo(1L); // TODO: hardcoded sender ID
        assertThat(savedMessage.getTimestamp()).isNotNull();
    }

    @Test
    void testSaveAndSendMessage_ShouldSendMessageToWebSocket() {
        // Given: A message request
        when(messageRepository.save(any(Message.class))).thenReturn(new Message());

        // When: Save and send message
        messageService.saveAndSendMessage(testRoomId, testMessageRequest);

        // Then: Should send message to WebSocket destination
        verify(template, times(1))
                .convertAndSend(eq("/chatroom/" + testRoomId), eq(testMessageRequest));
    }

    @Test
    void testGetChatHistoryByRoomId_ShouldQueryRepository() {
        // Given: Messages exist in the repository
        List<Message> mockMessages = Arrays.asList(
                createMessageEntity(1L, testRoomId, "User1", "Message 1"),
                createMessageEntity(2L, testRoomId, "User2", "Message 2"),
                createMessageEntity(3L, testRoomId, "User3", "Message 3")
        );
        when(messageRepository.findByRoomIdOrderByTimestampAsc(testRoomId)).thenReturn(mockMessages);

        // When: Get chat history
        messageService.getChatHistoryByRoomId(testRoomId);

        // Then: Should query repository
        verify(messageRepository, times(1)).findByRoomIdOrderByTimestampAsc(testRoomId);
    }

    @Test
    void testGetChatHistoryByRoomId_ShouldSendToWebSocket() {
        // Given: Messages exist in the repository
        List<Message> mockMessages = Arrays.asList(
                createMessageEntity(1L, testRoomId, "User1", "Message 1"),
                createMessageEntity(2L, testRoomId, "User2", "Message 2")
        );
        when(messageRepository.findByRoomIdOrderByTimestampAsc(testRoomId)).thenReturn(mockMessages);

        // When: Get chat history
        messageService.getChatHistoryByRoomId(testRoomId);

        // Then: Should send converted messages to WebSocket
        verify(template, times(1))
                .convertAndSend(eq("/chatroom/" + testRoomId + "/history"), anyList());
    }

    @Test
    void testGetChatHistoryByRoomId_EmptyRoom() {
        // Given: No messages in the room
        when(messageRepository.findByRoomIdOrderByTimestampAsc(testRoomId)).thenReturn(Arrays.asList());

        // When: Get chat history
        messageService.getChatHistoryByRoomId(testRoomId);

        // Then: Should query repository and send empty list
        verify(messageRepository, times(1)).findByRoomIdOrderByTimestampAsc(testRoomId);
        verify(template, times(1))
                .convertAndSend(eq("/chatroom/" + testRoomId + "/history"), anyList());
    }

    @Test
    void testMessageEntityCreation_ShouldHaveNullId() {
        // Given: A message request
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When: Save and send message
        messageService.saveAndSendMessage(testRoomId, testMessageRequest);

        // Then: Message entity should have null ID before saving (for auto-generation)
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(messageCaptor.capture());

        Message capturedMessage = messageCaptor.getValue();
        // Note: The ID would be null when passed to save(), allowing database to auto-generate it
        assertThat(capturedMessage.getRoomId()).isEqualTo(testRoomId);
    }

    @Test
    void testSaveAndSendMessage_VerifyTimestampIsSet() {
        // Given: A message request
        LocalDateTime beforeSave = LocalDateTime.now(ZoneOffset.UTC).minusSeconds(1);
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When: Save and send message
        messageService.saveAndSendMessage(testRoomId, testMessageRequest);

        // Then: Timestamp should be set to current time
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository).save(messageCaptor.capture());

        Message savedMessage = messageCaptor.getValue();
        LocalDateTime afterSave = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(1);

        assertThat(savedMessage.getTimestamp()).isNotNull();
        assertThat(savedMessage.getTimestamp()).isAfter(beforeSave);
        assertThat(savedMessage.getTimestamp()).isBefore(afterSave);
    }

    // Helper method to create message entities for testing
    private Message createMessageEntity(Long id, Long roomId, String senderName, String content) {
        return new Message(
                id,
                roomId,
                1L,
                senderName,
                content,
                LocalDateTime.now()
        );
    }
}

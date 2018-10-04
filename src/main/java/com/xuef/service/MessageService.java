package com.xuef.service;

import com.xuef.dao.MessageDao;
import com.xuef.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by moveb on 2018/10/4.
 */
@Service
public class MessageService {
    @Autowired
    MessageDao messageDao;

    @Autowired
    SensitiveFilterService sensitiveFilterService;

    public int addMessage(Message message) {
        message.setContent(sensitiveFilterService.filterSensitive(message.getContent()));
        return messageDao.addMessage(message);
    }

    public List<Message> getConversation(String conversationId, int offset, int limit) {
        return messageDao.getConversation(conversationId, offset, limit);
    }

    public List<Message> getConversationList(int userId, int offset, int limit) {
        return messageDao.getConversationList(userId, offset, limit);
    }

    public int convesationUnreadCount(int userId, String conversationId) {
        return messageDao.convesationUnreadCount(userId, conversationId);
    }
}

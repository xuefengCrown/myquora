package com.xuef.controller;

import com.xuef.base.ApiResponse;
import com.xuef.model.Message;
import com.xuef.model.User;
import com.xuef.model.UserHolder;
import com.xuef.service.MessageService;
import com.xuef.service.UserService;
import com.xuef.vo.ViewObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by moveb on 2018/10/4.
 */
@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    UserHolder userHolder;

    @RequestMapping(path="/msg", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse addMessage(@RequestParam("toName") String toName,
                                  @RequestParam("content") String content){
        try {
            if (userHolder.getUser() == null) {
                return ApiResponse.ofNotLogin();
            }
            User receiver = userService.getByName(toName);
            if (receiver == null) {
                return ApiResponse.ofMessage(0, "receiver not exist");
            }

            Message msg = new Message();
            msg.setContent(content);
            msg.setFromId(userHolder.getUser().getId());
            msg.setToId(receiver.getId());
            msg.setCreatedDate(new Date());
            messageService.addMessage(msg);
            return ApiResponse.ofSuccess(msg);
        } catch (Exception e) {
            logger.error("add message failed" + e.getMessage());
            return ApiResponse.ofMessage(500, "server error, add message failed");
        }
    }

    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model) {
        try {
            int localUserId = userHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<>();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("msg", msg);
                int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                User user = userService.getUserById(targetId);
                vo.set("user", user);
                vo.set("unread", messageService.convesationUnreadCount(localUserId, msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
        } catch (Exception e) {
            logger.error("get msgList failed" + e.getMessage());
        }
        return "msg_list";
    }

    @RequestMapping(path = {"/msg/{conversationId}"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model, @PathVariable("conversationId") String conversationId) {
        try {
            List<Message> conversationList = messageService.getConversation(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("msg", msg);
                User user = userService.getUserById(msg.getFromId());
                if (user == null) {
                    continue;
                }
                vo.set("profile", user.getProfile());
                vo.set("userId", user.getId());
                vo.set("username", user.getName());
                messages.add(vo);
            }
            model.addAttribute("messages", messages);
        } catch (Exception e) {
            logger.error("get messages failed" + e.getMessage());
        }
        return "msg_detail";
    }
}

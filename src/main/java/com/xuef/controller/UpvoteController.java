package com.xuef.controller;

import com.xuef.async.EventObj;
import com.xuef.async.EventProducer;
import com.xuef.async.EventType;
import com.xuef.base.ApiResponse;
import com.xuef.base.EntityType;
import com.xuef.model.Comment;
import com.xuef.model.User;
import com.xuef.model.UserHolder;
import com.xuef.service.CommentService;
import com.xuef.service.UpvoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 赞踩服务
 * Created by moveb on 2018/10/4.
 */
@Controller
public class UpvoteController {
    private static final Logger logger = LoggerFactory.getLogger(UpvoteController.class);
    @Autowired
    UpvoteService upvoteService;
    @Autowired
    UserHolder userHolder;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    CommentService commentService;

    @RequestMapping(path = "/like", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse like(Model model,
                            @RequestParam("commentId") int commentId){
        try {
            User user = userHolder.getUser();
            if (user == null) {
                return ApiResponse.ofNotLogin();
            }else{
                long likeCount = upvoteService.like(user.getId(), EntityType.ENTITY_COMMENT, commentId);
                Comment comment = commentService.getCommentById(commentId);
                eventProducer.fireEvent(new EventObj()
                        .setTriggerId(user.getId())
                        .setType(EventType.LIKE)
                        .setEntityId(commentId)
                        .setEntityType(EntityType.ENTITY_COMMENT)
                        .setEntityOwnerId(comment.getUserId())
                        .setExt("questionId", String.valueOf(comment.getEntityId()))
                );
                return ApiResponse.ofSuccess(likeCount);
            }
        }catch (Exception e){
            logger.error("server error: " + e.getMessage());
        }
        return ApiResponse.ofMessage(500, "server error!");
    }

    @RequestMapping(path = "/dislike", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse dislike(Model model,
                            @RequestParam("commentId") int commentId){
        try {
            User user = userHolder.getUser();
            if (user == null) {
                return ApiResponse.ofNotLogin();
            }else{
                long likeCount = upvoteService.disLike(user.getId(), EntityType.ENTITY_COMMENT, commentId);
                return ApiResponse.ofSuccess(likeCount);
            }
        }catch (Exception e){
            logger.error("server error: " + e.getMessage());
        }
        return ApiResponse.ofMessage(500, "server error!");
    }
}

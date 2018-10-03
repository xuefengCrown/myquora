package com.xuef.controller;

import com.xuef.base.Cons;
import com.xuef.base.EntityType;
import com.xuef.model.Comment;
import com.xuef.model.UserHolder;
import com.xuef.service.CommentService;
import com.xuef.service.QuestionService;
import com.xuef.service.SensitiveFilterService;
import com.xuef.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;

/**
 * Created by moveb on 2018/10/3.
 */
@Controller
public class CommentController {
    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
    @Autowired
    UserHolder userHolder;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    QuestionService questionService;
    @Autowired
    SensitiveFilterService sensitiveFilterService;

    @RequestMapping(path = {"/comment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("questionId") int questionId,
                             @RequestParam("content") String content) {
        try {
            content = HtmlUtils.htmlEscape(content);
            content = sensitiveFilterService.filterSensitive(content);
            Comment comment = new Comment();
            if (userHolder.getUser() != null) {
                comment.setUserId(userHolder.getUser().getId());
            } else {
                comment.setUserId(Cons.ANONYMOUS_USERID);
            }
            comment.setContent(content);
            comment.setEntityId(questionId);
            comment.setEntityType(EntityType.ENTITY_QUESTION);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);

            commentService.addComment(comment);
            // 更新question的评论数量
            int count = commentService.getCommentNum(comment.getEntityId(), comment.getEntityType());
            // TODO 怎么异步化(更新评论数量不应该在添加评论时做，需要将其分离出去，异步化)
            // 如果不分离出去，这儿的两步应该属于同一个事务，这里没做。因为后面要分离出去
            questionService.updateCommentCount(comment.getEntityId(), count);
        } catch (Exception e) {
            logger.error("comment add failed" + e.getMessage());
        }
        // TODO 这儿最好是局部刷新，而不该是整个页面刷新。如何实现？
        return "redirect:/question/" + String.valueOf(questionId);
    }
}

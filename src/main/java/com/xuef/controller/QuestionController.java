package com.xuef.controller;

import com.xuef.base.ApiResponse;
import com.xuef.base.EntityType;
import com.xuef.model.Comment;
import com.xuef.model.Question;
import com.xuef.model.User;
import com.xuef.model.UserHolder;
import com.xuef.vo.ViewComment;
import com.xuef.service.CommentService;
import com.xuef.service.QuestionService;
import com.xuef.service.SensitiveFilterService;
import com.xuef.service.UserService;
import com.xuef.vo.ViewObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class QuestionController {
    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    QuestionService questionService;
    @Autowired
    UserHolder userHolder;
    @Autowired
    UserService userService;
    @Autowired
    SensitiveFilterService sensitiveFilterService;

    @Autowired
    CommentService commentService;
    @RequestMapping(value = "/question/add", method = {RequestMethod.POST})
    @ResponseBody
    public ApiResponse addQuestion(@RequestParam("title") String title,
                                   @RequestParam("content") String content) {
        try {
            Question question = new Question();
            // html特殊字符过滤
            question.setContent(HtmlUtils.htmlEscape(content));
            question.setTitle(HtmlUtils.htmlEscape(title));
            // 敏感词过滤
            question.setTitle(sensitiveFilterService.filterSensitive(question.getTitle()));
            question.setContent(sensitiveFilterService.filterSensitive(question.getContent()));
            question.setCreatedDate(new Date());

            if (userHolder.getUser() == null) {
                return ApiResponse.ofMessage(10000, "not login");
            } else {
                question.setUserId(userHolder.getUser().getId());
            }
            if (questionService.addQuestion(question) > 0) {
                return ApiResponse.ofSuccess("question added");
            }
        } catch (Exception e) {
            logger.error("question add failed!: " + e.getMessage());
        }
        return ApiResponse.ofMessage(1, "question add failed");
    }
    private void setQuestionView(Question q, ViewObj v, User user){
        v.setContent(q.getContent());
        v.setCreatedDate(q.getCreatedDate());
        v.setProfile(user.getProfile());
        v.setUserId(user.getId());
        v.setCommentNum(q.getCommentNum());
        v.setQuestionId(q.getId());
        v.setTitle(q.getTitle());
        v.setUsername(user.getName());
    }

    @RequestMapping(value = "/question/{id}",method = RequestMethod.GET)
    public String questionDetail(Model model, @PathVariable("id") int id){
        Question question = questionService.getById(id);
        ViewObj questionView = new ViewObj();
        setQuestionView(question, questionView, userService.getUserById(question.getUserId()));
        List<Comment> commentList = commentService.getCommentsByEntity(id, EntityType.ENTITY_QUESTION);
        List<ViewComment> comments = new ArrayList<>();
        for (Comment comment : commentList) {
            /**
             * ViewObject的设计很不合理，如果comment有几百个字段，那要怎么办？
             */
            // TODO
            ViewComment c = new ViewComment();
            c.setId(comment.getId());
            c.setContent(comment.getContent());
            c.setCreatedDate(comment.getCreatedDate());
            c.setEntityType(comment.getEntityType());
            c.setEntityId(comment.getEntityId());
            c.setStatus(comment.getStatus());
            c.setUser(userService.getUserById(comment.getUserId()));
            comments.add(c);
        }

        model.addAttribute("question", questionView);
        model.addAttribute("comments", comments);
        return "qDetail";
    }
}
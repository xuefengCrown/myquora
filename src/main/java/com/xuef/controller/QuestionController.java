package com.xuef.controller;

import com.xuef.base.ApiResponse;
import com.xuef.base.EntityType;
import com.xuef.model.Comment;
import com.xuef.model.Question;
import com.xuef.model.User;
import com.xuef.model.UserHolder;
import com.xuef.service.*;
import com.xuef.vo.ViewComment;
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
    @Autowired
    UpvoteService upvoteService;

    @Autowired
    FollowingService followingService;

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

    @RequestMapping(value = "/question/{qid}",method = RequestMethod.GET)
    public String questionDetail(Model model, @PathVariable("qid") int qid){
        Question question = questionService.getById(qid);
        ViewObj questionView = new ViewObj();
        setQuestionView(question, questionView, userService.getUserById(question.getUserId()));
        // 设置当前问题的粉丝数
        questionView.setFansCount(followingService.getFansCount(EntityType.ENTITY_QUESTION, qid));

        // 问题的评论列表
        List<Comment> commentList = commentService.getCommentsByEntity(qid, EntityType.ENTITY_QUESTION);
        List<ViewComment> comments = new ArrayList<>();
        User curUser = userHolder.getUser();
        for (Comment comment : commentList) {
            /**
             * ViewXXX的设计很不合理，如果comment有几百个字段，那要怎么办？
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
            if(curUser != null){
                c.setLikeStatus(upvoteService.getLikeStatus(curUser.getId(), EntityType.ENTITY_COMMENT, comment.getId()));
            }else{
                c.setLikeStatus(0);
            }
            c.setLikeCount(upvoteService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
            comments.add(c);
        }
        // 粉丝列表
        List<User> fansList = new ArrayList<>();
        List<Integer> fansIds = followingService.getFans(EntityType.ENTITY_QUESTION, qid, 10);
        for(int fansId : fansIds){
            User u = userService.getUserById(fansId);
            fansList.add(u);
        }
        // 当前用户是否关注了该问题?
        if(curUser != null){
            boolean isFans = followingService.isFans(curUser.getId(), EntityType.ENTITY_QUESTION, qid);
            model.addAttribute("isFans", isFans);
        }else{
            model.addAttribute("isFans", false);
        }

        model.addAttribute("question", questionView);
        model.addAttribute("comments", comments);
        model.addAttribute("fansList", fansList);
        return "question_detail";
    }
}
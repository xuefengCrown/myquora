package com.xuef.controller;

import com.xuef.base.ApiResponse;
import com.xuef.model.Question;
import com.xuef.model.UserHolder;
import com.xuef.service.QuestionService;
import com.xuef.service.SensitiveFilterService;
import com.xuef.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;

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
}
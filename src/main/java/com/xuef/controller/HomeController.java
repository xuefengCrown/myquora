package com.xuef.controller;

import com.xuef.model.*;
import com.xuef.service.QuestionService;
import com.xuef.service.UserService;
import com.xuef.vo.ViewObj;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class HomeController {
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    @Autowired
    UserHolder userHolder;

    private List<ViewObj> getQuestions(int userId, int offset, int limit) {
        List<Question> questionList = questionService.getLatestQuestions(userId, offset, limit);
        List<ViewObj> vos = new ArrayList<>();
        User user = null;
        for (Question question : questionList) {
            user = userService.getUserById(question.getUserId());
            ViewObj vo = new ViewObj();
            vo.setQuestionId(question.getId());
            vo.setTitle(question.getTitle());
            vo.setUsername(user.getName());
            vo.setProfile(user.getProfile());
            vo.setUserId(user.getId());
            vo.setContent(question.getContent());
            vo.setCommentNum(question.getCommentNum());
            vo.setCreatedDate(question.getCreatedDate());
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model,
                        @RequestParam(value = "pop", defaultValue = "0") int pop) {
        int userId = 7;
        if(userHolder.getUser() != null){
            userId = userHolder.getUser().getId();
        }
        model.addAttribute("vos", getQuestions(userId, 0, 10));
        return "index";
    }

    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId) {
        model.addAttribute("vos", getQuestions(userId, 0, 10));
        return "index";
    }
}

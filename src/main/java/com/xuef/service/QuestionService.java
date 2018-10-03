package com.xuef.service;

import com.xuef.dao.QuestionDao;
import com.xuef.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class QuestionService {
    @Autowired
    QuestionDao questionDao;

    public Question getById(int id) {
        return questionDao.getById(id);
    }

    public int addQuestion(Question question) {
        question.setTitle(HtmlUtils.htmlEscape(question.getTitle()));
        question.setContent(HtmlUtils.htmlEscape(question.getContent()));
        // 敏感词过滤
        return questionDao.addQuestion(question) > 0 ? question.getId() : 0;
    }

    public List<Question> getLatestQuestions(int userId, int offset, int limit) {
        return questionDao.selectLatestQuestions(userId, offset, limit);
    }

    public int updateCommentCount(int id, int count) {
        return questionDao.updateCommentCount(id, count);
    }
}

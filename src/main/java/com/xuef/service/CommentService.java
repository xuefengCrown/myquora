package com.xuef.service;

import com.xuef.dao.CommentDao;
import com.xuef.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by moveb on 2018/10/3.
 */
@Service
public class CommentService {
    @Autowired
    private CommentDao commentDao;

    public Comment getCommentById(int id){
        return commentDao.getCommentById(id);
    }
    public List<Comment> getCommentsByEntity(int entityId, int entityType) {
        return commentDao.selectByEntity(entityId, entityType);
    }

    public int addComment(Comment comment) {
        return commentDao.addComment(comment);
    }

    public int getCommentNum(int entityId, int entityType) {
        return commentDao.getCommentNum(entityId, entityType);
    }

    public void deleteComment(int entityId, int entityType) {
        // dao层不认业务，只认操作(updateStatus)
        // service层反应业务(deleteComment)
        commentDao.updateStatus(entityId, entityType, 1);
    }
    public int getUserCommentNum(int userId){
        return commentDao.getUserCommentNum(userId);
    }
}

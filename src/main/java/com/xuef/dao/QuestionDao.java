package com.xuef.dao;

import com.xuef.model.Question;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuestionDao {
    String TABLE_NAME = " question ";
    String INSERT_FIELDS = " title, content, created_date, user_id, comment_num ";
    String SELECT_FIELDS = " id, " + INSERT_FIELDS;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELDS,
            ") values (#{title},#{content},#{createdDate},#{userId},#{commentNum})"})
    int addQuestion(Question question);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where user_id=#{userId}"})
    List<Question> selectLatestQuestions(@Param("userId") int userId, @Param("offset") int offset,
                                         @Param("limit") int limit);

    @Select({"select ", SELECT_FIELDS, " from ", TABLE_NAME, " where id=#{id}"})
    Question getById(int id);

    @Update({"update ", TABLE_NAME, " set comment_count = #{commentNum} where id=#{id}"})
    int updateCommentCount(@Param("id") int id, @Param("commentNum") int commentCount);
}

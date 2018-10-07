package com.xuef.controller;

import com.xuef.base.ApiResponse;
import com.xuef.base.EntityType;
import com.xuef.model.Question;
import com.xuef.model.User;
import com.xuef.model.UserHolder;
import com.xuef.service.CommentService;
import com.xuef.service.FollowingService;
import com.xuef.service.QuestionService;
import com.xuef.service.UserService;
import com.xuef.vo.ViewObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by moveb on 2018/10/7.
 */
@Controller
public class FollowingController {
    @Autowired
    FollowingService followingService;
    @Autowired
    UserHolder userHolder;

    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;

    @RequestMapping(path = "/followU", method = RequestMethod.POST)
    public ApiResponse followU(@RequestParam("userId") int userId){
        User follower = userHolder.getUser();
        if(follower == null){
            return ApiResponse.ofNotLogin();
        }
        boolean res = followingService.follow(follower.getId(), EntityType.ENTITY_USER, userId);

        // TODO eventProducer.fireEvent

        // 若关注成功，返回当前用户所关注用户的总数量
        long followingCount = followingService.getFollowingCount(follower.getId(), EntityType.ENTITY_USER);
        return res ? ApiResponse.ofSuccess(followingCount) : ApiResponse.ofMessage(500, "follow user failed");
    }
    @RequestMapping(value = "/followQ", method = RequestMethod.POST)
    @ResponseBody
    public ApiResponse followQ(@RequestParam("questionId") int questionId){
        User follower = userHolder.getUser();
        if(follower == null){
            return ApiResponse.ofNotLogin();
        }
        Question question =questionService.getById(questionId);
        boolean res = followingService.follow(follower.getId(), EntityType.ENTITY_QUESTION, questionId);
        // TODO eventProducer.fireEvent

        Map<String, Object> exts = new HashMap<>();
        exts.put("follower", follower);
        exts.put("fansCount", followingService.getFansCount(EntityType.ENTITY_QUESTION, questionId));
        return res ? ApiResponse.ofSuccess(exts) : ApiResponse.ofMessage(500, "follow question failed");
    }

    @RequestMapping(path = {"/unfollowQ"}, method = {RequestMethod.POST})
    @ResponseBody
    public ApiResponse unfollowQ(@RequestParam("questionId") int questionId) {
        User follower = userHolder.getUser();
        if(follower == null){
            return ApiResponse.ofNotLogin();
        }
        Question q = questionService.getById(questionId);

        boolean res = followingService.unfollow(follower.getId(), EntityType.ENTITY_QUESTION, questionId);

        // TODO eventProducer.fireEvent

        Map<String, Object> exts = new HashMap<>();
        exts.put("userId", follower.getId());
        exts.put("fansCount", followingService.getFansCount(EntityType.ENTITY_QUESTION, questionId));
        return res ? ApiResponse.ofSuccess(exts) : ApiResponse.ofMessage(500, "unfollow question failed");
    }

    /**
     * 查看当前用户的关注用户列表
     * 用户可以关注其他用户、话题、评论
     * @param model
     * @param uid
     * @return
     */
    @RequestMapping(path = {"/user/{uid}/followingsU"}, method = {RequestMethod.GET})
    public String followings(Model model, @PathVariable("uid") int uid) {
        List<Integer> followingIds = followingService.getFollowings(EntityType.ENTITY_USER, uid, 0, 10);
        if (userHolder.getUser() != null) {
            model.addAttribute("followings", ids2users(userHolder.getUser().getId(), followingIds));
        } else {
            model.addAttribute("followers", ids2users(0, followingIds));
        }
        model.addAttribute("followingCount", followingService.getFollowingCount(EntityType.ENTITY_USER, uid));
        model.addAttribute("follower", userService.getUserById(uid));
        return "followings";
    }

    @RequestMapping(path = {"/user/{uid}/fans"}, method = {RequestMethod.GET})
    public String followees(Model model, @PathVariable("uid") int userId) {
        List<Integer> fansIds = followingService.getFans(userId, EntityType.ENTITY_USER, 0, 10);

        if (userHolder.getUser() != null) {
            model.addAttribute("fans", ids2users(userHolder.getUser().getId(), fansIds));
        } else {
            model.addAttribute("fans", ids2users(0, fansIds));
        }
        model.addAttribute("fansCount", followingService.getFansCount(userId, EntityType.ENTITY_USER));
        model.addAttribute("user", userService.getUserById(userId));
        return "fans";
    }

    private List<ViewObject> ids2users(int curUserId, List<Integer> userIds) {
        List<ViewObject> users = new ArrayList<ViewObject>();
        for (Integer uid : userIds) {
            User user = userService.getUserById(uid);
            if (user == null) {
                continue;
            }
            ViewObject vo = new ViewObject();
            vo.set("user", user);
            vo.set("commentCount", commentService.getUserCommentNum(uid));
            vo.set("followingCount", followingService.getFollowingCount(EntityType.ENTITY_USER, uid));
            vo.set("fansCount", followingService.getFansCount(uid, EntityType.ENTITY_USER));
            if (curUserId != 0) {
                vo.set("isFans", followingService.isFans(curUserId, EntityType.ENTITY_USER, uid));
            } else {
                vo.set("isFans", false);
            }
            users.add(vo);
        }
        return users;
    }
}

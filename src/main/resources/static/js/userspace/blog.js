/*!
 * blog.html 页面脚本.
 */
"use strict";
$(function() {
	$.catalog("#catalog", ".post-content");
	
	// 处理删除博客事件
	
	$(".blog-content-container").on("click",".blog-delete-blog", function () { 
		// 获取 CSRF Token 
		//var csrfToken = $("meta[name='_csrf']").attr("content");
		//var csrfHeader = $("meta[name='_csrf_header']").attr("content");
		
		
		$.ajax({ 
			 url: blogUrl, 
			 type: 'DELETE', 
			 beforeSend: function(request) {
                 //reqiuest.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
             },
			 success: function(data){
				 if (data.success) {
					 toastr.info(data.message);
					 // 成功后，重定向
					 window.location = data.obj;
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 获取评论列表
	function getCommnet(blogId) {
		// 获取 CSRF Token 
		//var csrfToken = $("meta[name='_csrf']").attr("content");
		//var csrfHeader = $("meta[name='_csrf_header']").attr("content");
		
		$.ajax({ 
			 url: '/comment/blog',
			 type: 'GET', 
			 data:{"blogId":blogId},
			 beforeSend: function(request) {
	             //request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
	         },
			 success: function(data){
				 //console.log(data);
				 $("#mainContainer").html(data);
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	}

	// 提交评论
	$(".blog-content-container").on("click","#submitComment", function () { 
		// 获取 CSRF Token 
		//var csrfToken = $("meta[name='_csrf']").attr("content");
		//var csrfHeader = $("meta[name='_csrf_header']").attr("content");
 		
		$.ajax({ 
			 url: '/comment/blog',
			 type: 'POST', 
			 data:{
				 "blogId":blogId,
				 "commentContent":$('#commentContent').val()
			 },
			 beforeSend: function(request) {
                 //request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
             },
			 success: function(data){
				 if (data.success) {
					 // 清空评论框
					 $('#commentContent').val('');
					 //console.log(data);
					 // 获取评论列表
					 getCommnet(blogId);
				 }else if(data.code == '403'){
					 toastr.error("需要登录!");
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 删除评论
	$(".blog-content-container").on("click",".blog-delete-comment", function () { 
		// 获取 CSRF Token 
		var csrfToken = $("meta[name='_csrf']").attr("content");
		var csrfHeader = $("meta[name='_csrf_header']").attr("content");
 		
		$.ajax({ 
			 url: '/comments/'+$(this).attr("commentId")+'?blogId='+blogId, 
			 type: 'DELETE', 
			 beforeSend: function(request) {
                 request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token 
             },
			 success: function(data){
				 if (data.success) {
					 // 获取评论列表
					 getCommnet(blogId);
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	
	// like blog 提交
	$(".blog-content-container").on("click","#blog-like", function () {
		// 获取 CSRF Token 
		//var csrfToken = $("meta[name='_csrf']").attr("content");
		//var csrfHeader = $("meta[name='_csrf_header']").attr("content");
		$.ajax({
			 url: '/blog/like',
			 type: 'POST', 
			 data:{"blogId":blogId},
			 beforeSend: function(request) {
                 //request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
             },
			 success: function(data){
				 if (data.success) {
					 toastr.info(data.message);
					 // 成功后，重定向
					 window.location = blogUrl;
				 } else {
					 toastr.error("需要登录!");
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 取消喜欢
	$(".blog-content-container").on("click","#blog-dislike", function () {
		// 获取 CSRF Token 
		//var csrfToken = $("meta[name='_csrf']").attr("content");
		//var csrfHeader = $("meta[name='_csrf_header']").attr("content");
 		
		$.ajax({ 
			 url: '/blog/dislike',
			 type: 'POST',
			 data:{"blogId":blogId},
			 beforeSend: function(request) {
                 //request.setRequestHeader(csrfHeader, csrfToken); // 添加  CSRF Token
             },
			 success: function(data){
				 if (data.success) {
					 toastr.info(data.message);
					// 成功后，重定向
					 window.location = blogUrl;
				 } else {
					 toastr.error(data.message);
				 }
		     },
		     error : function() {
		    	 toastr.error("error!");
		     }
		 });
	});
	
	// 初始化 博客评论
	getCommnet(blogId);
	
});
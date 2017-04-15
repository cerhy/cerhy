CREATE TABLE `jc_personal_channel` (                                                          
                       `id` int(11) NOT NULL AUTO_INCREMENT,                                                       
                       `user_name` int(11) DEFAULT NULL COMMENT '访问者ID',                                     
                       `channel_id` int(11) DEFAULT NULL COMMENT '栏目id',                                       
                       `channel_type` int(11) DEFAULT NULL COMMENT '栏目类型(1,学科教研;2,市县教研)',  
                       `channel_name` varchar(50) DEFAULT NULL COMMENT '栏目名称',                             
                       PRIMARY KEY (`id`)                                                                          
                     ) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='个人栏目信息表'         

CREATE TABLE `jc_join_group` (                                                                             
                 `join_id` int(11) NOT NULL AUTO_INCREMENT,                                                               
                 `join_code` varchar(20) DEFAULT NULL COMMENT '组唯一编码',                                          
                 `create_user_id` int(11) DEFAULT NULL COMMENT '组创建人ID',                                          
                 `join_user_id` int(11) DEFAULT NULL COMMENT '加入组人员ID',
		 `columns_id` int(11) DEFAULT NULL COMMENT '群组ID',                                         
                 `join_time` datetime DEFAULT NULL COMMENT '加入时间',                                                
                 PRIMARY KEY (`join_id`),                                                                                 
                 KEY `fk_jc_blog_create_user_id` (`create_user_id`),                                                      
                 KEY `fk_jc_blog_join_user_id` (`join_user_id`),
                 KEY `fk_jc_blog_columns_id` (`columns_id`),                                         
                 CONSTRAINT `fk_jc_blog_create_user_id` FOREIGN KEY (`create_user_id`) REFERENCES `jc_user` (`user_id`),  
                 CONSTRAINT `fk_jc_blog_join_user_id` FOREIGN KEY (`join_user_id`) REFERENCES `jc_user` (`user_id`),
                 CONSTRAINT `fk_jc_blog_columns_id` FOREIGN KEY (`columns_id`) REFERENCES `columns` (`column_id`)       
               ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='加入组人员信息表'   
               
               
alter table `columns` add column `unique_code` varchar(20) DEFAULT NULL COMMENT '群组唯一标识码',               
               
               
               
               
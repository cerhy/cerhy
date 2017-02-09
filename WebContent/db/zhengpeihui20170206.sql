CREATE TABLE `jc_blog_visitor` (                                                                         
                   `vis_id` int(11) NOT NULL AUTO_INCREMENT,                                                              
                   `visitor_id` int(11) DEFAULT NULL COMMENT '访问者ID',                                               
                   `by_visitor_id` int(11) DEFAULT NULL COMMENT '被访问者ID',                                         
                   `visitor_time` datetime DEFAULT NULL COMMENT '访问时间',                                           
                   PRIMARY KEY (`vis_id`),                                                                                
                   KEY `fk_jc_blog_visitor_id` (`visitor_id`),                                                            
                   KEY `fk_jc_blog_by_visitor_id` (`by_visitor_id`),                                                      
                   CONSTRAINT `fk_jc_blog_by_visitor_id` FOREIGN KEY (`by_visitor_id`) REFERENCES `jc_user` (`user_id`),  
                   CONSTRAINT `fk_jc_blog_visitor_id` FOREIGN KEY (`visitor_id`) REFERENCES `jc_user` (`user_id`)         
                 ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='访问记录表' 
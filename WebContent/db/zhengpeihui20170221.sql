CREATE TABLE `jc_postil_info` (                                                                           
                  `postil_id` int(11) NOT NULL AUTO_INCREMENT,                                                            
                  `add_html` varchar(3000) DEFAULT NULL COMMENT '批注的内容的样式',                               
                  `input_content` varchar(255) DEFAULT NULL COMMENT '批注的内容',                                    
                  `content_id` int(11) DEFAULT NULL COMMENT '被批注的文章ID',                                       
                  `postil_user_id` int(11) DEFAULT NULL COMMENT '批注人ID',                                            
                  `create_time` datetime DEFAULT NULL COMMENT '批注时间',                                             
                  `div_id` varchar(255) DEFAULT NULL COMMENT '批注div的Id',                                            
                  PRIMARY KEY (`postil_id`),                                                                              
                  KEY `fk_jc_blog_postil_user_id` (`postil_user_id`),                                                     
                  CONSTRAINT `fk_jc_blog_postil_user_id` FOREIGN KEY (`postil_user_id`) REFERENCES `jc_user` (`user_id`)  
                ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='批注记录表' 
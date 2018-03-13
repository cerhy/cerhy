CREATE TABLE `jc_blog_notice` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户id',
  `notice` varchar(10000) DEFAULT NULL COMMENT '公告',
  `synopsis` varchar(8000) DEFAULT NULL COMMENT '简介',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
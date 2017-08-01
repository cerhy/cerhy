CREATE TABLE `jc_content_stick` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `content_id` int(11) NOT NULL COMMENT '文章id',
  `content_title` varchar(150) NOT NULL COMMENT '文章标题',
  `content_time` datetime NOT NULL COMMENT '文章时间',
  `stick_user_id` int(11) NOT NULL COMMENT '置顶用户id',
  `stick_time` datetime NOT NULL COMMENT '置顶时间',
  `path` varchar(30) NOT NULL COMMENT '路径',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=65 DEFAULT CHARSET=utf8 COMMENT='CMS文章置顶表';


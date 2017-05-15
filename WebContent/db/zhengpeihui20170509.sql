alter table `jc_user` add column `areacode` int(2) DEFAULT NULL COMMENT '手机国家地区代码';
alter table `jc_user` add column `type` int(2) DEFAULT NULL COMMENT '用户类型:0:教师,1:学生,2:教研工作人员,3:家长,4:其他';
alter table `jc_user` add column `nickname` varchar(50) DEFAULT NULL COMMENT '昵称';


CREATE TABLE `jc_focus` (
`focus_id`  int(11) NOT NULL AUTO_INCREMENT ,
`user_id`  int(11) NOT NULL ,
`user_name`  varchar(60) NOT NULL ,
`focus_user_id`  int(11) NOT NULL ,
`focus_user_name`  varchar(60) NOT NULL ,
`focus_time`  varchar(30) NOT NULL ,
PRIMARY KEY (`focus_id`)
)ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 AUTO_INCREMENT=1 ROW_FORMAT=COMPACT;
CREATE TABLE `columns` (
`column_id`  int(11) NOT NULL AUTO_INCREMENT ,
`user_id`  int(11) NOT NULL ,
`column_name`  varchar(30) NOT NULL ,
`order_id`  int(11)   ,
PRIMARY KEY (`column_id`)
)ENGINE=InnoDB DEFAULT CHARACTER SET=utf8 AUTO_INCREMENT=1 ROW_FORMAT=COMPACT;
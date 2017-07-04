alter table `columns` add column `type` int(2) DEFAULT '1' COMMENT '1:为普通栏目.2为群组';
alter table `columns` add column `columsLevel` int(2) DEFAULT NULL COMMENT '栏目等级';
alter table `columns` add column `parentId` int(11) DEFAULT NULL COMMENT '栏目父ID';
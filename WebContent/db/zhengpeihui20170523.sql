CREATE TABLE `jc_report_doc` (                                                         
                 `id` int(11) NOT NULL AUTO_INCREMENT,                                                
                 `reportname` varchar(50) DEFAULT NULL COMMENT '文档名称',                        
                 `reporturl` varchar(50) DEFAULT NULL COMMENT '文档路径',                         
                 `reporttime` datetime DEFAULT NULL COMMENT '文档上传时间',                     
                 PRIMARY KEY (`id`)                                                                   
               ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='特殊人员文档表'  

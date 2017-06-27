/*
Copyright (c) 2003-2012, CKSource - Frederico Knabben. All rights reserved.
For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR
		.addTemplates(
				'default',
				{
					imagesPath : CKEDITOR.getUrl(CKEDITOR.plugins
							.getPath('templates')
							+ 'templates/images/'),
					templates : [
							{
								title : '琼教研训文件模板',
								image : 'template1.gif',
								description : '',
								html : '<div style="padding: 6px 0px; text-align: center; border-bottom-color: #ff0000; border-bottom-width: 3px; border-bottom-style: solid;"><div style="padding: 30px 0px; color: #ff0000; font-family: 宋体; font-size: 38px; font-weight: bold;line-height: 40px;">海南省教育研究培训院文件</div><div style="text-align: center; line-height: 180%; font-family: 仿宋_GB2312, 仿宋; font-size: 18px;">琼教研训〔xxxx〕xx号</div></div><div style="padding: 32px 0px; text-align: center; line-height: 180%; font-family: 黑体; font-size: 26px;">标题<br />关于xxx的通知</div><div style="text-align: justify; line-height: 180%; font-family: 仿宋_GB2312, 仿宋; font-size: 18px;"><p style="margin: 0px; text-indent: 0px;">正文</p><div style="background: url(http://www.cerhy.com/r/cms/www/default/images/smp_jp.gif) no-repeat center; padding: 60px; text-align: center; float: right;">海南省教育研究培训院<div>xxxx年xx月xx日</div></div></div><br /><br /><br /><br /><br />'
							},
							{
								title : '琼教学会文件模板',
								image : 'template1.gif',
								description : '',
								html : '<div style="padding: 6px 0px; text-align: center; border-bottom-color: #ff0000; border-bottom-width: 3px; border-bottom-style: solid;"><div style="padding: 30px 0px; color: #ff0000; font-family: 宋体; font-size: 38px; font-weight: bold;">海 南 省 教 育 学 会 文 件</div><div style="text-align: center; line-height: 180%; font-family: 仿宋_GB2312, 仿宋; font-size: 18px;">琼教学会〔xxxx〕xx号</div></div><div style="padding: 32px 0px; text-align: center; line-height: 180%; font-family: 黑体; font-size: 26px;">标题 <br />关于xxx的通知</div><div style="text-align: justify; line-height: 180%; font-family: 仿宋_GB2312, 仿宋; font-size: 18px;"><p style="margin: 0px; text-indent: 0px;">正文</p><div style="background: url(http://www.cerhy.com/r/cms/www/default/images/smp_xh.gif) no-repeat center; padding: 60px; text-align: center; float: right;">海南省教育学会<div>xxxx年xx月xx日</div></div></div><br /><br /><br /><br /><br />'
							},
							{
								title : '琼教科研文件模板',
								image : 'template1.gif',
								description : '',
								html : '<div style="padding: 6px 0px; text-align: center; border-bottom-color: #ff0000; border-bottom-width: 3px; border-bottom-style: solid;"><div style="padding: 30px 0px; color: #ff0000; font-family: 宋体; font-size: 38px; font-weight: bold;line-height: 40px;">海南省教育科学规划领导小组办公室</div><div style="text-align: center; line-height: 180%; font-family: 仿宋_GB2312, 仿宋; font-size: 18px;">琼教科研〔xxxx〕xx号</div></div><div style="padding: 32px 0px; text-align: center; line-height: 180%; font-family: 黑体; font-size: 26px;">标题 <br />关于xxx的通知</div><div style="text-align: justify; line-height: 180%; font-family: 仿宋_GB2312, 仿宋; font-size: 18px;"><p style="margin: 0px; text-indent: 0px;">正文</p><div style="background: url(http://www.cerhy.com/r/cms/www/default/images/smp_gh.gif) no-repeat center; padding: 60px; text-align: center; float: right;">海南省教育科学规划领导小组办公室<div>xxxx年xx月xx日</div></div></div><br /><br /><br /><br /><br />'
							},
							{
								title : '教育学会、教培院联名文件模板',
								image : 'template1.gif',
								description : '',
								html : '<div style="padding: 6px 0; border-bottom: 3px solid #f00; text-align: center;">' +
								       '<div style="padding: 30px 0; font-family: 宋体; font-size: 38px; font-weight: bold; line-height: 58px; color: #f00;">' +
								       '<div style="margin-left: 40px; float: left">海 南 省 教 育 学 会<br />海南省教育研究培训院</div>' +
								       '<span style="margin: 30px 40px 0 0; float: right;">文件</span>' +
									   '<div style="clear: both; line-height: 0; height: 0;">&nbsp;</div>' +
								       '</div>' +
								       '<div style="font-family: 仿宋_GB2312,仿宋; font-size: 18px; text-align: center; line-height: 180%;">教育学会、教培院联名〔xxxx〕xx号</div>' +
							           '</div>' +
							           '<div style="font-family: 黑体; font-size: 26px; text-align: center; line-height: 180%; padding: 32px 0;">标题 <br />关于xxx的通知</div>' +
							           '<div style="font-family: 仿宋_GB2312,仿宋; font-size: 18px; text-align: justify; line-height: 180%;">正文' +
							           '<div style="background: url(http://www.cerhy.com/r/cms/www/default/images/smp_jp.gif) no-repeat center center; text-align: center; padding: 60px 20px; float: right;">海南省教育研究培训院<div>xxxx年xx月xx日</div></div>' +
							           '<div style="background: url(http://www.cerhy.com/r/cms/www/default/images/smp_xh.gif) no-repeat center center; text-align: center; padding: 60px 20px; float: right;">海南省教育学会<div>xxxx年xx月xx日</div></div>' +
							           '<div style="clear: both; line-height: 0; height: 0;">&nbsp;</div>' +
							           '<p>&nbsp;</p>' +
							           '</div><br /><br /><br /><br /><br />'
							},
							{
								title : '教培院函件文件模板',
								image : 'template1.gif',
								description : '',
								html : '<div style="padding: 6px 0 2px 0; border-bottom: 1px solid #f00; text-align: center;">' +
								       '<div style="padding: 30px 0; border-bottom: 4px solid #f00; font-family: 华文中宋,宋体; font-size: 38px; font-weight: bold; color: #f00;">海 南 省 教 育 研 究 培 训 院</div>' +
									   '</div>' +
									   '<div style="font-family: 黑体; font-size: 26px; text-align: center; line-height: 180%; padding: 32px 0;">标题 <br />关于xxx的通知</div>' +
									   '<div style="font-family: 仿宋_GB2312,仿宋; font-size: 18px; text-align: justify; line-height: 180%;">正文' +
									   '<div style="background: url(http://www.cerhy.com/r/cms/www/default/images/smp_jp.gif) no-repeat center center; text-align: center; padding: 60px; float: right;">海南省教育研究培训院<div>xxxx年xx月xx日</div></div>' +
									   '<div style="clear: both; line-height: 0; height: 0;">&nbsp;</div>' +
									   '<p>&nbsp;</p>' +
									   '</div><br /><br /><br /><br /><br />'
							},
							{
								title : '教育学会函件文件模板',
								image : 'template1.gif',
								description : '',
								html : '<div style="padding: 6px 0 2px 0; border-bottom: 1px solid #f00; text-align: center;">' +
									   '<div style="padding: 30px 0; border-bottom: 4px solid #f00; font-family: 华文中宋,宋体; font-size: 38px; font-weight: bold; color: #f00;">海　南　省　教　育　学　会</div>' +
									   '</div>' +
									   '<div style="font-family: 黑体; font-size: 26px; text-align: center; line-height: 180%; padding: 32px 0;">标题 <br />关于xxx的通知</div>' +
									   '<div style="font-family: 仿宋_GB2312,仿宋; font-size: 18px; text-align: justify; line-height: 180%;">正文' +
									   '<div style="background: url(http://www.cerhy.com/r/cms/www/default/images/smp_xh.gif) no-repeat center center; text-align: center; padding: 60px; float: right;">海南省教育学会<div>xxxx年xx月xx日</div></div>' +
									   '<div style="clear: both; line-height: 0; height: 0;">&nbsp;</div>' +
									   '<p>&nbsp;</p>' +
									   '</div><br /><br /><br /><br /><br />'
							},
							{
								title : '叙述式教学设计',
								image : 'template1.gif',
								description : '',
								html : '<h2 style="text-align: center;">课题名称</h2><h3 style="text-align: center;">作者</h3><p><strong>一、概述</strong></p><p>说明学科（数学、语言艺术等）和年级（中学、小学、学前等）;简要描述课题来源和所需课时；概述学习内容；概述这节课的价值以及学习内容的重要性。</p><p><strong>二、教学目标分析</strong></p><p>从知识与技能、过程与方法、情感态度与价值观三个维度对该课题预计达到的教学目标作出一个整体描述。</p><p><strong>三、学习者特征分析</strong></p><p>说明学习者在知识与技能、过程与方法、情感态度等三个方面的学习准备（学习起点），以及学生的学习风格。要注意结合特定的情景，切忌空泛。并说明教师是以何种方式进行的学习者特征分析，比如说是通过平时的观察、了解；或是通过预测题目的编制使用等。</p><p><strong>四、教学策略选择与设计</strong></p><p>说明本课题设计的基本理念，以及主要采用的教学与活动策略，以及这些策略实施过程中的关键问题。</p><p><strong>五、教学资源与工具设计</strong></p><p>一是为支持教师教的资源；二是支持学生学习的资源和工具，包括学习的环境、多媒体教学资源、特定的参考资料、参考网址、认知工具以及其它需要特别说明的传统媒体。如果是其它专题性学习、研究性学习方面的课程，可能还需要描述需要的人力支持及可获得情况。</p><p><strong>六、教学过程</strong></p><p>这一部分是该教学设计方案的关键所在。在这一部分，要说明教学的环节及所需的资源支持、具体的活动及其设计意图以及那些需要特别说明的教师引导语。最后，画出教学过程流程图。同时，流程图中需要清楚标注每一个阶段的教学目标、媒体和相应的评价方式。</p><p><strong>七、教学评价设计</strong></p><p>创建量规，向学生展示他们将如何被评价（来自教师和小组其他成员的评价）。另外，可以创建一个自我评价表，这样学生可以用它对自己的学习进行评价。</p><p><strong>八、帮助和总结</strong></p><p>说明教师以何种方式向学生提供帮助和指导，可以针对不同的学习阶段设计相应的不同帮助和指导，针对不同的学生提出不同水平的要求，给予不同的帮助。在学习结束后，对学生的学习做出简要总结。可以布置一些思考或练习题以强化学习效果，也可以提出一些问题或补充的链接鼓励学生超越这门课把思路拓展到其他内容领域。</p>'
							},
							{
								title : '主题式教学设计',
								image : 'template1.gif',
								description : '',
								html : '<table border="1" align="center" style="width: 80%;"><colgroup><col style="background: #f1f1f1;" /></colgroup><tbody><tr><td>姓名</td><td style="width: 80%; text-align: left;">&nbsp;</td></tr><tr><td>学校</td><td style="text-align: left;">&nbsp;</td></tr><tr><td>电话</td><td style="text-align: left;">&nbsp;</td></tr><tr><td>Email</td><td style="text-align: left;">&nbsp;</td></tr><tr><td>主题活动选题</td><td style="text-align: left;">&nbsp;</td></tr><tr><td>课程问题</td><td style="text-align: left;">&nbsp;</td></tr><tr><td>活动设计摘要</td><td style="text-align: left;">&nbsp;</td></tr><tr><td>学科领域</td><td style="text-align: left;">&nbsp;</td></tr><tr><td>年级</td><td style="text-align: left;">&nbsp;</td></tr><tr><td>学习目标/预期学习成果</td><td style="text-align: left;">&nbsp;</td></tr><tr><td>国家课程标准</td><td style="text-align: left;">&nbsp;</td></tr><tr><td>教学过程</td><td style="text-align: left;">&nbsp;</td></tr><tr><td>学习材料和资源</td><td style="text-align: left;">&nbsp;</td></tr></tbody></table>'
							},
							{
								title : '表格式教学设计1',
								image : 'template1.gif',
								description : '',
								html : '<table border="1" align="center" style="width: 80%;"><tbody><tr><td>案例名称</td><td colspan="3">&nbsp;</td><td>科目</td><td>&nbsp;</td></tr><tr><td>教学对象</td><td>&nbsp;</td><td>课时</td><td>&nbsp;</td><td>提供者</td><td>&nbsp;</td></tr><tr><td style="text-align: left;" colspan="6">一、教材内容分析</td></tr><tr><td style="text-align: left;" colspan="6"><br /><br /></td></tr><tr><td style="text-align: left;" colspan="6">二、教学目标（知识与技能、过程与方法、情感态度与价值观）</td></tr><tr><td style="text-align: left;" colspan="6"><br /><br /></td></tr><tr><td style="text-align: left;" colspan="6">三、学习者特征分析</td></tr><tr><td style="text-align: left;" colspan="6"><br /><br /></td></tr><tr><td style="text-align: left;" colspan="6">四、教学策略选择与设计</td></tr><tr><td style="text-align: left;" colspan="6"><br /><br /></td></tr><tr><td style="text-align: left;" colspan="6">五、教学环境及资源准备</td></tr><tr><td style="text-align: left;" colspan="6"><br /><br /></td></tr><tr><td style="text-align: left;" colspan="6">六、教学过程</td></tr><tr><td style="text-align: left;" colspan="6"><br /><br /></td></tr><tr><td style="text-align: left;" colspan="6">七、教学评价设计</td></tr><tr><td style="text-align: left;" colspan="6"><br /><br /></td></tr><tr><td style="text-align: left;" colspan="6">八、帮助和总结</td></tr><tr><td style="text-align: left;" colspan="6"><br /><br /></td></tr></tbody></table>'
							},
							{
								title : '表格式教学设计2',
								image : 'template1.gif',
								description : '',
								html : '<table border="1" align="center" style="width: 80%;"><tbody><tr><td style="background: #f1f1f1;" colspan="2">基本信息</td></tr><tr><td>课题</td><td style="width: 68%;">&nbsp;</td></tr><tr><td>作者及工作单位</td><td>&nbsp;</td></tr><tr><td style="background: #f1f1f1;" colspan="2">教材分析</td></tr><tr><td style="text-align: left;" colspan="2"><br /><br /></td></tr><tr><td style="background: #f1f1f1;" colspan="2">学情分析</td></tr><tr><td style="text-align: left;" colspan="2"><br /><br /></td></tr><tr><td style="background: #f1f1f1;" colspan="2">教学目标</td></tr><tr><td style="text-align: left;" colspan="2"><br /><br /></td></tr><tr><td style="background: #f1f1f1;" colspan="2">教学重点和难点</td></tr><tr><td style="text-align: left;" colspan="2"><br /><br /></td></tr><tr><td style="background: #f1f1f1;" colspan="2">教学过程</td></tr><tr><td style="text-align: left;" colspan="2"><br /><br /></td></tr><tr><td style="background: #f1f1f1;" colspan="2">板书设计</td></tr><tr><td style="text-align: left;" colspan="2"><br /><br /></td></tr><tr><td style="background: #f1f1f1;" colspan="2">学生学习活动评价设计</td></tr><tr><td style="text-align: left;" colspan="2"><br /><br /></td></tr><tr><td style="background: #f1f1f1;" colspan="2">教学反思</td></tr><tr><td style="text-align: left;" colspan="2"><br /><br /></td></tr></tbody></table>'
							},
							{
								title : '课堂实录',
								image : 'template1.gif',
								description : '',
								html : '<h2 style="text-align: center;">课题名称</h2><p>时间：</p><p>地点：</p><p>执教：</p><hr /><p>师：</p><p>生：</p>'
							},
							{
								title : '研修日志',
								image : 'template1.gif',
								description : '',
								html : '<h2 style="text-align: center;">xxxx 研修日志</h2><p>日期：</p><table style="width: 100%;"><tbody><tr><td style="width: 120px;">课题名称</td><td>&nbsp;</td><td style="width: 80px;">主讲</td><td>&nbsp;</td></tr><tr><td style="text-align: left;" colspan="4"><p>1.本专题最引起我关注的问题<br /><br /></p><p>2.本专题最能吸引我的观点<br /><br /></p><p>3.学习本专题后我的反思<br /><br /></p></td></tr><tr><td colspan="4">评价</td></tr><tr><td style="text-align: left;" colspan="4"><p>1.我对今天的培训活动感到：满意/一般/不满意<br /><br /></p><p>2.我认为今天的培训活动有待改进之处：<br /><br /></p></td></tr></tbody></table>'
							},
							{
								title : '教育叙事',
								image : 'template1.gif',
								description : '',
								html : '<h2 style="text-align: center;">标题</h2><h3 style="font-size: 14px; text-align: center;">作者<br />日期</h3><p><strong>案例概述</strong></p><p><br /><br /></p><p><strong>案例描述</strong></p><p><br /><br /></p><p><strong>反思感悟</strong></p><p><br /><br /></p>'
							},
							{
								title : '问题研讨',
								image : 'template1.gif',
								description : '',
								html : '<p><strong>研讨主题：</strong> &nbsp;</p><p><strong>研讨时间：</strong> &nbsp;</p><p><strong>主持人：</strong> &nbsp;</p><p><strong>研讨要求：</strong></p><p>1.</p><p>2.</p><p>注：每人限发一条评论</p><hr /><p><strong>结果整理：</strong></p><p>&nbsp;</p><p>&nbsp;</p>'
							},
							{
								title : 'Image and Title',
								image : 'template1.gif',
								description : 'One main image with a title and text that surround the image.',
								html : '<h3><img style="margin-right: 10px" height="100" width="100" align="left"/>Type the title here</h3><p>Type the text here</p>'
							},
							{
								title : 'Strange Template',
								image : 'template2.gif',
								description : 'A template that defines two colums, each one with a title, and some text.',
								html : '<table cellspacing="0" cellpadding="0" style="width:100%" border="0"><tr><td style="width:50%"><h3>Title 1</h3></td><td></td><td style="width:50%"><h3>Title 2</h3></td></tr><tr><td>Text 1</td><td></td><td>Text 2</td></tr></table><p>More text goes here.</p>'
							},
							{
								title : 'Text and Table',
								image : 'template3.gif',
								description : 'A title with some text and a table.',
								html : '<div style="width: 80%"><h3>Title goes here</h3><table style="width:150px;float: right" cellspacing="0" cellpadding="0" border="1"><caption style="border:solid 1px black"><strong>Table title</strong></caption></tr><tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr><tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr><tr><td>&nbsp;</td><td>&nbsp;</td><td>&nbsp;</td></tr></table><p>Type the text here</p></div>'
							} ]
				});

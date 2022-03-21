Hello I love Java

git add 文件 将文件添加
git status 查看文件状态
git commit -m "xxx" 提交文件，编辑文件的修改状态
git push  上传（完整代码git push -u origin master 往后就不需要写完整，只需写git push）

git clone xx(如果项目重名，在后面多加一个项目名称)  下载远程链接的代码

git pull 拉取本地仓库,更新变动

git branch 查看分支
git branch feature1 创建分支
git branch -b feature2 创建分支并切换到当前分支

git checkout feature1 切换分支

git branch -d feature1 删除分支

git merge feature3 合并该分支到master

由于github上是没有我们本地仓库的分支的
所以就得创建
git checkout feature1
git push origin feature1

删除github上的分支
git push origin :feature1

当你本地的分支是feature1，远端像称为f1
git push origin feature1:f1


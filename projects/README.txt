This folder is used to organised the source code by projects, in preparation of migration to GitHub.
For each project to be migrated to its single GitHub repo, all available code will be organised using the standard SVN trunk/tags/branches structure.
A single level of folder is used for tags and branches to ease migration to git.
If a deeper hiearchy existing in SVN, it's converted with a single level with ### as the "path delimiter".
For instance, branches/workspace/eric/controller would become branches/workspace###eric###controller

! DO NOT MODIFY ANY OF THE CODE IN THIS FOLDER, AS A MIGRATION OF THAT BIT OF THE CODE IS IN PROGRESS !

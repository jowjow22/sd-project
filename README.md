#  Distributed Systems Project

> This is a project created as work for the subject of distributed systems, taught at the Federal Technological University of Paraná

# How to run this project

 First of all, you need to clone the project, you can use:
```bash
  git clone https://github.com/jowjow22/sd-project.git
```
If you've cloned the repo, or already have a copy of the project, you need to make sure that you have installed all the dependencies via maven, this step will change based on the IDE that you're using.
After that, you will need to run a MySql server, if you don't have it installed, you can see the step-by-step installation in this link [MySQL installer guide](https://dev.mysql.com/doc/mysql-installation-excerpt/5.7/en/).
<hr />

If you already have MySql server running, you will need to change the credentials to access your user on this file `hibernate.cfg.xml` and change those two lines with your MySql server credentials


```diff 
-<property name="hibernate.connection.username">currentUser</property>
-<property name="hibernate.connection.password">currentPassword</property>
+<property name="hibernate.connection.username">yourUser</property>
+<property name="hibernate.connection.password">yourPassword</property>
```

after that, you just need to run the Server.java file and after it start, run the Client.java file and test the features in this program. 🥳

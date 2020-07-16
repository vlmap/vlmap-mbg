# vlmap-mbg

#### 功能介绍

  1. 对mgb生成内容和自定义内容做文件分离分离，解决mbg自定义内容和生成内容管理混乱问题
  
  2. 支持通过Hibernate Entity 注解类生成Mapper 

#### 安装教程

1. git clone https://github.com/vlmap/vlmap-mbg.git

2. cd mybatis-mbg

3. mvn install

 

#### 使用说明
>快速体验

导入示例SQL并修改数据源，然后执行下面命令

```
#cd vlmap-mbg 目录
mvn clean  install
mvn mybatis-generator:generate -f sample.pom.xml
```



生成的目录结构
```
│      
├─sample-dao
│  └─src
│      └─main
│          ├─java
│          │  └─com
│          │      └─sample
│          │          └─mapper
│          │                  BaseStudentMapper.java
│          │                  BaseSysUserMapper.java
│          │                  StudentMapper.java
│          │                  SysUserMapper.java
│          │                  
│          └─resources
│              └─mapper
│                      BaseStudent.xml
│                      BaseSysUser.xml
│                      StudentMapper.xml
│                      SysUserMapper.xml
│                      
└─sample-model
    └─src
        └─main
            └─java
                └─com
                    └─sample
                        ├─entity
                        │      BaseStudent.java
                        │      BaseSysUser.java
                        │      Student.java
                        │      SysUser.java
                        │      
                        └─example
                                StudentExample.java

```

>快速集成

```xml
<!--这个插件应该最后加载-->
<plugin type="com.github.vlmap.mbg.plugins.SplitPlugin">
    <property name="type" value="com.github.vlmap.mbg.plugins.SplitPlugin"/>
    <!--指定抽象类所在的子包-->
    <property name="basePackage" value="base"/>
    <!--指定抽象类名称前缀-->
    <property name="baseClassPrefix" value="Base"/>
</plugin>


```
####MVN坐标
>Step 1. Add the JitPack repository to your build file
 ```xml
 
    <repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
>  Step 2. Add the dependency
```xml
    <dependency>
	    <groupId>com.github.vlmap</groupId>
	    <artifactId>vlmap-mbg</artifactId>
	    <version>1.0.0.RELEASE</version>
    </dependency>
```


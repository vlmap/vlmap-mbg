package com.github.vlmap.mbg.plugins;

import com.github.vlmap.mbg.core.IntrospectedTableUtils;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.*;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.config.PropertyRegistry;

import java.io.File;
import java.util.*;


/**
 * mgb生成内容和自定义内容分离,该插件应该被最后被加载
 *
 * @author vlmap
 */
public class SplitPlugin extends PluginAdapter {

    /**
     * Base类的子包
     */
    private String basePackage;
    /**
     * Base类的父类前缀
     */
    private String baseClassPrefix;

    @Override
    public boolean validate(List<String> list) {
        return true;
    }


    @Override
    public void initialized(IntrospectedTable introspectedTable) {
        basePackage = this.properties.getProperty("basePackage");
        baseClassPrefix = this.properties.getProperty("baseClassPrefix");
        if (basePackage == null) {
            basePackage = "base";
        }
        if (isBlank(baseClassPrefix)) {
            baseClassPrefix = "Base";
        }
        List<File> list = new ArrayList<>();

        list.add(new File(context.getJavaClientGeneratorConfiguration().getTargetProject()));
        list.add(new File(context.getJavaModelGeneratorConfiguration().getTargetProject()));
        list.add(new File(context.getSqlMapGeneratorConfiguration().getTargetProject()));
        for (File file : list) {
            if (!file.exists()) {
                file.mkdirs();
            }

        }


    }

    private String baseName(String packgeName, String prefix) {
        StringBuilder builder = new StringBuilder();
        if (!"".equals(packgeName) && null != packgeName) {
            builder.append(".").append(packgeName);
        }
        if (!"".equals(prefix) && null != prefix) {
            builder.append(".").append(prefix);
        }
        return builder.toString();
    }

    protected TopLevelClass getBaseModelClass(IntrospectedTable introspectedTable) {
        String key = "BaseModelClass";
        TopLevelClass result = (TopLevelClass) introspectedTable.getAttribute(key);
        if (result == null) {
            result = new TopLevelClass(new FullyQualifiedJavaType(new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()).getPackageName() + baseName(basePackage, baseClassPrefix) + new FullyQualifiedJavaType(introspectedTable.getBaseRecordType()).getShortName()));
            introspectedTable.setAttribute(key, result);
        }

        return result;
    }
    protected TopLevelClass getBasePrimaryKeyClass(IntrospectedTable introspectedTable) {
        String key = "BasePrimaryKeyClass";
        TopLevelClass result = (TopLevelClass) introspectedTable.getAttribute(key);
        if (result == null) {
            result = new TopLevelClass(new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType()));
            introspectedTable.setAttribute(key, result);
        }

        return result;
    }
    protected Interface getBaseMapperInterface(IntrospectedTable introspectedTable) {

        String key = "BaseMapperInterface";
        Interface result = (Interface) introspectedTable.getAttribute(key);
        if (result == null) {
            result = new Interface(new FullyQualifiedJavaType(new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType()).getPackageName() + baseName(basePackage, baseClassPrefix) + new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType()).getShortName()));
            introspectedTable.setAttribute(key, result);
        }
        return result;

    }
//    protected Interface getBaseMapperRepositoryInterface(IntrospectedTable introspectedTable) {
//
//        String key = "BaseMapperRepositoryInterface";
//        Interface result = (Interface) introspectedTable.getAttribute(key);
//        if (result == null) {
//            result = new Interface(new FullyQualifiedJavaType(new FullyQualifiedJavaType(introspectedTable.getMyBatis3JavaMapperType()).getPackageName() + baseName(basePackage, baseClassPrefix) + "Repository"));
//            introspectedTable.setAttribute(key, result);
//        }
//        return result;
//
//    }
    protected XmlElement getBaseMapperXml(IntrospectedTable introspectedTable) {
        String key = "BaseMapperXml";
        XmlElement result = (XmlElement) introspectedTable.getAttribute(key);
        if (result == null) {
            result = new XmlElement("mapper"); //$NON-NLS-1$
            introspectedTable.setAttribute(key, result);
        }
        return result;
    }




    /**
     * 生成Model类
     * 把当前类内容移动到父类 。 如果子类文件已经存在返回false不覆盖当前子类文件
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        if (!IntrospectedTableUtils.isHbmIntrospectedTable(introspectedTable)) {

            TopLevelClass baseModelClass = this.getBaseModelClass(introspectedTable);
            baseModelClass.getFields().addAll(topLevelClass.getFields());
            topLevelClass.getFields().clear();


            baseModelClass.getMethods().addAll(topLevelClass.getMethods());
            topLevelClass.getMethods().clear();


            baseModelClass.getInnerClasses().addAll(topLevelClass.getInnerClasses());
            topLevelClass.getInnerClasses().clear();

            baseModelClass.getInnerEnums().addAll(topLevelClass.getInnerEnums());
            topLevelClass.getInnerEnums().clear();

            baseModelClass.getJavaDocLines().addAll(topLevelClass.getJavaDocLines());
            topLevelClass.getJavaDocLines().clear();

            baseModelClass.setSuperClass(topLevelClass.getSuperClass());


            Set<FullyQualifiedJavaType> superInterfaceTypes = topLevelClass.getSuperInterfaceTypes();
            baseModelClass.getSuperInterfaceTypes().addAll(superInterfaceTypes);
            superInterfaceTypes.clear();


            topLevelClass.addImportedType(baseModelClass.getType());

            topLevelClass.setSuperClass(baseModelClass.getType());

        }


        GeneratedJavaFile gjf = new GeneratedJavaFile(topLevelClass,
                context.getJavaModelGeneratorConfiguration()
                        .getTargetProject(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter());

        return !Util.getTargetFile(gjf).exists();

    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        GeneratedJavaFile javaFile = new GeneratedJavaFile(topLevelClass, context.getJavaModelGeneratorConfiguration().getTargetProject(), context.getProperty("javaFileEncoding"), context.getJavaFormatter());
        File file = SplitPlugin.Util.getTargetFile(javaFile);
        if (file.exists()) {
            System.out.println("Delete  JavaFile:" + file.toString());
            file.delete();

        }

        return super.modelPrimaryKeyClassGenerated(topLevelClass, introspectedTable);
    }
    @Override
    public boolean providerGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        GeneratedJavaFile javaFile = new GeneratedJavaFile(topLevelClass, context.getJavaModelGeneratorConfiguration().getTargetProject(), context.getProperty("javaFileEncoding"), context.getJavaFormatter());
        File file = SplitPlugin.Util.getTargetFile(javaFile);
        if (file.exists()) {
            System.out.println("Delete  JavaFile:" + file.toString());
            file.delete();

        }
        return super.providerGenerated(topLevelClass, introspectedTable);
    }
    /**
     * 生成 Model.java 文件
     *
     * @param introspectedTable
     * @return
     */
    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

        List<GeneratedJavaFile> result = new ArrayList<>();


        if (!IntrospectedTableUtils.isHbmIntrospectedTable(introspectedTable)) {


            TopLevelClass baseModelClass = this.getBaseModelClass(introspectedTable);
            TopLevelClass primaryKeyClass = this.getBasePrimaryKeyClass(introspectedTable);


            baseModelClass.setVisibility(JavaVisibility.PUBLIC);
            baseModelClass.setAbstract(true);
            GeneratedJavaFile javaFile = new GeneratedJavaFile(baseModelClass, context.getJavaModelGeneratorConfiguration().getTargetProject(), context.getProperty("javaFileEncoding"), context.getJavaFormatter());
            File file = Util.getTargetFile(javaFile);
            if (file.exists()) {
                System.out.println("Delete BaseModel JavaFile:" + file.toString());
                file.delete();

            }

            result.add(javaFile);

	        //删除符合Key文件
            baseModelClass.setVisibility(JavaVisibility.PUBLIC);
            baseModelClass.setAbstract(true);
            javaFile = new GeneratedJavaFile(primaryKeyClass, context.getJavaModelGeneratorConfiguration().getTargetProject(), context.getProperty("javaFileEncoding"), context.getJavaFormatter());
            file = Util.getTargetFile(javaFile);
            if (file.exists()) {
                System.out.println("Delete BaseModel JavaFile:" + file.toString());
                file.delete();

            }


        }



        Interface baseMapperInterface = this.getBaseMapperInterface(introspectedTable);

        baseMapperInterface.setVisibility(JavaVisibility.PUBLIC);


        GeneratedJavaFile javaFile = new GeneratedJavaFile(baseMapperInterface, context.getJavaClientGeneratorConfiguration().getTargetProject(), context.getProperty("javaFileEncoding"), context.getJavaFormatter());
        File file = Util.getTargetFile(javaFile);
        if (file.exists()) {
            System.out.println("Delete BaseMapper JavaFile:" + file.toString());

            file.delete();
        }
        result.add(javaFile);

//
//        Interface baseMapperRepositoryInterface = this.getBaseMapperRepositoryInterface(introspectedTable);
//
//        javaFile = new GeneratedJavaFile(baseMapperRepositoryInterface, context.getJavaClientGeneratorConfiguration().getTargetProject(), context.getProperty("javaFileEncoding"), context.getJavaFormatter());
//        file = Util.getTargetFile(javaFile);
//        if (file.exists()) {
//            System.out.println("Delete BaseMapperRepository JavaFile:" + file.toString());
//
//            file.delete();
//        }
//        result.add(javaFile);

        return result;
    }


    @Override
    public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        String targetProject = this.getContext().getJavaModelGeneratorConfiguration().getTargetProject();
        GeneratedJavaFile javaFile = new GeneratedJavaFile(topLevelClass, targetProject, context.getProperty("javaFileEncoding"), context.getJavaFormatter());
        File file = Util.getTargetFile(javaFile);
        if (file.exists()) {
            System.out.println("Delete Example JavaFile:" + file.toString());

            file.delete();
        }
        return true;
    }

    @Override
    public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {


        XmlElement baseMapperXml = this.getBaseMapperXml(introspectedTable);
        XmlElement root = document.getRootElement();

        Iterator<Element> iterator = root.getElements().iterator();

        while (iterator.hasNext()) {
            Element element =   iterator.next();
            baseMapperXml.addElement(element);
            iterator.remove();
        }
//  <resultMap id="BaseResultMap" type="com.github.vlmap.mbg.test.EmbeddedIdEntity"
//               extends="com.sample.mapper.base.BaseEmbeddedIdEntityMapper.BaseResultMap"/>
//    <sql id="Base_Column_List">
//        <include refid="com.sample.mapper.base.BaseEmbeddedIdEntityMapper.Base_Column_List"/>
//    </sql>

        Interface baseMapperClass = this.getBaseMapperInterface(introspectedTable);
        String namespace=baseMapperClass.getType().getFullyQualifiedNameWithoutTypeParameters();

         XmlElement resultMap = new XmlElement("resultMap"); //$NON-NLS-1$
        resultMap.addAttribute(new Attribute("id",            introspectedTable.getBaseResultMapId()));

        String returnType=introspectedTable.getBaseRecordType();

        resultMap.addAttribute(new Attribute("type",                  returnType));
        resultMap.addAttribute(new Attribute("extends",                 namespace+"."+introspectedTable.getBaseResultMapId()));



        document.getRootElement().addElement(resultMap);

        XmlElement sql = new XmlElement("sql"); //$NON-NLS-1$
        sql.addAttribute(new Attribute("id",            introspectedTable.getBaseColumnListId()));

        XmlElement include = new XmlElement("include");
        include.addAttribute(new Attribute("refid",namespace +"."+introspectedTable.getBaseColumnListId()));
        sql.addElement(include);


        document.getRootElement().addElement(sql);


        document.getRootElement().addElement(new TextElement("<!--******自定义类容可以写在这，重复生成不会被覆盖******-->"));


        GeneratedXmlFile gxf = new GeneratedXmlFile(document,
                introspectedTable.getMyBatis3XmlMapperFileName(), introspectedTable.getMyBatis3XmlMapperPackage(),
                context.getSqlMapGeneratorConfiguration().getTargetProject(),
                true, context.getXmlFormatter());

        File targetFile = Util.getTargetFile(gxf);

        return !targetFile.exists();
    }
//    sqlMapGenerated

    @Override
    public boolean sqlMapGenerated(GeneratedXmlFile sqlMap, IntrospectedTable introspectedTable) {
        try {
            sqlMap.getFormattedContent();

        } catch (NullPointerException e) {
            return false;
        }

        return true;
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
        List<GeneratedXmlFile> result = new ArrayList<>();


        XmlElement baseMapperXml = this.getBaseMapperXml(introspectedTable);

        Interface baseMapperClass = this.getBaseMapperInterface(introspectedTable);

        Document document = new Document(
                XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID,
                XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);
        baseMapperXml.addAttribute(new Attribute("namespace", baseMapperClass.getType().getFullyQualifiedNameWithoutTypeParameters()));
        baseMapperXml.addElement(new TextElement("<!--******不要编辑这个文件,下次生成时会被覆盖******-->"));
        document.setRootElement(baseMapperXml);

        GeneratedXmlFile gxf = new GeneratedXmlFile(document, baseMapperClass.getType().getShortName() + ".xml", introspectedTable.getMyBatis3XmlMapperPackage() + (isBlank(basePackage) ? "" : "." + basePackage),
                context.getSqlMapGeneratorConfiguration().getTargetProject(),
                true, context.getXmlFormatter());

        File targetFile = Util.getTargetFile(gxf);

        if (targetFile.exists()) {
            System.out.println("Delete BaseMapper XmlFile:" + targetFile.toString());

            targetFile.delete();
        }
        result.add(gxf);


        return result;

    }

    public static boolean isBlank(String string) {
        return string == null || string.isEmpty();
    }

    /**
     * 生成Mapper JAVA 文件，
     *
     * @param interfaze
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
//        if(!"true".equalsIgnoreCase(getContext().getProperty("__Repository__"))){
//            Interface repositoryClass    =    getBaseMapperRepositoryInterface(introspectedTable);
//
//
//            for(Optation method:interfaze.getMethods()){
//                FullyQualifiedJavaType returnType=method.getReturnType();
//
//                repositoryClass.getMethods().add(method);
//            }
//            repositoryClass.getMethods().addAll(interfaze.getMethods());
//            interfaze.getMethods().clear();
//
//            repositoryClass.getImportedTypes().addAll(interfaze.getImportedTypes());
//            interfaze.getImportedTypes().clear();
//            getContext().addProperty("__Repository__","true");
//
//        }

        Interface baseMapperClass = this.getBaseMapperInterface(introspectedTable);

        baseMapperClass.getSuperInterfaceTypes().addAll(interfaze.getSuperInterfaceTypes());
        interfaze.getSuperInterfaceTypes().clear();


        interfaze.addSuperInterface(baseMapperClass.getType());

        baseMapperClass.getMethods().addAll(interfaze.getMethods());
        interfaze.getMethods().clear();

        baseMapperClass.getImportedTypes().addAll(interfaze.getImportedTypes());
        interfaze.getImportedTypes().clear();

        baseMapperClass.getFields().addAll(interfaze.getFields());
        interfaze.getFields().clear();


        baseMapperClass.getInnerInterfaces().addAll(interfaze.getInnerInterfaces());

        interfaze.getInnerInterfaces().clear();


        baseMapperClass.getFileCommentLines().addAll(interfaze.getFileCommentLines());

        interfaze.getFileCommentLines().clear();


        baseMapperClass.getAnnotations().addAll(interfaze.getAnnotations());
        interfaze.getAnnotations().clear();

        baseMapperClass.getJavaDocLines().addAll(interfaze.getJavaDocLines());
        interfaze.getJavaDocLines().clear();

        baseMapperClass.getStaticImports().addAll(interfaze.getStaticImports());
        interfaze.getStaticImports().clear();






        GeneratedJavaFile gjf = new GeneratedJavaFile(interfaze,
                context.getJavaClientGeneratorConfiguration()
                        .getTargetProject(),
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter());

        return !Util.getTargetFile(gjf).exists();
    }


    static class Util {


        public static File getTargetFile(GeneratedFile gf) {
            File directory = getDirectory(gf.getTargetProject(), gf.getTargetPackage());
            File targetFile = new File(directory, gf.getFileName());
            return targetFile;
        }

        public static File getDirectory(String targetProject, String targetPackage) {


            File project = new File(targetProject);
            if (!project.isDirectory()) {
//                throw new IOException("");
            }

            StringBuilder sb = new StringBuilder();
            StringTokenizer st = new StringTokenizer(targetPackage, "."); //$NON-NLS-1$
            while (st.hasMoreTokens()) {
                sb.append(st.nextToken());
                sb.append(File.separatorChar);
            }

            File directory = new File(project, sb.toString());
            if (!directory.isDirectory()) {
                boolean rc = directory.mkdirs();
                if (!rc) {
//                    throw new IOException("");
                }
            }

            return directory;

        }


    }


}

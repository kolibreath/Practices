package com.kolibreath.annotation_compiler

//import com.google.auto.service.AutoService
import com.kolibreath.annotations.BindView
import java.io.Writer
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

class AnnotationCompiler: AbstractProcessor() {

    private lateinit var filer: Filer
    private lateinit var messager: Messager

    // 初始化
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        filer = processingEnv.filer
        messager = processingEnv.messager
    }

    // 筛选出支持的注解类型
    override fun getSupportedAnnotationTypes(): MutableSet<String>
        =  mutableSetOf(BindView::class.java.canonicalName)

    // 支持的Java类型
    override fun getSupportedSourceVersion(): SourceVersion = processingEnv.sourceVersion

    /**
     * 任务目标
     * 1. 分类注解： 将想要的注解选择出来
     * 2. 生成目标文件
     */
    override fun process(
        elements: MutableSet<out TypeElement>,
        roundEnvironment: RoundEnvironment): Boolean {

        // Element的类型：
        // TypeElement: 类
        // VariableElement: 属性
        // ExecutableElement: 方法名
        val annElements = roundEnvironment.getElementsAnnotatedWith(BindView::class.java)
        // 得到使用了注解的类之后，将这些类进行区分
        // Map<String, List<Element>> String: 当前Element所在的类
        // 的全限定名称, Element 当前的类标记的属性名称
        val map = HashMap<String, ArrayList<Element>>()
        for(element in annElements) {
            val activityName = element.enclosingElement.simpleName.toString()
            val list = map[activityName]
            if (list == null) {
                map[activityName] = ArrayList()
                map[activityName]!!.add(element)
            } else list.add(element)
        }
        // 如果当前Map中没有 直接跳过
        if(map.keys.isNotEmpty()) {
            var writer: Writer?
            for(activityName in map.keys) {
                val elements1 = map[activityName]!!
                // 当前的Element对应的类 具有相同的包名
                val packageName = processingEnv.elementUtils
                        .getPackageOf(elements1[0].enclosingElement).toString()
                // 在具体的包名下面创建类型
                val sourceFile = filer.createSourceFile("$packageName.${activityName}\$ViewBinder")
                writer = sourceFile.openWriter()
                val insideStringBuffer = StringBuffer()
                for (element in elements1) {
                    // 控件名称
                    val variableName = element.simpleName.toString()
                    val variableType = element.asType().toString()
                    val variableId = element.getAnnotation(BindView::class.java).value
                    insideStringBuffer.append("target.$variableName = ($variableType) target.findViewById($variableId);\n")
                }
                // 外层部分：
                val wholeString = "package $packageName;\n" +
                        "\n" +
                        "import android.view.View;\n" +
                        "\n" +
                        "public class $activityName\$ViewBinder {\n" +
                        "    public $activityName\$ViewBinder(${packageName}.$activityName target) {\n" +
                        "       $insideStringBuffer "+
                        "    }\n" +
                        "}"
                writer.write(wholeString)
                writer.close()
            }
        }
        return false
    }
}
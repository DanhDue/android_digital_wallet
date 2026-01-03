package com.danhdue.libraries.testutils

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Assert
import org.junit.Before
import java.io.File
import java.lang.reflect.Field
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Base class for model test
 */
abstract class BaseModelTest<T> : MockkUnitTest() {
    lateinit var moshi: Moshi
    lateinit var jsonAdapter: JsonAdapter<T>

    @Before
    override fun setUp() {
        super.setUp()
        moshi =
            Moshi
                .Builder()
                // .add(ReferenceAdapterFactory())
                .addLast(KotlinJsonAdapterFactory())
                .build()
        jsonAdapter = moshi.adapter(getModelType())
    }

    /**
     * Use to get model type
     * @return type of model
     */
    abstract fun getModelType(): Type

    /**
     * Check if fields of model are match with keys of json
     * @param jsonFileName name of json file in test resources
     */
    fun checkModelFieldsMatchJsonKeys(jsonFileName: String) {
        val json = getJsonStringFromFile(jsonFileName)
        val model = jsonAdapter.fromJson(json)
        Assert.assertNotNull("Model should not be null", model)

        val jsonMap = moshi.adapter(Map::class.java).fromJson(json) as Map<String, Any>
        val modelFields = getModelFields(model!!.javaClass)

        jsonMap.keys.forEach { key ->
            val field = modelFields.find { it.name == key || it.getAnnotation(com.squareup.moshi.Json::class.java)?.name == key }
            Assert.assertNotNull("Field for key '$key' not found in model", field)
        }
    }

    private fun getJsonStringFromFile(fileName: String): String {
        val classLoader = javaClass.classLoader
        val file = File(classLoader!!.getResource(fileName).path)
        return file.readText()
    }

    private fun getModelFields(clazz: Class<*>): List<Field> {
        val fields = mutableListOf<Field>()
        var currentClass: Class<*>? = clazz
        while (currentClass != null && currentClass != Any::class.java) {
            fields.addAll(currentClass.declaredFields)
            currentClass = currentClass.superclass
        }
        return fields
    }

    /**
     * Get generic type of model
     * @return generic type of model
     */
    protected fun getGenericType(): Type {
        val superclass = javaClass.genericSuperclass
        if (superclass is ParameterizedType) {
            return superclass.actualTypeArguments[0]
        }
        throw RuntimeException("Generic type not found")
    }
}

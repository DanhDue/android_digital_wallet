/*
 * Copyright © 2026, danhdue.com
 * All Rights Reserved.
 */
package com.danhdue.framework.platform

import android.annotation.SuppressLint
import timber.log.Timber
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

private const val TAG = "MobileService"

data class EmuiVersion(
    val name: String,
    val code: Int,
)

object MobileService {
    /**
     * Emui information of Huawei Devices
     *
     * @return EmuiVersion object contains name and code fields name format is EmotionUI_X.X.X and code is integer
     */
    fun getDeviceEmuiVersion(): EmuiVersion = EmuiVersion(getEmuiVersionName(), getEmuiVersionCode())

    @SuppressLint("PrivateApi")
    private fun getEmuiVersionName(): String {
        val classType: Class<*>
        var emuiVerName = ""
        try {
            classType = Class.forName("android.os.SystemProperties")
            val getMethod: Method =
                classType.getDeclaredMethod("get", String::class.java)
            emuiVerName =
                getMethod.invoke(classType, "ro.build.version.emui") as String
        } catch (e: ClassNotFoundException) {
            Timber.tag(TAG).e("ClassNotFoundException")
        } catch (e: NoSuchMethodException) {
            Timber.tag(TAG).e("NoSuchMethodException")
        } catch (e: IllegalAccessException) {
            Timber.tag(TAG).e("IllegalAccessException")
        } catch (e: InvocationTargetException) {
            Timber.tag(TAG).e("InvocationTargetException")
        }
        Timber.tag(TAG).i("Emui version name: $emuiVerName")
        return emuiVerName
    }

    private fun getEmuiVersionCode(): Int {
        var returnObj: Any? = null
        var emuiVersionCode = 0
        try {
            val targetClass = Class.forName("com.huawei.android.os.BuildEx\$VERSION")
            val field: Field = targetClass.getDeclaredField("EMUI_SDK_INT")
            AccessibleObject.setAccessible(arrayOf(field), true)
            returnObj = field.get(targetClass)
            if (null != returnObj) {
                emuiVersionCode = (returnObj as Int?)!!
            }
        } catch (e: ClassNotFoundException) {
            Timber.tag(TAG).e("ClassNotFoundException: ")
        } catch (e: NoSuchFieldException) {
            Timber.tag(TAG).e("NoSuchFieldException: ")
        } catch (e: IllegalAccessException) {
            Timber.tag(TAG).e("IllegalAccessException: ")
        } catch (e: ClassCastException) {
            Timber
                .tag(TAG)
                .e("ClassCastException: getEMUIVersionCode is not a number $returnObj")
        }
        Timber.tag(TAG).i("emuiVersionCodeValue: %s", emuiVersionCode)
        return emuiVersionCode
    }
}

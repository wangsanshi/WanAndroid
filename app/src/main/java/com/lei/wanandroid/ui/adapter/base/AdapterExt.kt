package com.lei.wanandroid.ui.adapter.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType

fun ViewGroup.getItemView(@LayoutRes layoutResId: Int): View {
    return LayoutInflater.from(this.context).inflate(layoutResId, this, false)
}

typealias OnItemClickListener = (RecyclerView.Adapter<*>, View, Int) -> Unit
typealias OnItemLongClickListener = (RecyclerView.Adapter<*>, View, Int) -> Boolean
typealias OnItemChildClickListener = (RecyclerView.Adapter<*>, View, Int) -> Unit
typealias OnItemChildLongClickListener = (RecyclerView.Adapter<*>, View, Int) -> Boolean

@Suppress("DEPRECATION")
fun <VH : BaseViewHolder> RecyclerView.Adapter<VH>.setListeners(
    viewHolder: VH,
    childClickViewIds: LinkedHashSet<Int>,
    childLongClickViewIds: LinkedHashSet<Int>,
    onItemClickListener: OnItemClickListener? = null,
    onItemLongClickListener: OnItemLongClickListener? = null,
    onItemChildClickListener: OnItemChildClickListener? = null,
    onItemChildLongClickListener: OnItemChildLongClickListener? = null
) {
    onItemClickListener?.let {
        viewHolder.itemView.setOnClickListener { v ->
            val position = viewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) return@setOnClickListener
            it.invoke(this, v, position)
        }
    }

    onItemLongClickListener?.let {
        viewHolder.itemView.setOnLongClickListener { v ->
            val position = viewHolder.adapterPosition
            if (position == RecyclerView.NO_POSITION) return@setOnLongClickListener false
            it.invoke(this, v, position)
        }
    }

    onItemChildClickListener?.let {
        for (id in childClickViewIds) {
            viewHolder.itemView.findViewById<View>(id)?.let { childView ->
                if (!childView.isClickable) childView.isClickable = true
                childView.setOnClickListener { v ->
                    val position = viewHolder.adapterPosition
                    if (position == RecyclerView.NO_POSITION) return@setOnClickListener
                    it.invoke(this, v, position)
                }
            }
        }
    }

    onItemChildLongClickListener?.let {
        for (id in childLongClickViewIds) {
            viewHolder.itemView.findViewById<View>(id)?.let { childView ->
                if (!childView.isClickable) childView.isClickable = true
                childView.setOnLongClickListener { v ->
                    val position = viewHolder.adapterPosition
                    if (position == RecyclerView.NO_POSITION) return@setOnLongClickListener false
                    it.invoke(this, v, position)
                }
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
fun <VH : BaseViewHolder> RecyclerView.Adapter<VH>.createViewHolder(
    view: View
): VH {
    var temp: Class<*>? = javaClass
    var z: Class<*>? = null
    while (z == null && null != temp) {
        z = getInstancedGenericKClass(temp)
        temp = temp.superclass
    }
    // 泛型擦除会导致z为null
    val vh: VH? = if (z == null) {
        BaseViewHolder(view) as VH
    } else {
        createBaseGenericKInstance(z, view)
    }
    return vh ?: BaseViewHolder(view) as VH
}

private fun getInstancedGenericKClass(z: Class<*>): Class<*>? {
    try {
        val type = z.genericSuperclass
        if (type is ParameterizedType) {
            val types = type.actualTypeArguments
            for (temp in types) {
                if (temp is Class<*>) {
                    if (BaseViewHolder::class.java.isAssignableFrom(temp)) {
                        return temp
                    }
                } else if (temp is ParameterizedType) {
                    val rawType = temp.rawType
                    if (rawType is Class<*> && BaseViewHolder::class.java.isAssignableFrom(
                            rawType
                        )
                    ) {
                        return rawType
                    }
                }
            }
        }
    } catch (e: java.lang.reflect.GenericSignatureFormatError) {
        e.printStackTrace()
    } catch (e: TypeNotPresentException) {
        e.printStackTrace()
    } catch (e: java.lang.reflect.MalformedParameterizedTypeException) {
        e.printStackTrace()
    }
    return null
}

/**
 * try to create Generic VH instance
 *
 * @param z
 * @param view
 */
@Suppress("UNCHECKED_CAST")
fun <VH : BaseViewHolder> RecyclerView.Adapter<VH>.createBaseGenericKInstance(
    z: Class<*>,
    view: View
): VH? {
    try {
        val constructor: Constructor<*>
        // inner and unstatic class
        return if (z.isMemberClass && !Modifier.isStatic(z.modifiers)) {
            constructor = z.getDeclaredConstructor(javaClass, View::class.java)
            constructor.isAccessible = true
            constructor.newInstance(this, view) as VH
        } else {
            constructor = z.getDeclaredConstructor(View::class.java)
            constructor.isAccessible = true
            constructor.newInstance(view) as VH
        }
    } catch (e: NoSuchMethodException) {
        e.printStackTrace()
    } catch (e: IllegalAccessException) {
        e.printStackTrace()
    } catch (e: InstantiationException) {
        e.printStackTrace()
    } catch (e: InvocationTargetException) {
        e.printStackTrace()
    }

    return null
}
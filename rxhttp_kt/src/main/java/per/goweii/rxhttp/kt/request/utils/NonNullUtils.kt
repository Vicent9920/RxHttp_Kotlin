package per.goweii.rxhttp.kt.request.utils

object NonNullUtils {
    fun check(vararg maps: Map<*, *>?): Boolean {
        if(maps.isNullOrEmpty())return false

        for (map in maps) {
            if (check(map)) {
                return true
            }
        }
        return false
    }

    fun check(vararg collections: Collection<*>?): Boolean {
        if(collections.isNullOrEmpty())return false

        for (collection in collections) {
            if (check(collection)) {
                return true
            }
        }
        return false
    }

    fun check(vararg objects: Any?): Boolean {
        if(objects.isNullOrEmpty())return false
        for (o in objects) {
            if (check(o)) {
                return true
            }
        }
        return false
    }

    fun check(map: Map<*, *>?): Boolean {
        return map.isNullOrEmpty().not()
    }

    fun check(collection: Collection<*>?): Boolean {
        return collection.isNullOrEmpty().not()
    }

    fun check(o: Any?): Boolean {
        if (o == null) {
            return false
        }
        if (o is Array<*>) {
            return check(*o)
        }
        return when (o) {
            is Collection<*> -> {
                check(o)
            }
            is Map<*, *> -> {
                check(o)
            }
            else -> {
                true
            }
        }
    }
}
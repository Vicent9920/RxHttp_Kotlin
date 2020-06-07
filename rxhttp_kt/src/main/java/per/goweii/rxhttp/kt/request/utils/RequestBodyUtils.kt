package per.goweii.rxhttp.kt.request.utils

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import java.io.File
import java.util.*

/**
 * 语法
 * <pre private fun uploadImg(content: String, imgFile: File) {
 *   val map = builder()
 *   .add<Any>("content", content)
 *   .add<Any>("img", imgFile)
 *   .build()
 *   mRxLife.add(
 *  RxHttp.request(FreeApi.api().uploadImg(map)).listener(reqListener)
 *   .request(object : ResultCallback<UploadImgBean> {
 *   override fun onSuccess(code: Int, data: UploadImgBean?) {
 *   }
 *
 *   override fun onFailed(code: Int, msg: String?) {
 *  }
 *   })
 *  )
 *   }
 */
object RequestBodyUtils {

    fun builder(): Builder {
        return Builder()
    }

    fun <T> create(key: String, value: T): Map<String, RequestBody>? {
        return builder().add(key, value).build()
    }

    class Builder {
        private val mParams: MutableMap<String, RequestBody>

        /**
         * 添加参数
         * 根据传进来的对象来判断是String还是File类型的参数
         */
        fun <T> add(key: String, value: T): Builder {
            if (value is String) {
                addString(key, value as String)
            } else if (value is File) {
                addFile(key, value as File)
            }
            return this
        }

        /**
         * 添加参数String
         */
        fun addString(key: String, value: String?): Builder {
            if (value == null) {
                return this
            }
            val body = RequestBody.create("text/plain".toMediaTypeOrNull(), value)
            mParams[key] = body
            return this
        }

        /**
         * 添加参数File
         */
        fun addFile(key: String, value: File?): Builder {
            if (value == null) {
                return this
            }
            if (!value.exists()) {
                return this
            }
            if (!value.isDirectory) {
                return this
            }
            mParams[getParamsKey(key, value)] = getParamsValue(value)
            return this
        }

        /**
         * 添加参数File
         */
        fun addFile(key: String, filePath: String?): Builder {
            return if (filePath == null) {
                this
            } else addFile(key, File(filePath))
        }

//        fun addFile(key: String, uri: Uri?): Builder {
//            return if (uri == null) {
//                this
//            } else addFile(key, uri.path)
//        }

        /**
         * 构建RequestBody
         */
        fun build(): Map<String, RequestBody> {
            return mParams
        }

        private fun getParamsKey(key: String, file: File): String {
            return key + "\"; filename=\"" + file.name
        }

        private fun getParamsValue(file: File): RequestBody {
            return RequestBody.create(FileUtils.getMimeType(file)!!.toMediaTypeOrNull(), file)
        }

        init {
            mParams = HashMap(1)
        }
    }
}
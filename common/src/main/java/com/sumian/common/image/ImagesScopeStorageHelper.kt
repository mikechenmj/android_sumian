package com.sumian.common.image

import android.Manifest
import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import com.sumian.common.media.bean.Image
import java.io.ByteArrayOutputStream
import java.lang.ref.SoftReference

object ImagesScopeStorageHelper {

    private lateinit var mContext: Application
    private var mHandler: Handler = Handler()

    private var mImageChangeListeners: MutableList<ImageChangeListener> = mutableListOf()


    var images: SoftReference<HashMap<Int, Image>> = SoftReference(HashMap())

    private var mContentObserver: ContentObserver = object : ContentObserver(mHandler) {
        override fun onChange(selfChange: Boolean) {
            loadMedia()
        }
    }

    private val IMAGE_PROJECTION = arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.MINI_THUMB_MAGIC,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME)

    fun init(context: Application) {
        mContext = context
        context.contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, mContentObserver)
        loadMedia()
    }

    fun loadMedia() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        Thread {
            if (images.get() == null) {
                images = SoftReference(HashMap())
            }
            var data = mContext.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    null, null, IMAGE_PROJECTION[2] + " DESC")
            if (data == null) {
                return@Thread
            }
            var imagesMap = images.get() ?: HashMap()
            imagesMap.clear()
            val count = data.count
            if (count > 0) {
                data.moveToFirst()
                do {
                    val path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]))
                    val name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]))
                    val dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]))
                    val id = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[3]))
                    val thumbPath = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]))
                    val bucket = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[5]))

                    val image = Image()
                    image.rawPath = path
                    val contentPath = MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString() + "/" + id
                    image.contentPath = contentPath
                    image.name = name
                    image.date = dateTime
                    image.id = id
                    image.thumbPath = thumbPath
                    image.folderName = bucket

                    imagesMap[id] = image
                } while (data.moveToNext())
            }
            images = SoftReference(imagesMap)
            for (listener in mImageChangeListeners) {
                mHandler.post { listener.onChange(imagesMap) }
            }
        }.start()
    }

    fun registerImageChangeListener(listener: ImageChangeListener) {
        mImageChangeListeners.add(listener)
    }

    fun unregisterImageChangeListener(listener: ImageChangeListener) {
        mImageChangeListeners.remove(listener)
    }

    fun contentUriToByte(contentUri: String): ByteArray {
        var byteOutputStream = ByteArrayOutputStream()
        var inputStream = mContext.contentResolver.openInputStream(Uri.parse(contentUri))
        if (inputStream == null) {
            return byteArrayOf()
        }
        var bufferSize = 1024
        var buffer = ByteArray(bufferSize)
        var len: Int
        do {
            len = inputStream.read(buffer)
            byteOutputStream.write(buffer)
        } while (len != -1)
        return byteOutputStream.toByteArray()
    }

    fun generateContentUri(context: Context, name: String, mimeType: String, isPending: Boolean): Uri? {
        var contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, name)
        contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        if (isPending) {
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
    }

    fun isContentUriFileExisted(context: Context, uri: Uri): Boolean {
        try {
            context.contentResolver.openFileDescriptor(uri, "r").use { fd ->
                if (fd != null) {
                    return true
                }
            }
        } catch (e: Exception) {
            return false
        }
        return false
    }

    interface ImageChangeListener {
        fun onChange(map: HashMap<Int, Image>)
    }
}
package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.BaseApplication
import com.preetTractor.galaxyAndroid.R
import com.bumptech.glide.Glide
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.orderUi.model.response.ResponseSchemeList
import com.preetTractor.galaxyAndroid.databinding.ItemImageCustomerBinding

class AttachmentNotesAdapter(private val items: List<ResponseSchemeList.Data.Attachment>) : RecyclerView.Adapter<AttachmentNotesAdapter.InnerViewHolder>() {

    inner class InnerViewHolder(val binding: ItemImageCustomerBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
        val binding = ItemImageCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InnerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            if(item.File.toString().contains(".pdf") == true){

                Glide.with(BaseApplication.getInstance()).load(R.drawable.pdf_image).into(ivImage)
            }
            else{
                Glide.with(holder.itemView.context).load(BuildConfig.IMAGE_URL + item.File).into(ivImage)
            }
        }

        holder.itemView.setOnClickListener {
            val fileUrl = BuildConfig.IMAGE_URL+item.File // Your file URL

            if (fileUrl.contains(".pdf", ignoreCase = true)) {
                Toast.makeText(BaseApplication.getInstance(), "Downloading PDF...", Toast.LENGTH_SHORT).show()
                downloadFile(BaseApplication.getInstance(), fileUrl, "NoteDownloaded.pdf")
            } else {
                // Show image preview
                previewImage(BaseApplication.getInstance(), fileUrl)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}

private fun downloadFile(context: Context, fileUrl: String, fileName: String) {
    try {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(fileUrl)

        val request = DownloadManager.Request(uri).apply {
            setTitle(fileName)
            setDescription("Downloading file...")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        }

        downloadManager.enqueue(request)
        Toast.makeText(context, "Download Finished", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Download Failed", Toast.LENGTH_SHORT).show()
    }
}

fun previewImage(context: Context, fileUrl: String) {
    try {
        Log.d("ImagePreview", "Opening image in browser: $fileUrl")

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Ensures it opens in a new task
        context.startActivity(intent)

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Cannot open image", Toast.LENGTH_SHORT).show()
    }
}
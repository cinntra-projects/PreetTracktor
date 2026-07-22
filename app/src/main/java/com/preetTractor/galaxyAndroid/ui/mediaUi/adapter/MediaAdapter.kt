package com.preetTractor.galaxyAndroid.ui.mediaUi.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.preetTractor.galaxyAndroid.BuildConfig
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.databinding.VideoDocumentAdapterLayoutBinding
import com.preetTractor.galaxyAndroid.helper.Constant
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.Globals.setDynamicValueWithStringXml
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.moreUi.model.documentsModel.DocumentItemListModel


class MediaAdapter(
    private val context: Context, private var dataList: ArrayList<DocumentItemListModel.Data>
) : RecyclerView.Adapter<MediaAdapter.ViewHolder>() {

    private var onItemClickClickListener: ((DocumentItemListModel.Data, Int) -> Unit)? = null

    fun setOnItemClickListener(listener: (DocumentItemListModel.Data, Int) -> Unit) {
        onItemClickClickListener = listener
    }

    private var onItemPdfClickClickListener: ((DocumentItemListModel.Data, Int) -> Unit)? = null

    fun setOnItemPdfClickListener(listener: (DocumentItemListModel.Data, Int) -> Unit) {
        onItemPdfClickClickListener = listener
    }


    private var onItemImageClickClickListener: ((DocumentItemListModel.Data, Int) -> Unit)? = null

    fun setOnItemImageClickListener(listener: (DocumentItemListModel.Data, Int) -> Unit) {
        onItemImageClickClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = VideoDocumentAdapterLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val dataPos = dataList[position]
        var videoId = ""

        holder.binding.apply {

            linearShareButton.setOnClickListener {
                onItemClickClickListener?.let { click ->
                    click(dataPos, position)
                }
            }


            tvVideoTitle.text = dataPos.title
            tvVideoDescription.text = dataPos.description/* tvDocSize.text =
                 context.setDynamicValueWithStringXml(R.string.size_dynamic, dataPos.size)*/


            if (dataPos.file_type.equals("Video")) {
                tvDocSize.visibility = View.GONE
            } else {
                tvDocSize.visibility = View.VISIBLE
                tvDocSize.text = "#" + dataPos.tags
            }


            tvDocFormat.text =
                context.setDynamicValueWithStringXml(R.string.format_dynamic, dataPos.format)

            val filePath: String = BuildConfig.IMAGE_URL + dataPos.default_img_url

            if (dataPos.file.contains("www.youtube.com")) {
                videoId = AppConstants.getVideoIdFromYouTubeUrl(dataPos.file)!!
            }

            val thumbnailUrl = dataPos.thumbnail
            if (thumbnailUrl.isNotEmpty()) {
                ivIconPlay.visibility = View.VISIBLE
                if (dataPos.file_type.equals("Video", ignoreCase = true)) {
                    Glide.with(context).load(dataPos.thumbnail).centerCrop().into(icon)
                } else {
                    Glide.with(context).load(BuildConfig.IMAGE_URL + dataPos.thumbnail).centerCrop()
                        .into(icon)
                }

            } else {
                if (dataPos.file_type.equals(Constant.FILE_TYPE_DOCUMENT)) {
                    ivIconPlay.visibility = View.GONE
                    Glide.with(context).load(R.drawable.pdf_doc).centerInside().into(icon)
                } else if (dataPos.file_type.equals(Constant.FILE_TYPE_Image)) {
                    ivIconPlay.visibility = View.GONE
                    Glide.with(context).load(BuildConfig.IMAGE_URL + dataPos.file).centerInside()
                        .into(icon)

                } else {
                    ivIconPlay.visibility = View.VISIBLE
                    Glide.with(context).load(BuildConfig.IMAGE_URL + dataPos.thumbnail).centerCrop()
                        .into(icon)
                }


            }


        }


        val filePath: String = BuildConfig.IMAGE_URL + dataPos.file

        holder.itemView.setOnClickListener {

            if (dataPos.file_type.equals(Constant.FILE_TYPE_DOCUMENT)) {
                // Global.openLinkInBrowser(context, AppConstants.PDFURL + dataPos.file)

                onItemPdfClickClickListener?.let { click ->
                    click(dataPos, position)
                }

            } else if (dataPos.file_type.equals(Constant.FILE_TYPE_Image)) {
                onItemImageClickClickListener?.let { click ->
                    click(dataPos, position)
                }

            } else {
                if (dataPos.file.contains("www.youtube.com")) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(dataPos.file)
                    try {
                        intent.setPackage("com.google.android.youtube")
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Globals.warningMessage(context, "No Player Found")

                    }

                } else {
                    Globals.warningMessage(context, "No Player Found")
                }
            }


        }

    }


    override fun getItemCount(): Int {
        return dataList.size
    }


    inner class ViewHolder(val binding: VideoDocumentAdapterLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {}


}
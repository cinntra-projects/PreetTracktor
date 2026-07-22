package com.preetTractor.galaxyAndroid.ui.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.preetTractor.galaxyAndroid.BaseApplication
import com.preetTractor.galaxyAndroid.R
import com.preetTractor.galaxyAndroid.data.localdata.ImageModel
import com.preetTractor.galaxyAndroid.databinding.ItemImageCustomerBinding
import com.bumptech.glide.Glide


class ImagesAdapter(
    private var images: MutableList<ImageModel>,
    private val onDeleteClick: (ImageModel) -> Unit
) : RecyclerView.Adapter<ImagesAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(val binding: ItemImageCustomerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemImageCustomerBinding =
            ItemImageCustomerBinding.inflate(inflater, parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageModel = images[position]

        holder.binding.tvImageName.visibility = View.GONE
        holder.binding.ivImageCross.visibility = View.VISIBLE

        if (imageModel.uri != null) {
            val uriString: String = imageModel.uri.toString()
            if (uriString.contains("/document/document")) {
                Glide.with(BaseApplication.getInstance()).load(R.drawable.pdf_image).into(holder.binding.ivImage)
            } else {
                holder.binding.ivImage.setImageURI(imageModel.uri)
            }
        }
        holder.binding.tvImageName.text = imageModel.path
        holder.binding.ivImageCross.setOnClickListener {
            onDeleteClick(imageModel)
        }
    }

    override fun getItemCount(): Int = images.size

    fun deleteImage(imageModel: ImageModel) {
        val position = images.indexOf(imageModel)
        if (position != -1) {
            images.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
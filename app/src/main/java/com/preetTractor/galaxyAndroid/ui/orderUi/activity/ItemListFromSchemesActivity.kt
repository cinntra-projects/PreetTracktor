package com.preetTractor.galaxyAndroid.ui.orderUi.activity

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.JsonObject
import com.preetTractor.galaxyAndroid.apiHelper.ApiClient
import com.preetTractor.galaxyAndroid.apiHelper.ApisInterface
import com.preetTractor.galaxyAndroid.apiHelper.AppConstants
import com.preetTractor.galaxyAndroid.databinding.ActivityItemListFromSchemesBinding
import com.preetTractor.galaxyAndroid.helper.APiPayloadKeys
import com.preetTractor.galaxyAndroid.helper.Globals
import com.preetTractor.galaxyAndroid.helper.PrefsByShubh
import com.preetTractor.galaxyAndroid.mvvmSetUp.DefaultMainRepositories
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainRepos
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModel
import com.preetTractor.galaxyAndroid.mvvmSetUp.MainViewModelProvider
import com.preetTractor.galaxyAndroid.searchUi.ui.activity.SearchActivity

import com.preetTractor.galaxyAndroid.ui.orderUi.adapter.ItemListFromSchemeOrderRequestPagingAdapter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ItemListFromSchemesActivity : AppCompatActivity() {
      lateinit var binding: ActivityItemListFromSchemesBinding
      //lateinit var sessionManagement: SessionManagement
      lateinit var viewModel: MainViewModel
      var subCatId = ""
      var discount = ""

      lateinit var layoutManager: LinearLayoutManager
      lateinit var itemListingFromSubCategoryPagingAdapter: ItemListFromSchemeOrderRequestPagingAdapter

      private val baseText = "search for \"aloo\""
      private val newWords = listOf("Category", "Sub-Category", "Item")
      private var wordIndex = 0

      // Declare a global variable to hold the ObjectAnimator
      private var textAnimation: ObjectAnimator? = null

      private fun setUpViewModel() {
            val dispatchers: CoroutineDispatcher = Dispatchers.Main
            val mainRepos = DefaultMainRepositories() as MainRepos
            val fanxApi: ApisInterface = ApiClient().service(this)
            val viewModelProviderfactory =
                  MainViewModelProvider(application, mainRepos, dispatchers, fanxApi)
            viewModel = ViewModelProvider(this, viewModelProviderfactory)[MainViewModel::class.java]

      }

      private fun setupRecyclerViewInitial() {
            //todo bind adapter here--
            binding!!.rvDispatchOrder.layoutManager = layoutManager
            binding!!.rvDispatchOrder.adapter = itemListingFromSubCategoryPagingAdapter
      }


      override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityItemListFromSchemesBinding.inflate(layoutInflater)
            setContentView(binding.root)
            binding.tvSearchDoc.text = newWords[wordIndex]
            // Initial text setup
            updateText(newWords[wordIndex])

            // startAutoUpdateText()

            // Start the automatic update coroutine
            startAutoUpdate()



            setUpViewModel()
            layoutManager = LinearLayoutManager(this)
            subCatId = intent.getStringExtra("id").toString()
            discount = intent.getStringExtra("discount").toString()

            itemListingFromSubCategoryPagingAdapter =
                  ItemListFromSchemeOrderRequestPagingAdapter(discount,binding.tvCartCounter)
            setupRecyclerViewInitial()
            //  supportActionBar?.hide()!!

            binding.apply {

                  ibBack.setOnClickListener {
                        finish()
                  }

            }

            binding.linearSearch.setOnClickListener {
                  Intent(this, SearchActivity::class.java).also {
                        it.putExtra("where", "item")
                        it.putExtra("docId", "")
                        startActivity(it)
                  }
            }

            binding.ibSearch.setOnClickListener {
                  Intent(this, SearchActivity::class.java).also {
                        it.putExtra("where", "item")
                        it.putExtra("docId", "")
                        startActivity(it)
                  }
            }



            //sessionManagement = SessionManagement(this)

            binding.tvTitle.setText(PrefsByShubh.getCardName() /*sessionManagement.getCardName()*/)

            binding.btnProceedToBuy.setOnClickListener {

                  if (AppConstants.cartListForOrderRequest.isNotEmpty()) {
                        // binding.tvCartCounter.text = AppConstants.cartListForOrderRequest.size.toString()
                        Intent(this, CartActivity::class.java).also {

                              startActivity(it)
                        }
                  } else {
                         Globals.warningMessage(this, "Cart is Empty")
                  }

            }


      }


      private fun updateText(word: String) {
            var newWord = "\"$word\""
            // val newText = baseText.replace("aloo", word)
            val newText = baseText.replace("search for \"aloo\"", word)
            val spannableString = SpannableString(newText)

            // Find the start and end indices of the word to be bolded
            val startIndex = newText.indexOf(word)
            val endIndex = startIndex + word.length
            // var subString=baseText.substring(startIndex-1,endIndex)


            // Apply bold style to the word
            spannableString.setSpan(
                  StyleSpan(Typeface.BOLD),
                  startIndex,
                  endIndex,
                  Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            // Apply text change with animation
            animateTextChange(spannableString)
      }

      private fun animateTextChange(newText: SpannableString) {
            // Cancel any ongoing animation
            cancelAnimation()
            // Fade out and translate up the old hint
            val fadeOut = ObjectAnimator.ofFloat(binding.tvSearchDoc, "alpha", 1f, 0f)
            val translateUp = ObjectAnimator.ofFloat(binding.tvSearchDoc, "translationY", 0f, -50f)
            fadeOut.duration = 300
            translateUp.duration = 300

            fadeOut.addListener(object : Animator.AnimatorListener {
                  override fun onAnimationStart(animation: Animator) {}
                  override fun onAnimationEnd(animation: Animator) {
                        // Set the new hint
                        binding.tvSearchDoc.text = newText

                        // Reset the translation and fade in the new hint
                        binding.tvSearchDoc.translationY = 100f
                        val fadeIn = ObjectAnimator.ofFloat(binding.tvSearchDoc, "alpha", 0f, 1f)
                        val translateDown =
                              ObjectAnimator.ofFloat(binding.tvSearchDoc, "translationY", 100f, 0f)
                        fadeIn.duration = 1000
                        translateDown.duration = 1000

                        fadeIn.start()
                        translateDown.start()
                  }

                  override fun onAnimationCancel(animation: Animator) {}
                  override fun onAnimationRepeat(animation: Animator) {}
            })

            fadeOut.start()
            translateUp.start()
      }

      private fun cancelAnimation() {
            textAnimation?.cancel()
      }

      private fun startAutoUpdate() {
            val job = CoroutineScope(Dispatchers.Main).launch {
                  while (isActive) {
                        delay(2500) // Delay for 3 seconds
                        wordIndex = (wordIndex + 1) % newWords.size
                        updateText(newWords[wordIndex])
                  }
            }

            // Cancel the job when the activity is destroyed
            lifecycle.addObserver(LifecycleEventObserver { _, event ->
                  if (event == Lifecycle.Event.ON_DESTROY) {
                        job.cancel()
                  }
            })
      }


      override fun onResume() {
            super.onResume()

            if (AppConstants.cartListForOrderRequest.isNotEmpty()) {
                  binding.tvCartCounter.visibility = View.VISIBLE
                  binding.tvCartCounter.text = AppConstants.cartListForOrderRequest.size.toString()

            } else {
                  binding.tvCartCounter.visibility = View.INVISIBLE
            }

            binding.ivCollapseCart.setOnClickListener {

                  if (AppConstants.cartListForOrderRequest.isNotEmpty()) {
                        binding.tvCartCounter.text = AppConstants.cartListForOrderRequest.size.toString()
                        Intent(this, CartActivity::class.java).also {

                              startActivity(it)
                        }
                  } else {
                         Globals.warningMessage(this, "Cart is Empty")
                  }

            }

            if ( Globals.checkForInternet(this)) {
                  viewModel.schemeItemInDMS(JsonObject().apply {
                        addProperty(APiPayloadKeys.id, subCatId)
                  }, this)
                  subscribeToItemListObserver()
            }
      }

      private fun subscribeToItemListObserver() {
            viewModel.itemInScheme.observe(this, Event.EventObserver(onError = {
                   Globals.warningMessage(this, it)
                  binding.apply {
                        shimmerLayout.stopShimmer()
                        shimmerLayout.visibility = View.GONE
                        spinKitLoader.visibility = View.GONE
                        rvDispatchOrder.visibility = View.VISIBLE
                  }

            }, onLoading = {
                  binding.apply {
                        shimmerLayout.startShimmer()
                        shimmerLayout.visibility = View.VISIBLE
                        spinKitLoader.visibility = View.GONE
                        rvDispatchOrder.visibility = View.GONE
                  }
            }, { response ->
                  binding.apply {
                        shimmerLayout.stopShimmer()
                        shimmerLayout.visibility = View.GONE
                        spinKitLoader.visibility = View.GONE
                        rvDispatchOrder.visibility = View.VISIBLE
                  }
                  if (response.status.equals(200)) {
                        if (response.data.isNotEmpty()) {
                              itemListingFromSubCategoryPagingAdapter.clearAllData()
                              itemListingFromSubCategoryPagingAdapter.setItems(response.data)
                              itemListingFromSubCategoryPagingAdapter.notifyDataSetChanged()
                        } else {
                              itemListingFromSubCategoryPagingAdapter.setItems(response.data)
                              itemListingFromSubCategoryPagingAdapter.notifyDataSetChanged()
                        }


                  } else if (response.status.equals(201)) {
                         Globals.warningMessage(this, response.message)
                  } else {
                         Globals.warningMessage(this, "Something Went Wrong")
                  }

            }))
      }
}